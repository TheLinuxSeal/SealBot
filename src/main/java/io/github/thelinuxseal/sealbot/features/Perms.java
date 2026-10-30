package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.DISCORD;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.*;

public class Perms {
    public static final FunctionRule SET_BAN = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+perm\\s+ban\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
        PERMS.map().put(m.group(1), BANNED.ordinal());
        return List.of("Banned "+m.group(1));
    }
    );

    public static final FunctionRule SET_DEFAULT = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+perm\\s+default\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
            PERMS.map().put(m.group(1),Permission.NORMAL.ordinal());
            return List.of("Defaulted "+m.group(1));
        }
    );

    public static final FunctionRule SET_ROOT = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+perm\\s+root\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
            PERMS.map().put(m.group(1), ROOT.ordinal());
            return List.of("Rooted "+m.group(1));
        }
    );

    public static final FunctionRule STAT = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, BANNED, PUBLIC_CHAT.getCooldown(),
                DISCORD, BANNED, DISCORD.getCooldown(),
                WHISPER, BANNED, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+perm\\s+stat\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
            int x = PERMS.map().getOrDefault(m.group(1), Permission.NORMAL.ordinal());
            if (x == 2) return List.of(m.group(1)+" is Root");
            if (x == 0) return List.of(m.group(1)+" is Banned");
            else return List.of(m.group(1)+" is Default");
        }
    );
}
