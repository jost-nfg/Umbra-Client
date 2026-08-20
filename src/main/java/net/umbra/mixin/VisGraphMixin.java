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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.core.BlockPos;

@Mixin(VisGraph.class)
public class VisGraphMixin {
	@Inject(at = { @At("HEAD") }, method = { "setOpaque" }, cancellable = true)
	private void onMarkClosed(BlockPos pos, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.xray.state.getValue()
				|| (umbra.moduleManager.freecam.state.getValue() && umbra.moduleManager.freecam.disableCulling.getValue())) {
			ci.cancel();
		}
	}
}
