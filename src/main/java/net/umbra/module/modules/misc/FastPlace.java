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
import net.umbra.module.AntiCheat;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.FloatSetting;

public class FastPlace extends Module implements TickListener {

	private final FloatSetting speed = FloatSetting.builder().id("fastplace_delay").displayName("Delay")
			.description("Delay at which blocks are placed in ticks..").defaultValue(0f).minValue(0f).maxValue(5f)
			.step(1f).build();

	public FastPlace() {
		super("FastPlace");

		setCategory(Category.of("Misc"));
		setDescription("Places blocks exceptionally fast");

		addSetting(speed);

		setDetectable(
		    AntiCheat.NoCheatPlus,
		    AntiCheat.AdvancedAntiCheat,
		    AntiCheat.Matrix,
		    AntiCheat.Negativity
		);
	}

	@Override
	public void onDisable() {
		IMC.setItemUseCooldown(5);
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
		int currentItemCooldown = IMC.getItemUseCooldown();
		int speedValue = speed.getValue().intValue();
		if (currentItemCooldown == 0 || currentItemCooldown > speedValue)
			IMC.setItemUseCooldown(speedValue);
	}

	@Override
	public void onTick(Post event) {

	}
}
