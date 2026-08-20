/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.render;

import net.umbra.Umbra;
import net.umbra.event.events.Render3DEvent;
import net.umbra.event.listeners.Render3DListener;
import net.umbra.gui.colors.Color;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.rendering.shaders.Shader;
import net.umbra.settings.types.ShaderSetting;
import net.umbra.settings.types.FloatSetting;
import net.umbra.utils.ModuleUtils;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.phys.AABB;

public class ChestESP extends Module implements Render3DListener {

	private final ShaderSetting color = ShaderSetting.builder().id("chestesp_color").displayName("Color")
			.description("Color").defaultValue(Shader.solid(new Color(0, 1f, 1f, 0.3f))).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("chestesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public ChestESP() {
		super("ChestESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see Chests with an ESP.");

		addSettings(color, lineThickness);
	}

	@Override
	public void onDisable() {
		Umbra.getInstance().eventManager.RemoveListener(Render3DListener.class, this);
	}

	@Override
	public void onEnable() {
		Umbra.getInstance().eventManager.AddListener(Render3DListener.class, this);
	}

	@Override
	public void onToggle() {

	}

	@Override
	public void onRender(Render3DEvent event) {
		ModuleUtils.getTileEntities().forEach(blockEntity -> {
			if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof TrappedChestBlockEntity
					|| blockEntity instanceof BarrelBlockEntity) {
				AABB box = new AABB(blockEntity.getBlockPos());
				event.getRenderer().drawBox(box, color.getValue(),
						lineThickness.getValue().floatValue());
			}
		});
	}
}
