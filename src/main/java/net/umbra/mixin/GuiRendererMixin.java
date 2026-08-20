package net.umbra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.umbra.Umbra;
import net.umbra.gui.GuiManager;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@Unique
	private final CubeMap umbra$cubeMap = new CubeMap(Identifier.fromNamespaceAndPath("umbra", "textures/mainmenu/panorama"));

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderTail(CallbackInfo ci) {
		if (Umbra.getInstance() != null && Umbra.getInstance().render2D != null) {
			Umbra.getInstance().render2D.render();
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/CubeMap;render(FF)V"))
	private void umbra$redirectPanoramaCubeMap(CubeMap instance, float rotX, float rotY) {
		boolean useUmbra = Umbra.getInstance() != null && GuiManager.enableCustomTitle.getValue();
		if(useUmbra) {
			this.umbra$cubeMap.render(rotX, rotY);
		}else {
			instance.render(rotX, rotY);
		}
	}

	@Inject(method = "registerPanoramaTextures", at = @At("TAIL"))
	private void umbra$registerPanoramaTextures(TextureManager textureManager, CallbackInfo ci) {
		this.umbra$cubeMap.registerTextures(textureManager);
	}

	@Inject(method = "close", at = @At("TAIL"))
	private void umbra$closePanoramaCubeMap(CallbackInfo ci) {
		this.umbra$cubeMap.close();
	}
}
