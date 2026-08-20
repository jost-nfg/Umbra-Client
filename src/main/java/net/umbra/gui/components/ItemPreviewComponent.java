/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.components;

import net.umbra.gui.UIElement;
import net.umbra.gui.UIProperty;
import net.umbra.gui.colors.Color;
import net.umbra.gui.colors.Colors;
import net.umbra.gui.types.Size;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ItemPreviewComponent extends Component {
	private static final float NATIVE_SIZE = 16f;
	private static final RandomSource SPRITE_PICKER = RandomSource.create(0L);

	private ItemStack itemStack = ItemStack.EMPTY;
	private final ItemStackRenderState renderState = new ItemStackRenderState();
	private @Nullable TextureAtlasSprite sprite;
	private @Nullable ItemStack lastItem;

	public static final UIProperty<Item> ItemProperty = new UIProperty<>("Item", null, false, false,
			ItemPreviewComponent::onItemChanged);
	
	public static final UIProperty<ItemStack> ItemStackProperty = new UIProperty<>("ItemStack", null, false, false,
			ItemPreviewComponent::onItemStackChanged);

	private static void onItemChanged(UIElement sender, Item oldValue, Item newValue) {
		if (sender instanceof ItemPreviewComponent itemComponent) {
			itemComponent.itemStack = newValue == null ? ItemStack.EMPTY : new ItemStack(newValue);
			itemComponent.regenerateSprite();
		}
	}

	private static void onItemStackChanged(UIElement sender, ItemStack oldValue, ItemStack newValue) {
		if (sender instanceof ItemPreviewComponent itemComponent) {
			itemComponent.itemStack = newValue == null ? ItemStack.EMPTY : newValue;
			itemComponent.regenerateSprite();
		}
	}

	private void regenerateSprite() {
		sprite = null;
		if (itemStack.isEmpty())
			return;

		renderState.clear();
		MC.getItemModelResolver().updateForTopItem(renderState, itemStack, ItemDisplayContext.GUI, MC.level, null, 0);
		SPRITE_PICKER.setSeed(0L);
		Material.Baked mat = renderState.pickParticleMaterial(SPRITE_PICKER);
		if (mat != null)
			sprite = mat.sprite();
		lastItem = itemStack;
	}

	public ItemPreviewComponent() {

	}

	@Override
	public Size measure(Size availableSize) {
		Float width = getProperty(UIElement.WidthProperty);
		Float height = getProperty(UIElement.HeightProperty);
		float w = width != null ? width : NATIVE_SIZE;
		float h = height != null ? height : NATIVE_SIZE;
		return new Size(w, h);
	}

	@Override
	public void draw(Renderer2D renderer, float partialTicks) {
		if (sprite == null)
			return;

		float ax = getActualSize().x();
		float ay = getActualSize().y();
		float aw = getActualSize().width();
		float ah = getActualSize().height();
		renderer.drawSprite(sprite.atlasLocation(), sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), ax,
				ay, aw, ah, Shader.image(Colors.White));

		if (itemStack.isBarVisible()) {
			float sx = aw / 16f;
			float sy = ah / 16f;
			float barX = ax + 2f * sx;
			float barY = ay + 13f * sy;
			int rgb = itemStack.getBarColor();
			Color barColor = new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
			renderer.drawBox(barX, barY, 13f * sx, 2f * sy, Shader.solid(Colors.Black));
			renderer.drawBox(barX, barY, itemStack.getBarWidth() * sx, sy, Shader.solid(barColor));
		}
	}
}
