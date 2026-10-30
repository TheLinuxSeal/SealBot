package io.github.thelinuxseal.sealbot;

import io.github.thelinuxseal.sealbot.rule.ChatPattern;

public record MessageData(String body, String user, ChatPattern pattern){};
