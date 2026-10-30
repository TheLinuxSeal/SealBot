package io.github.thelinuxseal.sealbot.rule;

import io.github.thelinuxseal.sealbot.MessageData;

import java.util.List;

public interface Rule {

    public List<String> check(MessageData data);

    RuleData getRuleData();
}
