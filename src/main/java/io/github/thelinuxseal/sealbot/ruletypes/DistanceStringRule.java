package io.github.thelinuxseal.sealbot.ruletypes;

import io.github.thelinuxseal.sealbot.MessageData;
import io.github.thelinuxseal.sealbot.rule.Rule;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.perms.Permission.ROOT;

/**
 * Like StringRule, but once the pattern is found the message must also contain every keyword, typos allowed.
 * Keywords are space-separated, can appear in any order, and "say|sound" means either word.
 * Example: new DistanceStringRule(ASK, "capital france", List.of("Oh oui oui oui. It is Paris."))
 * matches "seal answer whats the capitol of frnace?" but not "seal answer what is the capital of spain?".
 */
public class DistanceStringRule implements Rule {
    RuleData r;
    Pattern p;
    String[][] k; // one entry per keyword, holding its alternatives
    List<String> d;

    public DistanceStringRule(RuleData ruleData, Pattern pattern, String keywords, List<String> data){
        r = ruleData;
        p = pattern;
        String[] words = keywords.toLowerCase(Locale.ROOT).trim().split("\\s+");
        k = new String[words.length][];
        for (int i = 0; i < words.length; i++) k[i] = words[i].split("\\|");
        d = data;
    }
    /** Open to everyone, unlike StringRule's ROOT-only default. */
    public DistanceStringRule(Pattern pattern, String keywords, List<String> data){
        this(new RuleData(
            TriMap.of(
                PUBLIC_CHAT, ROOT, PUBLIC_CHAT.getCooldown(),
                DISCORD, ROOT, DISCORD.getCooldown(),
                WHISPER, ROOT, WHISPER.getCooldown()
            )
        ), pattern, keywords, data);
    }

    @Override
    public List<String> check(MessageData data) {
        if (!p.matcher(data.body()).find()) return null;
        String[] words = data.body().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", " ").trim().split(" ");
        for (String[] alternatives : k) {
            if (!hasAny(words, alternatives)) return null;
        }
        return d;
    }

    @Override
    public RuleData getRuleData() {
        return r;
    }

    private static boolean hasAny(String[] words, String[] alternatives) {
        for (String keyword : alternatives) {
            if (hasWord(words, keyword)) return true;
        }
        return false;
    }

    /**
     * Typos allowed per keyword: none up to 3 letters (so "how" never counts as "cow"), 1 up to 7, then 2.
     * A trailing "s" on the player's word is also tried without it, so "cows" counts as "cow".
     */
    private static boolean hasWord(String[] words, String keyword) {
        int allowed = keyword.length() <= 3 ? 0 : keyword.length() <= 7 ? 1 : 2;
        for (String w : words) {
            if (distance(w, keyword) <= allowed) return true;
            if (w.endsWith("s") && distance(w.substring(0, w.length() - 1), keyword) <= allowed) return true;
        }
        return false;
    }

    /** Edit distance where swapping two neighbouring letters ("frnace") counts as one typo, not two. */
    static int distance(String a, String b) {
        int[][] t = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) t[i][0] = i;
        for (int j = 0; j <= b.length(); j++) t[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                t[i][j] = Math.min(t[i - 1][j - 1] + cost, Math.min(t[i - 1][j] + 1, t[i][j - 1] + 1));
                if (i > 1 && j > 1 && a.charAt(i - 1) == b.charAt(j - 2) && a.charAt(i - 2) == b.charAt(j - 1))
                    t[i][j] = Math.min(t[i][j], t[i - 2][j - 2] + 1);
            }
        }
        return t[a.length()][b.length()];
    }
}
