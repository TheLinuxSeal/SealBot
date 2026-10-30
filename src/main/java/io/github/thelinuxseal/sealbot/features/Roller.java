package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;

import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.NORMAL;

public class Roller {
    public static final Random rand = new Random();

    private static final String[] SUITS = {"Hearts", "Diamonds", "Clubs", "Spades"};
    private static final String[] RANKS = {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

    public static final FunctionRule FLIP = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+flip\\s*$",Pattern.CASE_INSENSITIVE),
        (Matcher m) -> List.of(rand.nextBoolean() ? "Heads" : "Tails")
    );

    public static final FunctionRule DICE = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+dice\\s*$",Pattern.CASE_INSENSITIVE),
        (Matcher m) -> List.of(String.valueOf(rand.nextInt(6)+1))
    );

    public static final FunctionRule DDICE = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+2dice\\s*$",Pattern.CASE_INSENSITIVE),
        (Matcher m) -> {
            int a = rand.nextInt(6)+1;
            int b = rand.nextInt(6)+1;
            String x = a + " + " + b + " = " + (a+b);
            if (a==b) x += " DOUBLES!";
            return List.of(x);
        }
    );

    public static final FunctionRule CARD = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+card\\s*$",Pattern.CASE_INSENSITIVE),
            (Matcher m) -> List.of(RANKS[rand.nextInt(13)]+" of "+SUITS[rand.nextInt(4)])
    );
}
