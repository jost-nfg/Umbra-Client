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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.event.events.PlayerHealthEvent;
import net.umbra.event.events.SendMovementPacketEvent;
import net.umbra.gui.GuiManager;
import net.umbra.mixin.interfaces.ICamera;
import net.umbra.module.modules.combat.AntiKnockback;
import net.umbra.module.modules.movement.Fly;
import net.umbra.module.modules.movement.HighJump;
import net.umbra.module.modules.movement.Noclip;
import net.umbra.module.modules.movement.Step;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayerMixin {
	@Shadow
	private ClientPacketListener connection;

	@Shadow
	protected abstract void sendPosition();

	@Inject(at = { @At("HEAD") }, method = "setShowDeathScreen(Z)V")
	private void onShowDeathScreen(boolean state, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		GuiManager hudManager = umbra.guiManager;

		if (state && hudManager.isClickGuiOpen()) {
			hudManager.setClickGuiOpen(false);
		}

		if (state && hudManager.isFlatMenuOpen()) {
			hudManager.setFlatMenuOpen(false);
		}
	}

	@Override
	public void onSetHealth(float health, CallbackInfo ci) {
		PlayerHealthEvent event = new PlayerHealthEvent(null, health);
		Umbra.getInstance().eventManager.Fire(event);
	}

	@Override
	protected void onGetOffGroundSpeed(CallbackInfoReturnable<Float> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.fly.state.getValue()) {
			Fly fly = umbra.moduleManager.fly;
			cir.setReturnValue((float) fly.getSpeed());
		} else if (umbra.moduleManager.noclip.state.getValue()) {
			Noclip noclip = umbra.moduleManager.noclip;
			cir.setReturnValue(noclip.getSpeed());
		}
	}

	@Override
	public void onGetStepHeight(CallbackInfoReturnable<Float> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		Step stepHack = umbra.moduleManager.step;
		if (stepHack.state.getValue()) {
			cir.setReturnValue(cir.getReturnValue());
		}
	}

	@Override
	public void onGetJumpVelocityMultiplier(CallbackInfoReturnable<Float> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		HighJump higherJump = umbra.moduleManager.higherjump;
		if (higherJump.state.getValue()) {
			cir.setReturnValue(higherJump.getJumpHeightMultiplier());
		}
	}

	@Override
	public void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.freecam.state.getValue()) {
			float f = (float) cursorDeltaY * 0.15f;
			float g = (float) cursorDeltaX * 0.15f;

			Minecraft mc = Minecraft.getInstance();
			Camera camera = mc.gameRenderer.mainCamera();
			ICamera icamera = (ICamera) camera;

			float newYaw = camera.yRot() + g;
			float newPitch = Math.min(90, Math.max(camera.xRot() + f, -90));

			icamera.setCameraRotation(newYaw, newPitch);
			ci.cancel();
		}
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 0))
	private void onTickHasVehicleBeforeSendPackets(CallbackInfo info) {
		SendMovementPacketEvent.Pre sendMovementPacketPreEvent = new SendMovementPacketEvent.Pre();
		Umbra.getInstance().eventManager.Fire(sendMovementPacketPreEvent);
	}

	@Inject(method = "sendPosition", at = @At("HEAD"), cancellable = true)
	private void onSendMovementPacketsHead(CallbackInfo info) {
		SendMovementPacketEvent.Pre sendMovementPacketPreEvent = new SendMovementPacketEvent.Pre();
		Umbra.getInstance().eventManager.Fire(sendMovementPacketPreEvent);
		if (sendMovementPacketPreEvent.isCancelled())
			info.cancel();
	}

	@Inject(method = "sendPosition", at = @At("TAIL"), cancellable = true)
	private void onSendMovementPacketsTail(CallbackInfo info) {
		SendMovementPacketEvent.Post sendMovementPacketPostEvent = new SendMovementPacketEvent.Post();
		Umbra.getInstance().eventManager.Fire(sendMovementPacketPostEvent);
		if (sendMovementPacketPostEvent.isCancelled())
			info.cancel();
	}

	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V", ordinal = 1, shift = At.Shift.AFTER))
	private void onTickHasVehicleAfterSendPackets(CallbackInfo info) {
		SendMovementPacketEvent.Post sendMovementPacketPostEvent = new SendMovementPacketEvent.Post();

		Umbra.getInstance().eventManager.Fire(sendMovementPacketPostEvent);
	}

	@Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"), cancellable = true)
	private void onPushOutOfBlocks(double x, double z, CallbackInfo ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		AntiKnockback antiKnockback = umbra.moduleManager.antiknockback;

		if (antiKnockback.state.getValue() && antiKnockback.getNoPushBlocks()) {
			ci.cancel();
		}
	}
}
