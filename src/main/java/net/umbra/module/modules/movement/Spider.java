/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.movement;

import net.umbra.Umbra;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.TickListener;
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class Spider extends Module implements TickListener {

	private final FloatSetting speed = FloatSetting.builder().id("spider_speed").displayName("Speed")
			.description("Speed that the player climbs up blocks.").defaultValue(0.1f).minValue(0.05f).maxValue(1f)
			.step(0.05f).build();

	public Spider() {
		super("Spider");
		setCategory(Category.of("Movement"));
		setDescription("Allows players to climb up blocks like a spider.");
		addSetting(speed);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity,
		    AntiCheat.Karhu
		);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
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
		LocalPlayer player = MC.player;

		if (player.horizontalCollision) {
			Vec3 playerVelocity = player.getDeltaMovement();
			MC.player.setDeltaMovement(new Vec3(playerVelocity.x(), speed.getValue(), playerVelocity.z()));
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
