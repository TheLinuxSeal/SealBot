package io.github.thelinuxseal.sealbot;

import io.github.thelinuxseal.sealbot.features.*;
import io.github.thelinuxseal.sealbot.rule.Rule;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(value = "sealbot", dist = Dist.CLIENT)
public class SealBot {

    /** Every reply starts with this tag; any incoming message containing it is ignored (loop guard). */
    private static final String TAG = "[SealBot";

    private static final String WHISPER_CMD = "msg";

    public static final long CHAT_COOLDOWN_NS = 45_000_000_000L;
    public static final long WHISPER_COOLDOWN_NS = 5_000_000_000L;

    public static boolean enabled = true;

    public static final PermissionsStorage PERMS = new PermissionsStorage();

    // ---- Add your own rules here -------------------------------------------------
    private static final List<Rule> RULES = List.of(
            Settings.OFF,
            Settings.ON,

            Help.HELP,
            Help.HELPSPECIFIC,
            Help.CREDITS,

            Perms.BAN,
            Perms.DEFAULT,
            Perms.ROOT,
            Perms.STAT,

            Settings.COOLDOWN,

            Calc.CALC,
            Echo.ECHO,
            Server.PING,

            Chatbot.CHATBOT,

            Roller.FLIP,
            Roller.DICE,
            Roller.DDICE,
            Roller.CARD,

            Answer.LIFE
            //Answer.ARCHBTW
    );
    // ------------------------------------------------------------------------------

    // Only these two message shapes are accepted:
    //   <Bob> message
    //   Bob whispers to you: message
    private static final Pattern PUBLIC_CHAT = Pattern.compile("^<(\\w{1,16})> (.+)$");
    private static final Pattern WHISPER = Pattern.compile("^(\\w{1,16}) whispers to you: (.+)$");

    private static final Map<String, Long> lastChat = new HashMap<>();
    private static final Map<String, Long> lastWhisper = new HashMap<>();

    private static final int TICKS_BETWEEN_MESSAGES = 12;   // ~0.6s, avoids spam kicks
    private static final int MAX_QUEUE = 6;                 // drop extra replies instead of spamming
    private static final int MAX_LEN = 256;                 // Minecraft's chat length limit

    private static final Deque<String> outbox = new ArrayDeque<>();
    private int delay = 0;

    public SealBot() {
        PERMS.load();                                                    // on startup
        Runtime.getRuntime().addShutdownHook(new Thread(PERMS::save));
        NeoForge.EVENT_BUS.addListener(this::onChat);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onChat(ClientChatReceivedEvent event) {
        if (event instanceof ClientChatReceivedEvent.System sys && sys.isOverlay()) return;
        handle(event.getMessage().getString());
    }

    // Drain the outbox one message at a time.
    private void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            outbox.clear();
            return;
        }
        if (delay > 0) {
            delay--;
            return;
        }
        String next = outbox.poll();
        if (next != null) {
            if (next.startsWith("/")) mc.player.connection.sendCommand(next.substring(1));
            else mc.player.connection.sendChat(next);
            delay = TICKS_BETWEEN_MESSAGES;
        }
    }

    private void handle(String text) {
        text = text.trim();
        if (text.contains(TAG)) return; // our own (or another SealBot's) reply

        Matcher chat = PUBLIC_CHAT.matcher(text);
        Matcher whisper = WHISPER.matcher(text);

        String user;
        String body;
        String suffix;
        boolean isWhisper;
        if (chat.matches()) {
            user = chat.group(1);
            body = chat.group(2);
            suffix = "@" + user;
            isWhisper = false;
        } else if (whisper.matches()) {
            user = whisper.group(1);
            body = whisper.group(2);
            suffix = "@" + user +  " (whisper)";
            isWhisper = true;
        } else {
            return; // not a player message
        }

        Map<String, Long> last = isWhisper ? lastWhisper : lastChat;
        long cooldown = isWhisper ? WHISPER_COOLDOWN_NS : CHAT_COOLDOWN_NS;
        Long prev = last.get(user);
        if (prev != null && System.nanoTime() - prev < cooldown) return;

        int level = PERMS.map().getOrDefault(user, Permission.NORMAL.ordinal());

        if (!enabled){
            List<String> data = Settings.ON.check(body, new MessageData(body,user,isWhisper));
            if (data != null) {
                String hash = shortHash(user+body,2);
                for (String line : data) {
                    if (outbox.size() >= MAX_QUEUE) break;
                    String msg = TAG+" "+hash+" "+suffix+"] "+line;
                    if (isWhisper) msg = "/"+WHISPER_CMD+" "+user+" "+msg;
                    outbox.add(msg.length() > MAX_LEN ? msg.substring(0, MAX_LEN) : msg);
                }
                last.put(user, System.nanoTime());
            }
            return;
        };

        for (Rule rule : RULES) {
            if (!isWhisper && level < rule.getRuleData().publicChat().ordinal()) continue;
            if (isWhisper && level < rule.getRuleData().whisper().ordinal()) continue;
            List<String> data = rule.check(body, new MessageData(body,user,isWhisper));
            if (data != null) {
                String hash = shortHash(user+body,2);
                for (String line : data) {
                    if (outbox.size() >= MAX_QUEUE) break;
                    String msg = TAG+" "+hash+" "+suffix+"] "+line;
                    if (isWhisper) msg = "/"+WHISPER_CMD+" "+user+" "+msg;
                    outbox.add(msg.length() > MAX_LEN ? msg.substring(0, MAX_LEN) : msg);
                }
                last.put(user, System.nanoTime());
                return; // first matching rule wins
            }
        }
    }

    public static void queueMessage(List<String> text, MessageData data){
        String suffix;
        if (data.isWhisper()) suffix = "@" + data.user() +  " (whisper)";
        else suffix = "@" + data.user();
        String hash = shortHash(data.user()+data.body(),2);
        for (String line : text) {
            if (outbox.size() >= MAX_QUEUE) break;
            String msg = TAG+" "+hash+" "+suffix+"] "+line;
            if (data.isWhisper()) msg = "/"+WHISPER_CMD+" "+data.user()+" "+msg;
            outbox.add(msg.length() > MAX_LEN ? msg.substring(0, MAX_LEN) : msg);
        }
    }


    private static String shortHash(String input, int bytes) {   // bytes=2 -> 4 hex chars, bytes=4 -> 8
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(d, 0, bytes);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);   // SHA-256 is always available
        }
    }

    public static record MessageData(String body, String user, boolean isWhisper){};
}