package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.MessageData;
import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.rule.Rule;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.ROOT;

public class StringRule implements Rule {
    RuleData r;
    Pattern p;
    List<String> d;

    public StringRule(RuleData ruleData, Pattern pattern, List<String> data){
        r = ruleData;
        p = pattern;
        d = data;
    }
    public StringRule(Pattern pattern, List<String> data){
        r = new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        );
        p = pattern;
        d = data;
    }

    @Override
    public List<String> check(MessageData data) {
        Matcher m = p.matcher(data.body());
        if (m.find()) {return d;}
        else {return null;}
    }

    @Override
    public RuleData getRuleData() {
        return r;
    }
}
