package nl.enjarai.afkdetector.mixin;

import nl.enjarai.afkdetector.AfkDetector;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.enjarai.afkdetector.Helpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        UUID uuid = player.getUuid();
        AfkDetector.TRACKER.touchPlayer(uuid);
        Helpers.updatePlayerListEntry(uuid);
    }
}
