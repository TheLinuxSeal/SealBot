package io.github.thelinuxseal.sealbot.rule;

import io.github.thelinuxseal.sealbot.Permission;

public record RuleData(Permission publicChat, Permission whisper, long publicChatCooldown, long whisperCooldown) {
    public static final long PUBLIC_CHAT_COOLDOWN_NS = 45_000_000_000L;
    public static final long WHISPER_COOLDOWN_NS = 5_000_000_000L;

    public RuleData(Permission publicChat, Permission whisper, long both){
        this(publicChat,whisper,both,both);
    }

    public RuleData(Permission publicChat, Permission whisper){
        this(publicChat,whisper,PUBLIC_CHAT_COOLDOWN_NS,WHISPER_COOLDOWN_NS);
    }
    
    public RuleData(Permission both, long publicChatCooldown, long whisperCooldown){
        this(both,both,publicChatCooldown,whisperCooldown);
    }

    public RuleData(Permission both0, long both1){
        this(both0,both0,both1,both1);
    }

    public RuleData(Permission both){
        this(both,both,PUBLIC_CHAT_COOLDOWN_NS,WHISPER_COOLDOWN_NS);
    }

    public RuleData(){
        this(Permission.ROOT,Permission.ROOT,PUBLIC_CHAT_COOLDOWN_NS,WHISPER_COOLDOWN_NS);
    }

}
