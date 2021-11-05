package nl.enjarai.afkdetector;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import eu.pb4.placeholders.TextParser;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ReloadCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class AfkDetector implements ModInitializer {
    public static MinecraftServer SERVER;
    public static final Logger LOGGER = LogManager.getLogger("AfkDetector");
    public static final File CONFIG_FILE = new File("config/afkdetector.json");
    public static final ConfigManager CONFIG = ConfigManager.loadConfigFile(CONFIG_FILE);
    public static final AfkTracker TRACKER = new AfkTracker();

    public static byte TICK_COUNTER = 0;


    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(AfkDetector::onServerStarting);

        // Define and register "the playertoucher"
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (TICK_COUNTER > 10) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    UUID uuid = player.getUuid();
                    if (TRACKER.hasMoved(player)) {
                        TRACKER.touchPlayer(uuid);
                    }
                    if (TRACKER.stateUpdated(uuid)) {
                        Helpers.updatePlayerListEntry(uuid);
                        player.sendMessage(TextParser.parse(TRACKER.isAfk(uuid) ?
                                CONFIG.nowAfkMsg : CONFIG.noLongerAfkMsg), false);
                    }
                }

                TICK_COUNTER = 0;
            }
            TICK_COUNTER++;
        });

        // Define and register the placeholderAPI placeholder
        PlaceholderAPI.register(new Identifier("afk", "displayname"), context -> {
            ServerPlayerEntity player = context.getPlayer();
            if (TRACKER.isAfk(player.getUuid())) {
                HashMap<String, Text> placeholders = new HashMap<>();

                placeholders.put("displayname", new LiteralText(player.getDisplayName().asString()));

                return PlaceholderResult.value(PlaceholderAPI.parsePredefinedText(
                        TextParser.parse(CONFIG.afkNameFormat),
                        PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                        placeholders
                ));
            } else {
                return PlaceholderAPI.getPlaceholders().get(
                        new Identifier("player", "displayname")).PlaceholderHandler(context);
            }
        });

        LOGGER.info("Ready to make some names turn gray!");
    }

    private static void onServerStarting(MinecraftServer server) {
        SERVER = server;
    }
}
