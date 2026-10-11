package io.github.thelinuxseal.sealbot;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Persistent Map<String, Integer>, stored as "key=value" lines in config/sealbot-data.txt. */
public final class PermissionsStorage {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Path file = FMLPaths.CONFIGDIR.get().resolve("sealbot-perms.txt");

    // Concurrent because the save hook runs on a different thread than the game.
    private final Map<String, Integer> map = new ConcurrentHashMap<>();

    public Map<String, Integer> map() {
        return map;
    }

    public void load() {
        if (!Files.exists(file)) {
            map.put("TheLinuxSeal", 2);
            return;
        };
        try {
            for (String line : Files.readAllLines(file)) {
                int i = line.lastIndexOf('=');
                if (i <= 0) continue; // blank or malformed line
                try {
                    map.put(line.substring(0, i), Integer.parseInt(line.substring(i + 1).trim()));
                } catch (NumberFormatException ignored) {
                    // skip lines whose value isn't an int
                }
            }
        } catch (IOException e) {
            LOGGER.error("SealBot: failed to load {}", file, e);
        }
        map.put("thelinuxseal", Permission.ROOT.ordinal());
    }

    public void save() {
        try {
            Files.createDirectories(file.getParent());

            List<String> lines = new ArrayList<>();
            map.forEach((k, v) -> lines.add(k.replace('\n', ' ').replace('\r', ' ') + "=" + v));

            // write to a temp file first so a crash mid-write can't corrupt the real one
            Path tmp = file.resolveSibling(file.getFileName() + ".tmp");
            Files.write(tmp, lines);
            Files.move(tmp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            LOGGER.error("SealBot: failed to save {}", file, e);
        }
    }

    public void set(String user, Permission p) {
        if (user.equals("thelinuxseal")) return;   // owner can't be demoted
        map.put(user, p.ordinal());
    }
}