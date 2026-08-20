/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.umbra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.umbra.Umbra;
import net.umbra.event.events.Render2DEvent;
import net.umbra.module.modules.render.NoRender;
import net.umbra.rendering.Renderer2D;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

@Mixin(Hud.class)
public class GuiMixin {
	private static final String POWDER_SNOW_PATH = "textures/misc/powder_snow_outline.png";
	private static final String PUMPKIN_PATH = "textures/misc/pumpkinblur.png";
	

	@Inject(at = @At("TAIL"), method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V")
	private void onRenderPlayerList(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		Renderer2D renderer = umbra.render2D;
		renderer.beginFrame(context, tickCounter);
		Umbra.getInstance().eventManager.Fire(new Render2DEvent(renderer));
	}

	@Inject(method = "extractVignette", at = @At("HEAD"), cancellable = true)
	private void onRenderVignetteOverlay(GuiGraphicsExtractor context, Entity entity, CallbackInfo ci) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		NoRender norender = umbra.moduleManager.norender;

		if (norender.state.getValue() && norender.getNoVignette())
			ci.cancel();
	}

	@ModifyVariable(method = "extractTextureOverlay", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private float modifyOverlayOpacity(float opacity, GuiGraphicsExtractor context, Identifier identifier) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return opacity;

		NoRender norender = umbra.moduleManager.norender;
		if (norender == null || !norender.state.getValue())
			return opacity;

		String path = identifier.getPath();

		if (path.equals(PUMPKIN_PATH) && norender.getNoPumpkinOverlay()) {
			return 0f;
		}

		if (path.equals(POWDER_SNOW_PATH) && norender.getNoPowderSnowOverlay()) {
			return 0f;
		}

		return opacity;
	}

	@Inject(method = "extractPortalOverlay", at = @At("HEAD"), cancellable = true)
	private void onRenderPortalOverlay(GuiGraphicsExtractor context, float nauseaStrength, CallbackInfo ci) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		NoRender norender = umbra.moduleManager.norender;

		if (norender.state.getValue() && norender.getNoPortalOverlay())
			ci.cancel();
	}

	@Inject(method = "extractCrosshair", at = @At("HEAD"), cancellable = true)
	private void onRenderCrosshair(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.guiManager.isClickGuiOpen() || umbra.guiManager.isFlatMenuOpen()) {
			ci.cancel();
			return;
		}
		NoRender norender = umbra.moduleManager.norender;
		if (norender.state.getValue() && norender.getNoCrosshair())
			ci.cancel();
	}
}
