package io.github.thelinuxseal.sealbot;

import java.util.List;
import java.util.regex.Matcher;
@Deprecated
public interface Feature {
    public List<String> trigger(Matcher m);
}
