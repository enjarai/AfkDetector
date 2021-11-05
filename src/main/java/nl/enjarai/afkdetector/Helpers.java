package nl.enjarai.afkdetector;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;

import java.util.UUID;

public class Helpers {
    public static void updatePlayerListEntry(UUID uuid) {
        PlayerListS2CPacket packet = new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME,
                AfkDetector.SERVER.getPlayerManager().getPlayer(uuid));
        AfkDetector.SERVER.getPlayerManager().sendToAll(packet);
    }
}
