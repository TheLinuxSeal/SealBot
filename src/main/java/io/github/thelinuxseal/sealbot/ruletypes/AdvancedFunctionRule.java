package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.rule.Rule;
import io.github.thelinuxseal.sealbot.rule.RuleData;

import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedFunctionRule implements Rule {
    RuleData r;
    Pattern p;
    BiFunction<Matcher, SealBot.MessageData, List<String>> f;

    public AdvancedFunctionRule(RuleData ruleData, Pattern pattern, BiFunction<Matcher, SealBot.MessageData, List<String>> function){
        r = ruleData;
        p = pattern;
        f = function;
    }
    public AdvancedFunctionRule(Pattern pattern, BiFunction<Matcher, SealBot.MessageData, List<String>> function){
        r = new RuleData(Permission.ROOT);
        p = pattern;
        f = function;
    }

    @Override
    public List<String> check(String msg, SealBot.MessageData data) {
        Matcher m = p.matcher(msg);
        if (m.find()) {return f.apply(m,data);}
        else {return null;}
    }

    @Override
    public RuleData getRuleData() {
        return r;
    }
}
