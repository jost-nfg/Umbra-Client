/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.render;

import java.util.LinkedList;

import net.umbra.Umbra;
import net.umbra.event.events.Render3DEvent;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.Render3DListener;
import net.umbra.event.listeners.TickListener;
import net.umbra.gui.colors.Color;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.rendering.shaders.Shader;
import net.umbra.settings.types.ShaderSetting;
import net.minecraft.world.phys.Vec3;

public class Breadcrumbs extends Module implements Render3DListener, TickListener {

	private final ShaderSetting color = ShaderSetting.builder().id("breadcrumbs_color").displayName("Color")
			.description("Color").defaultValue(Shader.solid(new Color(0, 1f, 1f))).build();

	private final float distanceThreshold = 1.0f; // Minimum distance to record a new position
	private float currentTick = 0;
	private final float timer = 10;
	private final LinkedList<Vec3> positions = new LinkedList<>();
	private final int maxPositions = 1000;

	public Breadcrumbs() {
		super("Breadcrumbs");
		setCategory(Category.of("Render"));
		setDescription("Shows breadcrumbs of where you last stepped;");
		addSetting(color);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
		Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);
		positions.clear();
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(Render3DListener.class, this);
		Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		Vec3 prevPosition = null;
		for (Vec3 position : positions) {
			if (prevPosition != null) {
				event.getRenderer().drawLine(prevPosition, position, color.getValue());
			}
			prevPosition = position;
		}
	}

	@Override
	public void onTick(Pre event) {

	}

	@Override
	public void onTick(Post event) {
		currentTick++;
		if (timer == currentTick) {
			currentTick = 0;
			if (!Umbra.getInstance().moduleManager.freecam.state.getValue()) {
				Vec3 currentPosition = MC.player.position();
				if (positions.isEmpty() || positions.getLast().distanceToSqr(currentPosition) >= distanceThreshold
						* distanceThreshold) {
					if (positions.size() >= maxPositions) {
						positions.removeFirst();
					}
					positions.add(currentPosition);
				}
			}
		}
	}
}