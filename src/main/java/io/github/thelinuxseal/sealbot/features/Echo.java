package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Echo {
    public static final FunctionRule ECHO = new FunctionRule(
        Permission.NORMAL,
        Pattern.compile("\\bseal\\s+echo\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> List.of(m.group(1))
    );
}
