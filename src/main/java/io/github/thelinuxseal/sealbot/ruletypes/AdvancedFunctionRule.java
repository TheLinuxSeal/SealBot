package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.MessageData;
import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.rule.Rule;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;

import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.ROOT;

public class AdvancedFunctionRule implements Rule {
    RuleData r;
    Pattern p;
    BiFunction<Matcher, MessageData, List<String>> f;

    public AdvancedFunctionRule(RuleData ruleData, Pattern pattern, BiFunction<Matcher, MessageData, List<String>> function){
        r = ruleData;
        p = pattern;
        f = function;
    }
    public AdvancedFunctionRule(Pattern pattern, BiFunction<Matcher, MessageData, List<String>> function){
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
        if (m.find()) {return f.apply(m,data);}
        else {return null;}
    }

    @Override
    public RuleData getRuleData() {
        return r;
    }
}
