package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.rule.Rule;
import io.github.thelinuxseal.sealbot.rule.RuleData;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        r = new RuleData(Permission.ROOT);
        p = pattern;
        d = data;
    }

    @Override
    public List<String> check(String msg, SealBot.MessageData data) {
        Matcher m = p.matcher(msg);
        if (m.find()) {return d;}
        else {return null;}
    }

    @Override
    public RuleData getRuleData() {
        return r;
    }
}
