package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.PERMS;

public class Perms {
    public static final FunctionRule BAN = new FunctionRule(
        Permission.ROOT,
        Pattern.compile("\\bseal\\s+perm\\s+ban\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
        PERMS.map().put(m.group(1),Permission.BANNED.ordinal());
        return List.of("Banned "+m.group(1));
    }
    );

    public static final FunctionRule DEFAULT = new FunctionRule(
        Permission.ROOT,
        Pattern.compile("\\bseal\\s+perm\\s+default\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
            PERMS.map().put(m.group(1),Permission.NORMAL.ordinal());
            return List.of("Defaulted "+m.group(1));
        }
    );

    public static final FunctionRule ROOT = new FunctionRule(
        Permission.ROOT,
        Pattern.compile("\\bseal\\s+perm\\s+root\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
            PERMS.map().put(m.group(1),Permission.ROOT.ordinal());
            return List.of("Rooted "+m.group(1));
        }
    );

    public static final FunctionRule STAT = new FunctionRule(
        Permission.BANNED,
        Pattern.compile("\\bseal\\s+perm\\s+stat\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
            int x = PERMS.map().getOrDefault(m.group(1), Permission.NORMAL.ordinal());
            if (x == 2) return List.of(m.group(1)+" is Root");
            if (x == 0) return List.of(m.group(1)+" is Banned");
            else return List.of(m.group(1)+" is Default");
        }
    );
}
