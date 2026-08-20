/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.misc;

import net.umbra.Umbra;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.TickListener;
import net.umbra.managers.rotation.RotationMode;
import net.umbra.managers.rotation.goals.EntityGoal;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.BooleanSetting;
import net.umbra.settings.types.EnumSetting;
import net.umbra.settings.types.FloatSetting;
import net.umbra.utils.FindItemResult;
import net.umbra.utils.entity.BodyPart;
import net.umbra.utils.entity.EntityUtils;
import net.umbra.utils.player.InteractionUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;

public class AutoShear extends Module implements TickListener {

	private final FloatSetting radius = FloatSetting.builder().id("autoshear_radius").displayName("Radius")
			.description("Radius that AutoShear will trigger on Mobs.").defaultValue(3f).minValue(0.1f).maxValue(10f)
			.step(0.1f).build();

	private final BooleanSetting useRaycast = BooleanSetting.builder().id("autoshear_use_raycast")
			.displayName("Use Raycast")
			.description("Whether a raycast will be used to ensure that AutoShear will only shear visible sheep.")
			.defaultValue(false).build();

	private final EnumSetting<RotationMode> rotationMode = EnumSetting.<RotationMode>builder()
			.id("autoshear_rotation_mode").displayName("Rotation Mode")
			.description("Controls how the player's view rotates.").defaultValue(RotationMode.NONE).build();

	private final FloatSetting maxRotation = FloatSetting.builder().id("autoshear_max_rotation").displayName("Max Rotation")
			.description("The max speed that AutoShear will rotate").defaultValue(10.0f).minValue(1.0f).maxValue(360.0f)
			.build();

	private final FloatSetting yawRandomness = FloatSetting.builder().id("autoshear_yaw_randomness")
			.displayName("Yaw Rotation Jitter").description("The randomness of the player's yaw").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private final FloatSetting pitchRandomness = FloatSetting.builder().id("autoshear_pitch_randomness")
			.displayName("Pitch Rotation Jitter").description("The randomness of the player's pitch").defaultValue(0.0f)
			.minValue(0.0f).maxValue(10.0f).step(0.1f).build();

	private EntityGoal currentGoal;

	public AutoShear() {
		super("AutoShear");

		setCategory(Category.of("Misc"));
		setDescription("Automatically shears Sheep that are near you.");

		addSetting(radius);
		addSetting(useRaycast);
		addSetting(rotationMode);
		addSetting(maxRotation);
		addSetting(yawRandomness);
		addSetting(pitchRandomness);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
		reset();
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		Sheep foundEntity = null;
		double closestSqr = Double.MAX_VALUE;

		for (Entity entity : Umbra.getInstance().entityManager.getEntities()) {
			if (!(entity instanceof Sheep sheep))
				continue;

			// Ensure that the sheap is within a range.
			double distSqr = MC.player.distanceToSqr(entity);
			if (distSqr > radius.getValueSqr() || distSqr >= closestSqr)
				continue;

			// Get if the sheep is shearable.
            if (!sheep.readyForShearing() || sheep.isSheared() || sheep.isBaby())
				continue;

			closestSqr = distSqr;
			foundEntity = sheep;
		}

		if (foundEntity != null) {
			// Set the rotation goal to that sheep.
			currentGoal = EntityGoal.builder().goal(foundEntity).mode(rotationMode.getValue())
					.maxRotation(maxRotation.getValue()).pitchRandomness(pitchRandomness.getValue())
					.yawRandomness(yawRandomness.getValue()).build();
			Umbra.getInstance().rotationManager.setGoal(currentGoal);

			// Try and find the item slow, and change to it if needed.
			FindItemResult shearItemSlot = findInHotbar(Items.SHEARS);
			if (shearItemSlot.found()) {
				swap(shearItemSlot.slot(), false);

				InteractionHand hand = shearItemSlot.getHand();
				if (hand == null)
					return;

				EntityHitResult hitResult = useRaycast.getValue()
						? InteractionUtils.raycastEntity(foundEntity, radius.getValue())
						: new EntityHitResult(foundEntity,
								EntityUtils.getBodyPartPosition(foundEntity, BodyPart.CHEST, 1.0f));
				if (hitResult == null)
					return;

				MC.player.swing(hand);
				MC.gameMode.interact(MC.player, foundEntity, hitResult, hand);
			}
		} else
			// No entity found, reset the rotation goal.
			reset();
	}

	private void reset() {
		if (currentGoal == null)
			return;

		if (Umbra.getInstance().rotationManager.getGoal() == currentGoal)
			Umbra.getInstance().rotationManager.setGoal(null);

		currentGoal = null;
	}
}
