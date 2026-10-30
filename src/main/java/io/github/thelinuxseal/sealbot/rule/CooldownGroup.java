package io.github.thelinuxseal.sealbot.rule;

import io.github.thelinuxseal.sealbot.SealBot;
import io.github.thelinuxseal.sealbot.perms.Permission;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class CooldownGroup {
    private final Map<String, Long> cooldowns = new HashMap<>();
    public void setCooldown(String player, long cooldown) {
        if (SealBot.PERMS.map().getOrDefault(player,Permission.NORMAL.ordinal()) >= Permission.ROOT.ordinal()) return;
        cooldowns.put(player,System.currentTimeMillis()+cooldown);
    }
    public  boolean isOver(String player) {
        //System.out.println(MessageFormat.format("{0}, {1}, {2}",player,System.currentTimeMillis(),cooldowns.getOrDefault(player,0L)));
        return System.currentTimeMillis()>cooldowns.getOrDefault(player,0L);
    }
}
