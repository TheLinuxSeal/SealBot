package io.github.thelinuxseal.sealbot.chatpatterns;

import io.github.thelinuxseal.sealbot.MessageData;
import io.github.thelinuxseal.sealbot.rule.ChatPattern;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.TAG;

public class Whisper implements ChatPattern {
    private static final Pattern pattern = Pattern.compile("^([\\w\\[\\]]+) whispers to you: (.+)$");
    private static final String WHISPER_CMD = "/msg {0} {1}";
    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public long getCooldown() {
        return 5000;
    }

    @Override
    public MessageData getData(Matcher matcher) {
        String user = matcher.group(1);
        String body = matcher.group(2);
        return new MessageData(body,user,this);
    }

    @Override
    public String format(MessageData data, String output) {
        return MessageFormat.format(WHISPER_CMD,data.user(),MessageFormat.format(TAG, ChatPattern.shortHash(data.user()+data.body(),2),"@"+data.user()+" (whisper)",output));
    }
}
