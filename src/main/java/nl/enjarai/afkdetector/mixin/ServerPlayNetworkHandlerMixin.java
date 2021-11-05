package nl.enjarai.afkdetector.mixin;

import nl.enjarai.afkdetector.AfkDetector;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
	@Shadow
	public ServerPlayerEntity player;

	@Inject(method = "onDisconnected", at = @At("TAIL"))
	public void onDisconnect(Text reason, CallbackInfo ci) {
		AfkDetector.TRACKER.playerLeft(player.getUuid());
	}
}
