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

import net.umbra.Umbra;
import net.umbra.event.events.PlayerDeathEvent;
import net.umbra.gui.GuiManager;
import net.minecraft.client.gui.screens.DeathScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {

    @Inject(at = {@At("HEAD")}, method = "init()V", cancellable = true)
    private void onInit(CallbackInfo ci) {
        net.umbra.UmbraClient umbra = Umbra.getInstance();
        if (umbra == null || !umbra.isFullyLoaded())
            return;

        GuiManager hudManager = umbra.guiManager;
        if (hudManager.isClickGuiOpen()) {
            hudManager.setClickGuiOpen(false);
        }

        PlayerDeathEvent event = new PlayerDeathEvent();
        Umbra.getInstance().eventManager.Fire(event);

        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
