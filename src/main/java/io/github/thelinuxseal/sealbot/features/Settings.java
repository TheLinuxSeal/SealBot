package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.ROOT;

public class Settings {
    public static final FunctionRule OFF = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+off\\s*$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {SealBot.enabled = false; return List.of("Disabled SealBot");}
    );

    public static final FunctionRule ON = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+on\\s*$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {SealBot.enabled = true; return List.of("Enabled SealBot");}
    );

    /*public static final FunctionRule COOLDOWN = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+cooldown\\s*$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {return List.of("Public chat: "+SealBot.CHAT_COOLDOWN_NS/1_000_000_000+" seconds, whisper: "+SealBot.WHISPER_COOLDOWN_NS/1_000_000_000+" seconds");}
    );*/

}
