package io.github.thelinuxseal.sealbot;

import io.github.thelinuxseal.sealbot.featureclasses.Calc;
import io.github.thelinuxseal.sealbot.ruletypes.AdvancedRule;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;
import io.github.thelinuxseal.sealbot.ruletypes.Rule;
import io.github.thelinuxseal.sealbot.ruletypes.StringRule;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(value = "sealbot", dist = Dist.CLIENT)
public class SealBot {

    /** Every reply starts with this tag; any incoming message containing it is ignored (loop guard). */
    private static final String TAG = "[SealBot";

    private final static Calc CALC = new Calc();

    public static final PermissionsStorage PERMS = new PermissionsStorage();

    // ---- Add your own rules here -------------------------------------------------
    private static final List<Rule> RULES = List.of(
            new AdvancedRule(Permission.NORMAL, Pattern.compile("\\bseal\\s+calc\\s+(.+)$", Pattern.CASE_INSENSITIVE), CALC),
            new StringRule(
                    Permission.NORMAL,
                    Pattern.compile("\\bseal\\s+(?:help|list)\\s*$", Pattern.CASE_INSENSITIVE),
                    List.of(
                            "seal help -> prints this",
                            "seal calc <expr> -> evaluates <expr> with EvalEx",
                            "seal credits -> prints credits"
                    )
            ),
            new StringRule(
                    Permission.NORMAL,
                    Pattern.compile("\\bseal\\s+credits\\s*$", Pattern.CASE_INSENSITIVE),
                    List.of(
                            "TheLinuxSeal - SealBot",
                            "Ezylang - EvalEx"
                    )
            ),
            new StringRule(
                    Permission.NORMAL,
                    Pattern.compile("\\bseal\\s+answer\\b.*\\blife\\b", Pattern.CASE_INSENSITIVE),
                    List.of("42")
            ),
            new FunctionRule(
                    Permission.NORMAL,
                    Pattern.compile("\\bseal\\s+echo\\s+(.+)$", Pattern.CASE_INSENSITIVE),
                    (Matcher m) -> List.of(m.group(1))
            ),
            new FunctionRule(
                    Permission.ROOT,
                    Pattern.compile("\\bseal\\s+perm\\s+ban\\s+(.+)$", Pattern.CASE_INSENSITIVE),
                    (Matcher m) -> {
                        PERMS.map().put(m.group(1),Permission.BANNED.ordinal());
                        return List.of("Banned "+m.group(1));
                    }
            ),
            new FunctionRule(
                    Permission.ROOT,
                    Pattern.compile("\\bseal\\s+perm\\s+default\\s+(.+)$", Pattern.CASE_INSENSITIVE),
                    (Matcher m) -> {
                        PERMS.map().put(m.group(1),Permission.NORMAL.ordinal());
                        return List.of("Defaulted "+m.group(1));
                    }
            ),
            new FunctionRule(
                    Permission.ROOT,
                    Pattern.compile("\\bseal\\s+perm\\s+root\\s+(.+)$", Pattern.CASE_INSENSITIVE),
                    (Matcher m) -> {
                        PERMS.map().put(m.group(1),Permission.ROOT.ordinal());
                        return List.of("Rooted "+m.group(1));
                    }
            ),
            new FunctionRule(
                    Permission.BANNED,
                    Pattern.compile("\\bseal\\s+perm\\s+stat\\s+(.+)$", Pattern.CASE_INSENSITIVE),
                    (Matcher m) -> {

                        //PERMS.map().put(m.group(1),Permission.ROOT.ordinal())
                        int x = PERMS.map().getOrDefault(m.group(1), Permission.NORMAL.ordinal());
                        if (x == 2) return List.of(m.group(1)+" is Root");
                        if (x == 0) return List.of(m.group(1)+" is Banned");
                        else return List.of(m.group(1)+" is Default");
                    }
            ),
            new StringRule(
                    Permission.NORMAL,
                    Pattern.compile(".*btw.*", Pattern.CASE_INSENSITIVE),
                    List.of("I use arch btw!")
            )
    );
    // ------------------------------------------------------------------------------

    // Only these two message shapes are accepted:
    //   <Bob> message
    //   Bob whispers to you: message
    private static final Pattern PUBLIC_CHAT = Pattern.compile("^<(\\w{1,16})> (.+)$");
    private static final Pattern WHISPER = Pattern.compile("^(\\w{1,16}) whispers to you: (.+)$");

    private static final int TICKS_BETWEEN_MESSAGES = 12;   // ~0.6s, avoids spam kicks
    private static final int MAX_QUEUE = 6;                 // drop extra replies instead of spamming
    private static final int MAX_LEN = 256;                 // Minecraft's chat length limit

    private final Deque<String> outbox = new ArrayDeque<>();
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
            mc.player.connection.sendChat(next);
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
        String prefix;
        if (chat.matches()) {
            user = chat.group(1);
            body = chat.group(2);
            prefix = TAG + " @" + user + "] ";
        } else if (whisper.matches()) {
            user = whisper.group(1);
            body = whisper.group(2);
            prefix = TAG + " @" + user +  " '" + body + "'] ";
        } else {
            return; // not a player message
        }



        for (Rule rule : RULES) {
            int level = PERMS.map().getOrDefault(user, Permission.NORMAL.ordinal());
            if (level < rule.getPerm().ordinal()) continue;
            List<String> data = rule.check(body);
            if (data != null) {
            for (String line : data) {
                    if (outbox.size() >= MAX_QUEUE) break;
                    String msg = prefix + line;
                    outbox.add(msg.length() > MAX_LEN ? msg.substring(0, MAX_LEN) : msg);
                }
                return; // first matching rule wins
            }
        }
    }
}