package net.umbra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import net.umbra.Umbra;
import net.umbra.module.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {

	@Inject(method = "submitFire(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;)V", at = @At("HEAD"), cancellable = true)
	private static void onRenderFireOverlay(PoseStack matrices, SubmitNodeCollector vertexConsumers,
			TextureAtlasSprite sprite, CallbackInfo info) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		NoRender norender = umbra.moduleManager.norender;

		if (norender.state.getValue() && norender.getNoFireOverlay())
			info.cancel();
	}

	@Inject(method = "submitWater", at = @At("HEAD"), cancellable = true)
    private static void onRenderUnderwaterOverlay(Minecraft client, PoseStack matrices,
			SubmitNodeCollector vertexConsumers, CallbackInfo info) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		NoRender norender = umbra.moduleManager.norender;

		if (norender.state.getValue() && norender.getNoLiquidOverlay())
			info.cancel();
	}
}
