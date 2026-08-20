package net.umbra.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.umbra.Umbra;
import net.umbra.managers.rotation.goals.Goal;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.phys.Vec3;

@Mixin(FireworkRocketEntity.class)
public class FireworkRocketEntityMixin {
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
	private Vec3 onGetLookAngle(LivingEntity instance) {
		net.umbra.UmbraClient umbra = Umbra.getInstance();
		if (umbra == null || !umbra.isFullyLoaded())
			return instance.getLookAngle();

		Minecraft mc = Minecraft.getInstance();
		if (instance == mc.player) {
			Goal<?> goal = Umbra.getInstance().rotationManager.getGoal();
			if (goal != null && goal.isFakeRotation()) {
				Float serverYaw = Umbra.getInstance().rotationManager.getServerYaw();
				Float serverPitch = Umbra.getInstance().rotationManager.getServerPitch();
				if (serverYaw != null && serverPitch != null) {
					return instance.calculateViewVector(serverPitch, serverYaw);
				}
			}
		}
		return instance.getLookAngle();
	}
}
