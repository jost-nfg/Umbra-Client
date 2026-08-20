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
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.event.events.MouseClickEvent;
import net.umbra.event.events.MouseMoveEvent;
import net.umbra.event.events.MouseScrollEvent;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
	@Shadow
	private double xpos;
	@Shadow
	private double ypos;

	@Inject(at = { @At("HEAD") }, method = { "onButton(JLnet/minecraft/client/input/MouseButtonInfo;I)V" }, cancellable = true)
	private void onButton(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra != null && umbra.eventManager != null) {
			int button = buttonInfo.button();
			int mods = buttonInfo.modifiers();
			MouseClickEvent event = new MouseClickEvent(xpos, ypos, button, action, mods);
			umbra.eventManager.Fire(event);

			if (umbra.guiManager != null && (umbra.guiManager.isClickGuiOpen() || umbra.guiManager.isFlatMenuOpen())) {
				ci.cancel();
			}
		}
	}

	@Inject(at = { @At("HEAD") }, method = { "onScroll(JDD)V" }, cancellable = true)
	private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra != null && umbra.eventManager != null) {
			MouseScrollEvent event = new MouseScrollEvent(horizontal, vertical);
			umbra.eventManager.Fire(event);

			if (event.isCancelled()) {
				ci.cancel();
			}
		}
	}

	@Inject(at = { @At("HEAD") }, method = { "grabMouse()V" }, cancellable = true)
	private void onLockCursor(CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra != null && umbra.guiManager != null) {
			if (umbra.guiManager.isClickGuiOpen() || umbra.guiManager.isFlatMenuOpen())
				ci.cancel();
		}
	}

	@Inject(at = { @At("HEAD") }, method = { "onMove(JDD)V" }, cancellable = true)
	private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra != null && umbra.eventManager != null) {
			double cursorDeltaX = x - this.xpos;
			double cursorDeltaY = y - this.ypos;

			MouseMoveEvent event = new MouseMoveEvent(x, y, cursorDeltaX, cursorDeltaY);
			umbra.eventManager.Fire(event);

			if (event.isCancelled()) {
				// Update the XY but not the delta (used for camera movements);
				this.xpos = x;
				this.ypos = y;
				ci.cancel();
			}

		}
	}
}