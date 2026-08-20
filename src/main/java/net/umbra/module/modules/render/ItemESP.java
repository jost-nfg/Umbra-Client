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
import net.umbra.settings.types.BooleanSetting;
import net.umbra.settings.types.ShaderSetting;
import net.umbra.settings.types.FloatSetting;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;

public class ItemESP extends Module implements Render3DListener {

	private final ShaderSetting color = ShaderSetting.builder().id("itemesp_color").displayName("Color")
			.description("Color").defaultValue(Shader.solid(new Color(0, 1f, 1f, 0.3f))).build();

	private final BooleanSetting visibilityToggle = BooleanSetting.builder().id("itemesp_visibility")
			.displayName("Visibility").defaultValue(true).build();

	private final FloatSetting range = FloatSetting.builder().id("itemesp_range").displayName("Range")
			.description("Range that the ESP will be drawn on items.").defaultValue(100f).minValue(10f).maxValue(500f)
			.step(5f).build();

	private final ShaderSetting rareItemColor = ShaderSetting.builder().id("itemesp_rare_color")
			.displayName("Rare Item Color").description("Rare Item Color").defaultValue(Shader.solid(new Color(1f, 0.5f, 0f, 1f)))
			.build();

	private final BooleanSetting colorRarity = BooleanSetting.builder().id("itemesp_color_rarity")
			.displayName("Color Rarity").defaultValue(true).build();

	private final FloatSetting lineThickness = FloatSetting.builder().id("itemesp_linethickness")
			.displayName("Line Thickness").description("Adjust the thickness of the ESP box lines").defaultValue(2f)
			.minValue(0f).maxValue(5f).step(0.1f).build();

	public ItemESP() {
		super("ItemESP");
		setCategory(Category.of("Render"));
		setDescription("Allows the player to see items with an ESP.");

		addSetting(color);
		addSetting(lineThickness);
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
		if (!visibilityToggle.getValue())
			return;

		Vec3 playerPos = MC.player.position();
		for (Entity entity : Umbra.getInstance().entityManager.getEntities()) {
			if (entity instanceof ItemEntity) {
				Vec3 itemPos = entity.position();
				if (playerPos.distanceTo(itemPos) <= range.getValue()) {
					Shader finalColor = colorRarity.getValue() ? getColorBasedOnItemRarity(entity) : color.getValue();
					event.getRenderer().drawBox(entity.getBoundingBox(), finalColor,
							lineThickness.getValue().floatValue());
				}
			}
		}
	}

	private Shader getColorBasedOnItemRarity(Entity entity) {
		boolean isRare = false;

		if (entity instanceof ItemEntity itemEntity) {
            isRare = itemEntity.getItem().getRarity() == Rarity.RARE;
		}

		return isRare ? rareItemColor.getValue() : color.getValue();
	}
}
