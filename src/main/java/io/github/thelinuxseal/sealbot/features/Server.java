package io.github.thelinuxseal.sealbot.features;

import io.github.thelinuxseal.sealbot.perms.Permission;
import io.github.thelinuxseal.sealbot.rule.RuleData;
import io.github.thelinuxseal.sealbot.rule.TriMap;
import io.github.thelinuxseal.sealbot.ruletypes.FunctionRule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.github.thelinuxseal.sealbot.SealBot.*;
import static io.github.thelinuxseal.sealbot.SealBot.WHISPER;
import static io.github.thelinuxseal.sealbot.perms.Permission.NORMAL;

public class Server {
    public static final FunctionRule PING = new FunctionRule(
        new RuleData(
            TriMap.of(
                PUBLIC_CHAT, NORMAL, PUBLIC_CHAT.getCooldown(),
                DISCORD, NORMAL, DISCORD.getCooldown(),
                WHISPER, NORMAL, WHISPER.getCooldown()
            )
        ),
        Pattern.compile("\\bseal\\s+ping\\s+(.+)$", Pattern.CASE_INSENSITIVE),
        (Matcher m) -> List.of(getPing(m.group(1)))
    );

    private static String getPing(String uname){
        ClientPacketListener conn = Minecraft.getInstance().getConnection();
        if (conn==null) return "No Connection";
        PlayerInfo inf = conn.getPlayerInfo(uname);
        if (inf == null) return "No Player";
        return uname + ": " + inf.getLatency() + " ms";
    }
}
