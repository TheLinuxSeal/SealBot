package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.Permission;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringRule implements Rule {
    Permission pm;
    Pattern p;
    List<String> d;

    public StringRule(Permission perm, Pattern pattern, List<String> data){
        pm = perm;
        p = pattern;
        d = data;
    }
    public StringRule(Pattern pattern, List<String> data){
        pm = Permission.ROOT;
        p = pattern;
        d = data;
    }

    @Override
    public List<String> check(String msg) {
        Matcher m = p.matcher(msg);
        if (m.find()) {return d;}
        else {return null;}
    }

    @Override
    public Permission getPerm() {
        return pm;
    }
}
