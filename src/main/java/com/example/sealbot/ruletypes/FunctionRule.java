package com.example.sealbot.ruletypes;

import com.example.sealbot.Permission;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionRule implements Rule{
    Permission pm;
    Pattern p;
    Function<Matcher, List<String>> f;

    public FunctionRule(Permission perm, Pattern pattern, Function<Matcher, List<String>> function){
        pm = perm;
        p = pattern;
        f = function;
    }
    public FunctionRule(Pattern pattern, Function<Matcher, List<String>> function){
        pm = Permission.ROOT;
        p = pattern;
        f = function;
    }

    @Override
    public List<String> check(String msg) {
        Matcher m = p.matcher(msg);
        if (m.find()) {return f.apply(m);}
        else {return null;}
    }

    @Override
    public Permission getPerm() {
        return pm;
    }
}
