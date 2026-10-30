package io.github.thelinuxseal.sealbot.rule;

import io.github.thelinuxseal.sealbot.perms.Permission;

import static io.github.thelinuxseal.sealbot.SealBot.MAIN_COOLDOWN_GROUP;

public record RuleData(TriMap<ChatPattern, Permission, Long> patternData, CooldownGroup group) {
    public RuleData(TriMap<ChatPattern, Permission, Long> patternData) {this(patternData,MAIN_COOLDOWN_GROUP);}
}
