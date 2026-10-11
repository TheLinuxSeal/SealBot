package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings {
    public static final FunctionRule OFF = new FunctionRule(
        Permission.ROOT,
        Pattern.compile("\\bseal\\s+off\\s*$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {SealBot.enabled = false; return List.of("Disabled SealBot");}
    );

    public static final FunctionRule COOLDOWN = new FunctionRule(
            Permission.NORMAL,
            Pattern.compile("\\bseal\\s+cooldown\\s*$", Pattern.CASE_INSENSITIVE),
            (Matcher m) -> {return List.of("Cooldown is "+SealBot.COOLDOWN_NS);}
    );

}
