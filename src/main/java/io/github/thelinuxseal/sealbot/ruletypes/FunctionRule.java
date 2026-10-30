package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.MessageData;
import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.rule.Rule;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.ROOT;

public class FunctionRule implements Rule {
    RuleData r;
    Pattern p;
    Function<Matcher, List<String>> f;

    public FunctionRule(RuleData ruleData, Pattern pattern, Function<Matcher, List<String>> function){
        r = ruleData;
        p = pattern;
        f = function;
    }
    public FunctionRule(Pattern pattern, Function<Matcher, List<String>> function){
        r = new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        );
        p = pattern;
        f = function;
    }

    @Override
    public List<String> check(MessageData data) {
        Matcher m = p.matcher(data.body());
        if (m.find()) {return f.apply(m);}
        else {return null;}
    }

    @Override
    public RuleData getRuleData() {
        return r;
    }
}
