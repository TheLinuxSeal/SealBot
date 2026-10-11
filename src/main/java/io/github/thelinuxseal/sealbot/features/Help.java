package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.ruletypes.StringRule;

import java.util.List;
import java.util.regex.Pattern;

public class Help {
    public static final StringRule HELP =  new StringRule(
        Permission.NORMAL,
        Pattern.compile("\\bseal\\s+(?:help|list)\\s*$", Pattern.CASE_INSENSITIVE),
            List.of(
                    "seal help -> prints this",
                    "seal calc <expr> -> evaluates <expr> with EvalEx",
                    "seal credits -> prints credits"
                )
            );

    public static final StringRule CREDITS = new StringRule(
        Permission.NORMAL,
        Pattern.compile("\\bseal\\s+credits\\s*$", Pattern.CASE_INSENSITIVE),
            List.of(
                "TheLinuxSeal - SealBot",
                "Ezylang - EvalEx"
                )
            );
}
