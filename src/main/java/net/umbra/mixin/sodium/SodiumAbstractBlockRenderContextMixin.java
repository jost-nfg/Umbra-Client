package net.umbra.mixin.sodium;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.module.modules.render.XRay;
import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = AbstractBlockRenderContext.class, remap = false)
public abstract class SodiumAbstractBlockRenderContextMixin {

	@Shadow
	protected BlockState state;

	@Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
	private void onShouldDrawSide(Direction face, CallbackInfoReturnable<Boolean> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded()) return;
		XRay xray = umbra.moduleManager.xray;
		if (xray.state.getValue() && state != null && xray.isXRayBlock(state.getBlock())) {
			cir.setReturnValue(true);
		}
	}
}
