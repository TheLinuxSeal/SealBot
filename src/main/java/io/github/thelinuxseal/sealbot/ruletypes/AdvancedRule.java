package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.Feature;
import io.github.thelinuxseal.sealbot.Permission;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdvancedRule implements Rule{
    Permission pm;
    Pattern p;
    Feature f;


    public AdvancedRule(Permission perm, Pattern pattern, Feature feature){
        pm = perm;
        p = pattern;
        f = feature;
    }
    public AdvancedRule(Pattern pattern, Feature feature){
        pm = Permission.ROOT;
        p = pattern;
        f = feature;
    }

    @Override
    public List<String> check(String msg) {
        Matcher m = p.matcher(msg);
        if (m.find()) {return f.trigger(m);}
        else {return null;}
    }

    @Override
    public Permission getPerm() {
        return pm;
    }
}
