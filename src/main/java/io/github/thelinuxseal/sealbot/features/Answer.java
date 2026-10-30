package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.chatpatterns.PublicChat;
import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;
import io.github.thelinuxseal.sealbot.ruletypes.StringRule;

import java.util.List;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.perms.Permission.NORMAL;

public class Answer {
    public static final StringRule LIFE = new StringRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+answer\\b.*\\blife\\b.*\\b", Pattern.CASE_INSENSITIVE),
        List.of("42")
    );

    public static final StringRule ARCHBTW = new StringRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile(".*btw.*", Pattern.CASE_INSENSITIVE),
        List.of("\"btw\" HOLY ARCH LINUX REFERENCE")
    );
}
