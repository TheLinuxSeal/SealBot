package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.ruletypes.StringRule;

import java.util.List;
import java.util.regex.Pattern;

public class Answer {
    public static final StringRule LIFE = new StringRule(
        Permission.NORMAL,
        Pattern.compile("\\bseal\\s+answer\\b.*\\blife\\b", Pattern.CASE_INSENSITIVE),
        List.of("42")
    );

    public static final StringRule ARCHBTW = new StringRule(
        Permission.NORMAL,
        Pattern.compile(".*btw.*", Pattern.CASE_INSENSITIVE),
        List.of("\"btw\" HOLY ARCH LINUX REFERENCE")
    );
}
