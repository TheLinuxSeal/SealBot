package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.NORMAL;

public class Echo {
    public static final FunctionRule ECHO = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+echo\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> List.of(m.group(1))
    );
}
