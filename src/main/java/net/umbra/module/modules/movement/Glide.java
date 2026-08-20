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

public class Glide extends Module implements TickListener {

	private final FloatSetting fallSpeed = FloatSetting.builder().id("glide_fallspeed").displayName("Fall Speed")
			.description("The speed at which the player will fall.").defaultValue(0.2f).minValue(0.1f).maxValue(2f)
			.step(0.1f).build();

	public Glide() {
		super("Glide");
		setCategory(Category.of("Movement"));
		setDescription("Allows the player to glide down when in the air. Does not prevent fall damage.");
		addSetting(fallSpeed);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.Vulcan,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Verus,
		    AntiCheat.Grim,
		    AntiCheat.Matrix,
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
		if (player.getDeltaMovement().y < 0 && (!player.onGround() || !player.isInLava() || !player.isUnderWater()
				|| !player.isSuppressingSlidingDownLadder())) {
			player.setDeltaMovement(player.getDeltaMovement().x, Math.max(player.getDeltaMovement().y, -fallSpeed.getValue()),
					player.getDeltaMovement().z);
		}
	}

	@Override
	public void onTick(Post event) {

	}
}
