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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.module.modules.combat.AntiKnockback;
import net.umbra.module.modules.combat.Reach;
import net.umbra.module.modules.misc.FastBreak;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntityMixin {

	@Shadow
	private Inventory inventory;

	@Inject(method = "isSpectator()Z", at = @At("HEAD"), cancellable = true)
	public void onIsSpectator(CallbackInfoReturnable<Boolean> cir) {

	}

	@Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
	public void onGetBlockBreakingSpeed(BlockState blockState, CallbackInfoReturnable<Float> ci) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		FastBreak fastBreak = umbra.moduleManager.fastbreak;

		// If fast break is enabled.
		if (fastBreak.state.getValue()) {
			// Multiply the break speed and override the return value.
			// float speed = inventory.getBlockBreakingSpeed(blockState);
			float speed = fastBreak.getMultiplier();

			if (!fastBreak.shouldIgnoreWater()) {
				if (isEyeInFluid(FluidTags.WATER) || isEyeInFluid(FluidTags.LAVA) || !onGround()) {
					// speed /= 5.0F;
				}
			}

			ci.setReturnValue(speed);
		}
	}

	@Inject(at = { @At("HEAD") }, method = "getFlyingSpeed()F", cancellable = true)
	protected void onGetOffGroundSpeed(CallbackInfoReturnable<Float> cir) {
	}

	@Inject(at = { @At("HEAD") }, method = "blockInteractionRange()D", cancellable = true)
	private void onBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.reach.state.getValue()) {
			Reach reach = umbra.moduleManager.reach;
			double effectiveReach = reach.getEffectiveReach();

			// Never reduce the vanilla survival block reach of 4.5 blocks.
			if (effectiveReach > 4.5)
				cir.setReturnValue(effectiveReach);
		}
	}

	@Inject(at = { @At("HEAD") }, method = "entityInteractionRange()D", cancellable = true)
	private void onEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		if (umbra.moduleManager.reach.state.getValue()) {
			Reach reach = umbra.moduleManager.reach;
			double effectiveReach = reach.getEffectiveReach();

			// Never reduce the vanilla entity reach of 3.0 blocks.
			if (effectiveReach > 3.0)
				cir.setReturnValue(effectiveReach);
		}
	}

	@Inject(method = "isPushedByFluid", at = @At("HEAD"), cancellable = true)
	private void onIsPushedByFluids(CallbackInfoReturnable<Boolean> cir) {
		UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return;

		AntiKnockback antiKnockback = umbra.moduleManager.antiknockback;

		if (antiKnockback.state.getValue() && antiKnockback.getNoPushLiquids()) {
			cir.setReturnValue(false);
		}
	}
}
