package io.github.thelinuxseal.sealbot.rule;

import io.github.thelinuxseal.sealbot.SealBot;

import java.util.List;

public interface Rule {

    public List<String> check(String msg, SealBot.MessageData data);

    RuleData getRuleData();
}
