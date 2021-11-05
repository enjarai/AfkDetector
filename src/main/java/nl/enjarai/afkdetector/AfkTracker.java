package nl.enjarai.afkdetector;

import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.UUID;

public class AfkTracker {
    private final HashMap<UUID, Long> lastMoved = new HashMap<>();
    private final HashMap<UUID, Vec3d> lastPos = new HashMap<>();
    private final HashMap<UUID, Boolean> lastAfk = new HashMap<>();

    public void touchPlayer(UUID uuid) {
        lastMoved.put(uuid, System.currentTimeMillis());
    }

    public void playerLeft(UUID uuid) {
        lastMoved.remove(uuid);
        lastPos.remove(uuid);
        lastAfk.remove(uuid);
    }

    public boolean isAfk(UUID uuid) {
        return lastMoved.get(uuid) < System.currentTimeMillis() - (AfkDetector.CONFIG.secondsTillAfk * 1000L);
    }

    public boolean stateUpdated(UUID uuid) {
        boolean afk = isAfk(uuid);
        Boolean wasAfk = lastAfk.get(uuid);

        // Special case for player that has just logged in
        if (wasAfk == null) {
            lastAfk.put(uuid, afk);
            return false;
        }

        boolean updated = wasAfk != afk;
        if (updated) {
            lastAfk.put(uuid, afk);
        }
        return updated;
    }

    public boolean hasMoved(ServerPlayerEntity player) {
        Vec3d pos = player.getPos();
        UUID uuid = player.getUuid();
        Vec3d last = lastPos.get(uuid);

        lastPos.put(uuid, pos);
        return pos != last;
    }

}
