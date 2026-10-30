package io.github.thelinuxseal.sealbot;

import io.github.thelinuxseal.sealbot.chatpatterns.Discord;
import io.github.thelinuxseal.sealbot.chatpatterns.PublicChat;
import io.github.thelinuxseal.sealbot.chatpatterns.Whisper;
import io.github.thelinuxseal.sealbot.features.*;
import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.perms.PermissionsStorage;
import io.github.thelinuxseal.sealbot.rule.ChatPattern;
import io.github.thelinuxseal.sealbot.rule.CooldownGroup;
import io.github.thelinuxseal.sealbot.rule.Rule;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StringUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(value = "sealbot", dist = Dist.CLIENT)
public class SealBot {

    /** Every reply starts with this tag; any incoming message containing it is ignored (loop guard). */
    public static final String TAG = "[SealBot {0} {1}] {2}";
    //private static final Pattern TAG_PATTERN = Pattern.compile("\\[SealBot [^\\]]+\\] ", Pattern.CASE_INSENSITIVE);

    public static final CooldownGroup MAIN_COOLDOWN_GROUP = new CooldownGroup();

    public static boolean enabled = true;

    public static final PermissionsStorage PERMS = new PermissionsStorage();

    public static final ChatPattern PUBLIC_CHAT = new PublicChat();
    public static final ChatPattern DISCORD = new Discord();
    public static final ChatPattern WHISPER = new Whisper();

    public static final List<ChatPattern> PATTERNS = List.of(
        PUBLIC_CHAT,
        DISCORD,
        WHISPER
    );

    // ---- Add your own rules here -------------------------------------------------
    public static final List<Rule> RULES = List.of(
            Settings.OFF,
            Settings.ON,

            Help.HELP,
            Help.HELPSPECIFIC,
            Help.CREDITS,
            Help.SOURCE,

            Perms.SET_BAN,
            Perms.SET_DEFAULT,
            Perms.SET_ROOT,
            Perms.STAT,

            //Settings.COOLDOWN,

            Calc.CALC,
            Echo.ECHO,
            Server.PING,
            UnitConvert.CONVERT,

            Roller.FLIP,
            Roller.DICE,
            Roller.DDICE,
            Roller.CARD,

            Answer.LIFE,

            Chatbot.CHATBOT
            //Answer.ARCHBTW
    );
    // ------------------------------------------------------------------------------

    // Only these two message shapes are accepted:
    //   <Bob> message
    //   Bob whispers to you: message


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
            StringBuilder sb = new StringBuilder(next.length());
            for (int i = 0; i < next.length(); i++) {
                char c = next.charAt(i);
                sb.append(StringUtil.isAllowedChatCharacter(c) ? c : '?');
            }
            next = sb.toString();
            if (next.startsWith("/")) mc.player.connection.sendCommand(next.substring(1));
            else mc.player.connection.sendChat(next);
            delay = TICKS_BETWEEN_MESSAGES;
        }
    }

    private void handle(String text) {
        text = text.trim();
        if (text.contains("[SealBot")) return; // our own (or another SealBot's) reply

        Matcher m;
        for (ChatPattern pattern : PATTERNS) {
            m = pattern.getPattern().matcher(text);
            if (m.matches()) {
                MessageData data = pattern.getData(m);

                int level = PERMS.map().getOrDefault(data.user(), Permission.NORMAL.ordinal());

                //System.out.println(data);

                for (Rule rule : RULES) {
                    if (!enabled && rule != Settings.ON) continue;
                    if (rule.getRuleData().patternData().getFirst(pattern).ordinal()>level) continue;
                    if (!rule.getRuleData().group().isOver(data.user())) continue;
                    //System.out.println("a");
                    List<String> result = rule.check(data);

                    if (result != null) {
                        queueMessage(result,data);

                        rule.getRuleData().group().setCooldown(data.user(),rule.getRuleData().patternData().getSecond(data.pattern()));
                    }
                }
                return;
            }
        }
    }

    public static void queueMessage(List<String> result, MessageData data){
        //System.out.println(result);
        if (result != null) {
            //System.out.println(result);
            for (String line : result) {
                if (outbox.size() >= MAX_QUEUE) break;
                String msg = data.pattern().format(data, line);
                outbox.add(msg.length() > MAX_LEN ? msg.substring(0, MAX_LEN) : msg);
            }
        }
    }

}