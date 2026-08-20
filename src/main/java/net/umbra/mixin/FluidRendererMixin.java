package net.umbra.mixin;

import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.module.modules.render.XRay;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {
    @Inject(method = "tesselate", at = @At("HEAD"), cancellable = true)
    private void onRender(BlockAndTintGetter world, BlockPos pos, FluidRenderer.Output output, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        UmbraClient umbra = Umbra.getInstance();
        if (umbra == null || !umbra.isFullyLoaded())
            return;

        XRay xray = umbra.moduleManager.xray;
        if (xray.state.getValue()) {
            if (!xray.isXRayBlock(blockState.getBlock()) && !xray.fluids.getValue()) {
                ci.cancel();
            }
        }
    }
}
