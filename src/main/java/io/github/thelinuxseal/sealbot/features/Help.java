package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.Permission;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;
import io.github.thelinuxseal.sealbot.ruletypes.StringRule;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Help {

    private static final Map<String, List<String>> TOPICS = new TreeMap<>();

    static {
        TOPICS.put("help",       List.of("help - prints the available commands", "help <cmd> - prints info about <cmd>"));
        TOPICS.put("credits",    List.of("credits - prints credits"));
        TOPICS.put("calc",       List.of("calc <expr> - evaluates <expr> with EvalEx"));
        TOPICS.put("echo",       List.of("echo <text> - repeats <text>"));
        TOPICS.put("perm stat",  List.of("perm stat <username> - prints the permission status of <username>"));
        TOPICS.put("flip",       List.of("flip - flips a coin"));
        TOPICS.put("dice",       List.of("dice - rolls a die"));
        TOPICS.put("ddice",      List.of("ddice - rolls 2 dice"));
        TOPICS.put("doubledice", List.of("doubledice - rolls 2 dice"));
        TOPICS.put("2dice",      List.of("2dice - rolls 2 dice"));
        TOPICS.put("ping",       List.of("ping <username> - prints the ping of <username>"));
        TOPICS.put("answer",     List.of("answer <question> - answers <question>"));
    }

    public static final StringRule HELP =  new StringRule(
        Permission.NORMAL,
        Pattern.compile("\\bseal\\s+(?:help|list)\\s*$", Pattern.CASE_INSENSITIVE),
        List.of("Available commands: "+String.join(", ",TOPICS.keySet()))
    );

    public static final FunctionRule HELPSPECIFIC =  new FunctionRule(
        Permission.NORMAL,
        Pattern.compile("\\bseal\\s+help\\s+(.*)\\s*$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> getHelpAbout(m.group(1))
    );

    public static final StringRule CREDITS = new StringRule(
        Permission.NORMAL,
        Pattern.compile("\\bseal\\s+credits\\s*$", Pattern.CASE_INSENSITIVE),
        List.of(
            "TheLinuxSeal - SealBot",
            "Ezylang - EvalEx",
            "Konstantin Herud - java-llama.cpp",
            "The ggml authors - llama.cpp",
            "Alibaba Cloud - Qwen2.5-0.5B-Instruct"
        )
    );


    public static List<String> getHelpAbout(String what) {
        return TOPICS.getOrDefault(what.trim().toLowerCase(Locale.ROOT), List.of("Unknown command"));
    }
}
