package net.umbra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.module.modules.movement.Freecam;
import net.umbra.module.modules.render.NoRender;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;

@Mixin(Camera.class)
public class CameraMixin {
	@Shadow
	private boolean initialized;

	@Shadow
	private Entity entity;

	@Shadow
	private Level level;

	@Inject(at = {
			@At("HEAD") }, method = "setEntity(Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
	private void onSetEntity(Entity entity, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.freecam.state.getValue() && this.entity != null) {
			ci.cancel();
		}
	}

	@Inject(at = { @At("HEAD") }, method = "alignWithEntity(F)V", cancellable = true)
	private void onAlignWithEntity(float partialTicks, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.freecam.state.getValue() && this.entity != null) {
			ci.cancel();
		}
	}

	@Inject(at = { @At("HEAD") }, method = "isDetached()Z", cancellable = true)
	private void onIsDetached(CallbackInfoReturnable<Boolean> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.freecam.state.getValue()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(at = {
			@At("HEAD") }, method = "getFluidInCamera()Lnet/minecraft/world/level/material/FogType;", cancellable = true)
	private void onGetSubmersionType(CallbackInfoReturnable<FogType> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		Freecam freecam = umbra.moduleManager.freecam;
		NoRender norender = umbra.moduleManager.norender;

		if (freecam.state.getValue() || (norender.state.getValue() && norender.getNoLiquidOverlay())) {
			cir.setReturnValue(FogType.NONE);
		}
	}
}
