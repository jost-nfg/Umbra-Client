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
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.settings.types.BooleanSetting;

public class AutoWalk extends Module implements TickListener {
	private final BooleanSetting automaticJump = BooleanSetting.builder().id("autowalk_automatic_jump")
			.displayName("Automatically Jump").description("Automatically jumps when you hit a wall.")
			.defaultValue(true).build();

	public AutoWalk() {
		super("AutoWalk");

		setCategory(Category.of("Misc"));
		setDescription("Automatically forward walks for you.");

		addSetting(automaticJump);
	}

	@Override
	public void onDisable() {
		MC.options.keyUp.setDown(false);
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
		MC.options.keyUp.setDown(true);
		if (MC.player.horizontalCollision && MC.player.onGround())
			MC.player.jumpFromGround();
	}

	@Override
	public void onTick(Post event) {

	}
}
