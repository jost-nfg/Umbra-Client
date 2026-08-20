/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants.Key;

import net.umbra.Umbra;
import net.umbra.event.events.KeyDownEvent;
import net.umbra.event.events.MouseClickEvent;
import net.umbra.event.events.MouseScrollEvent;
import net.umbra.event.events.Render2DEvent;
import net.umbra.event.events.TickEvent;
import net.umbra.event.listeners.KeyDownListener;
import net.umbra.event.listeners.MouseClickListener;
import net.umbra.event.listeners.MouseScrollListener;
import net.umbra.event.listeners.Render2DListener;
import net.umbra.event.listeners.TickListener;
import net.umbra.gui.GuiManager;
import net.umbra.gui.colors.Color;
import net.umbra.gui.font.FontManager;
import net.umbra.gui.font.UIFont;
import net.umbra.gui.types.Rectangle;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.shaders.Shader;
import net.umbra.utils.types.MouseAction;
import net.umbra.utils.types.MouseButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

/**
 * Flat-design module menu drawn entirely with the client's own
 * {@link Renderer2D} as an in-game overlay (no vanilla Screen). Toggled with
 * the INSERT key (see {@link GuiManager#normalGuiButton}); ESC, INSERT or a
 * click on the backdrop close it again. While open the game input is blocked
 * the same way the ClickGUI blocks it (see MouseHandlerMixin /
 * KeyboardHandlerMixin) and the game keeps running unpaused.
 *
 * All drawing happens in the same coordinate space GuiManager uses: the pose
 * is pre-scaled by {@code 1 / guiScale}, so layout and mouse coordinates
 * (Minecraft.mouseHandler) line up in raw window pixels.
 */
public class FlatMenu implements KeyDownListener, MouseClickListener, MouseScrollListener, TickListener,
		Render2DListener {
	private static final Minecraft MC = Minecraft.getInstance();

	/** Sidebar categories in fixed order; any extra (addon) categories follow. */
	private static final String[] CATEGORY_ORDER = { "Combat", "Movement", "Render", "World", "Misc" };

	private static final float PANEL_MAX_WIDTH = 760f;
	private static final float PANEL_MAX_HEIGHT = 480f;
	private static final float PANEL_MARGIN = 80f;
	private static final float PANEL_RADIUS = 10f;
	private static final float TOP_BAR_HEIGHT = 46f;
	private static final float SIDEBAR_WIDTH = 148f;
	private static final float CATEGORY_ROW_HEIGHT = 28f;
	private static final float SEARCH_WIDTH = 190f;
	private static final float SEARCH_HEIGHT = 24f;
	private static final float CONTENT_PADDING = 14f;
	private static final float HEADER_HEIGHT = 30f;
	private static final float CARD_GAP = 10f;
	private static final float CARD_HEIGHT = 62f;
	private static final float CARD_MIN_WIDTH = 170f;
	private static final float SCROLL_STEP = 30f;

	private static final float FONT_TITLE = 16f;
	private static final float FONT_LABEL = 11f;
	private static final float FONT_SMALL = 9f;

	/** A module card and its bounds from the last rendered frame. */
	private record CardHit(Module module, Rectangle bounds) {
	}

	/** A sidebar category row and its bounds from the last rendered frame. */
	private record CategoryHit(Category category, Rectangle bounds) {
	}

	private final Shader backdropShader = Shader.solid(new Color(10, 10, 14, 150));
	private final Shader panelShader = Shader.solid(new Color(20, 20, 26, 242));
	private final Shader outlineShader = Shader.solid(new Color(255, 255, 255, 20));
	private final Shader dividerShader = Shader.solid(new Color(255, 255, 255, 12));
	private final Shader cardShader = Shader.solid(new Color(255, 255, 255, 7));
	private final Shader cardHoverShader = Shader.solid(new Color(255, 255, 255, 14));
	private final Shader rowHoverShader = Shader.solid(new Color(255, 255, 255, 8));
	private final Shader rowSelectedShader = Shader.solid(new Color(255, 255, 255, 12));
	private final Shader searchShader = Shader.solid(new Color(255, 255, 255, 10));
	private final Shader dimShader = Shader.solid(new Color(150, 150, 160));
	private final Shader offShader = Shader.solid(new Color(90, 90, 100));
	private final Shader scrollbarShader = Shader.solid(new Color(255, 255, 255, 45));

	private boolean open = false;
	private boolean searchFocused = false;
	private String searchText = "";
	private Category selectedCategory;
	private float scrollOffset = 0f;
	private float contentHeight = 0f;

	private int caretTick = 0;
	private boolean caretVisible = true;
	private int backspaceDelayTimer = 0;

	private final List<Category> categories = new ArrayList<>();

	// Hit-test geometry, recomputed every rendered frame.
	private Rectangle panelRect = new Rectangle(0, 0, 0, 0);
	private Rectangle searchRect = new Rectangle(0, 0, 0, 0);
	private Rectangle contentRect = new Rectangle(0, 0, 0, 0);
	private final List<CardHit> cardHits = new ArrayList<>();
	private final List<CategoryHit> categoryHits = new ArrayList<>();

	public FlatMenu() {
		Umbra.getInstance().eventManager.AddListener(KeyDownListener.class, this);
		Umbra.getInstance().eventManager.AddListener(MouseClickListener.class, this);
		Umbra.getInstance().eventManager.AddListener(MouseScrollListener.class, this);
		Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
		Umbra.getInstance().eventManager.AddListener(Render2DListener.class, this);
	}

	public boolean isOpen() {
		return open;
	}

	/**
	 * Sets the open state of the menu. Mouse grab/release is handled by
	 * {@link GuiManager#setFlatMenuOpen(boolean)}, which is the single entry
	 * point for opening and closing.
	 */
	public void setOpen(boolean state) {
		if (open == state)
			return;

		open = state;
		if (state) {
			refreshCategories();
			scrollOffset = 0f;
		} else {
			setSearchFocused(false);
		}
	}

	private void setSearchFocused(boolean focused) {
		if (searchFocused == focused)
			return;

		searchFocused = focused;
		GuiManager.setKeyboardInputActive(focused);
		caretVisible = true;
		caretTick = 0;
		backspaceDelayTimer = 0;
	}

	private void refreshCategories() {
		categories.clear();
		Map<String, Category> allCategories = Category.getAllCategories();
		for (String name : CATEGORY_ORDER) {
			Category category = allCategories.get(name);
			if (category != null)
				categories.add(category);
		}
		for (Category category : allCategories.values()) {
			if (!categories.contains(category))
				categories.add(category);
		}

		if (selectedCategory == null || !categories.contains(selectedCategory))
			selectedCategory = categories.isEmpty() ? null : categories.get(0);
	}

	private List<Module> getVisibleModules() {
		List<Module> result = new ArrayList<>();
		String filter = searchText.trim().toLowerCase();
		for (Module module : Umbra.getInstance().moduleManager.modules) {
			if (!filter.isEmpty()) {
				if (!module.getName().toLowerCase().contains(filter))
					continue;
			} else if (selectedCategory != null && !module.isCategory(selectedCategory)) {
				continue;
			}
			result.add(module);
		}
		return result;
	}

	@Override
	public void onRender(Render2DEvent event) {
		if (!open)
			return;

		// If a vanilla screen took over (death screen, disconnect, ...) the
		// overlay closes itself instead of drawing on top of it.
		if (MC.gui.screen() != null) {
			Umbra.getInstance().guiManager.setFlatMenuOpen(false);
			return;
		}

		Renderer2D renderer = event.getRenderer();
		Matrix3x2fStack matrixStack = renderer.getDrawContext().pose();
		matrixStack.pushMatrix();

		int guiScale = MC.getWindow().calculateScale(MC.options.guiScale().get(), MC.isEnforceUnicode());
		matrixStack.scale(1.0f / guiScale, 1.0f / guiScale);

		renderMenu(renderer);

		matrixStack.popMatrix();
	}

	private void renderMenu(Renderer2D renderer) {
		com.mojang.blaze3d.platform.Window window = MC.getWindow();
		float screenW = window.getScreenWidth();
		float screenH = window.getScreenHeight();
		float mouseX = (float) MC.mouseHandler.xpos();
		float mouseY = (float) MC.mouseHandler.ypos();

		UIFont uiFont = GuiManager.fontSetting.getValue();
		Font titleFont = uiFont.getRenderer(FONT_TITLE, FontManager.WEIGHT_BOLD);
		Font labelFont = uiFont.getRenderer(FONT_LABEL, FontManager.WEIGHT_NORMAL);
		Font labelBoldFont = uiFont.getRenderer(FONT_LABEL, FontManager.WEIGHT_SEMI_BOLD);
		Font smallFont = uiFont.getRenderer(FONT_SMALL, FontManager.WEIGHT_NORMAL);

		Shader textShader = GuiManager.foregroundColor.getValue();
		Shader accentShader = GuiManager.foregroundHeaderColor.getValue();

		// Backdrop
		renderer.drawBox(0, 0, screenW, screenH, backdropShader);

		// Panel
		float panelW = Math.min(PANEL_MAX_WIDTH, screenW - PANEL_MARGIN);
		float panelH = Math.min(PANEL_MAX_HEIGHT, screenH - PANEL_MARGIN);
		float panelX = (screenW - panelW) * 0.5f;
		float panelY = (screenH - panelH) * 0.5f;
		panelRect = new Rectangle(panelX, panelY, panelW, panelH);

		renderer.drawRoundedBox(panelX, panelY, panelW, panelH, PANEL_RADIUS, panelShader);
		renderer.drawRoundedBoxOutline(panelX, panelY, panelW, panelH, PANEL_RADIUS, 1f, outlineShader);

		// Top bar: title + search box
		renderer.drawString("UMBRA", panelX + 18f, centeredTextY(panelY, TOP_BAR_HEIGHT, FONT_TITLE), accentShader,
				titleFont, FONT_TITLE);

		float searchX = panelX + panelW - 16f - SEARCH_WIDTH;
		float searchY = panelY + (TOP_BAR_HEIGHT - SEARCH_HEIGHT) * 0.5f;
		searchRect = new Rectangle(searchX, searchY, SEARCH_WIDTH, SEARCH_HEIGHT);

		renderer.drawRoundedBox(searchX, searchY, SEARCH_WIDTH, SEARCH_HEIGHT, 6f, searchShader);
		if (searchFocused)
			renderer.drawRoundedBoxOutline(searchX, searchY, SEARCH_WIDTH, SEARCH_HEIGHT, 6f, 1f, accentShader);

		renderer.beginClip(new Rectangle(searchX + 8f, searchY, SEARCH_WIDTH - 14f, SEARCH_HEIGHT));
		float searchTextY = centeredTextY(searchY, SEARCH_HEIGHT, FONT_SMALL);
		if (searchText.isEmpty() && !searchFocused) {
			renderer.drawString("Search...", searchX + 8f, searchTextY, dimShader, smallFont, FONT_SMALL);
		} else {
			renderer.drawString(searchText, searchX + 8f, searchTextY, textShader, smallFont, FONT_SMALL);
			if (searchFocused && caretVisible) {
				float caretX = searchX + 8f + Renderer2D.getStringWidth(searchText, smallFont);
				renderer.drawBox(caretX + 1f, searchY + 6f, 1.5f, SEARCH_HEIGHT - 12f, textShader);
			}
		}
		renderer.endClip();

		// Dividers
		renderer.drawBox(panelX, panelY + TOP_BAR_HEIGHT, panelW, 1f, dividerShader);
		renderer.drawBox(panelX + SIDEBAR_WIDTH, panelY + TOP_BAR_HEIGHT, 1f, panelH - TOP_BAR_HEIGHT, dividerShader);

		renderSidebar(renderer, panelX, panelY, mouseX, mouseY, labelFont, labelBoldFont, textShader, accentShader);
		renderContent(renderer, panelX, panelY, panelW, panelH, mouseX, mouseY, labelFont, labelBoldFont, smallFont,
				textShader, accentShader);
	}

	private void renderSidebar(Renderer2D renderer, float panelX, float panelY, float mouseX, float mouseY,
			Font labelFont, Font labelBoldFont, Shader textShader, Shader accentShader) {
		float rowX = panelX + 8f;
		float rowW = SIDEBAR_WIDTH - 16f;
		float rowY = panelY + TOP_BAR_HEIGHT + 10f;

		categoryHits.clear();
		for (Category category : categories) {
			Rectangle rowRect = new Rectangle(rowX, rowY, rowW, CATEGORY_ROW_HEIGHT);
			categoryHits.add(new CategoryHit(category, rowRect));

			boolean selected = category == selectedCategory;
			boolean hovered = rowRect.intersects(mouseX, mouseY);

			if (selected)
				renderer.drawRoundedBox(rowX, rowY, rowW, CATEGORY_ROW_HEIGHT, 6f, rowSelectedShader);
			else if (hovered)
				renderer.drawRoundedBox(rowX, rowY, rowW, CATEGORY_ROW_HEIGHT, 6f, rowHoverShader);

			Shader entryShader = selected ? accentShader : (hovered ? textShader : dimShader);
			renderer.drawTexturedQuad(category.getIcon(), rowX + 10f, rowY + (CATEGORY_ROW_HEIGHT - 13f) * 0.5f, 13f,
					13f, entryShader);
			renderer.drawString(category.getName(), rowX + 32f, centeredTextY(rowY, CATEGORY_ROW_HEIGHT, FONT_LABEL),
					entryShader, selected ? labelBoldFont : labelFont, FONT_LABEL);

			rowY += CATEGORY_ROW_HEIGHT + 2f;
		}
	}

	private void renderContent(Renderer2D renderer, float panelX, float panelY, float panelW, float panelH,
			float mouseX, float mouseY, Font labelFont, Font labelBoldFont, Font smallFont, Shader textShader,
			Shader accentShader) {
		float contentX = panelX + SIDEBAR_WIDTH + 1f;
		float contentY = panelY + TOP_BAR_HEIGHT + 1f;
		float contentW = panelW - SIDEBAR_WIDTH - 1f;
		float contentH = panelH - TOP_BAR_HEIGHT - 1f;
		contentRect = new Rectangle(contentX, contentY, contentW, contentH);

		List<Module> visible = getVisibleModules();

		// Header line
		String header = !searchText.isEmpty() ? "Search results (" + visible.size() + ")"
				: (selectedCategory != null ? selectedCategory.getName() + " (" + visible.size() + ")" : "");
		renderer.drawString(header, contentX + CONTENT_PADDING,
				centeredTextY(contentY, HEADER_HEIGHT, FONT_LABEL), textShader, labelBoldFont, FONT_LABEL);

		if (visible.isEmpty()) {
			String empty = "No modules found";
			float emptyWidth = Renderer2D.getStringWidth(empty, labelFont);
			renderer.drawString(empty, contentX + (contentW - emptyWidth) * 0.5f,
					centeredTextY(contentY, contentH, FONT_LABEL), dimShader, labelFont, FONT_LABEL);
			cardHits.clear();
			contentHeight = 0f;
			return;
		}

		// Card grid
		float gridX = contentX + CONTENT_PADDING;
		float gridY = contentY + HEADER_HEIGHT;
		float availW = contentW - CONTENT_PADDING * 2f;
		int columns = Math.max(1, (int) Math.floor((availW + CARD_GAP) / (CARD_MIN_WIDTH + CARD_GAP)));
		float cardW = (availW - (columns - 1) * CARD_GAP) / columns;

		int rows = (visible.size() + columns - 1) / columns;
		contentHeight = HEADER_HEIGHT + CONTENT_PADDING + rows * CARD_HEIGHT + (rows - 1) * CARD_GAP
				+ CONTENT_PADDING;
		float maxScroll = Math.max(0f, contentHeight - contentH);
		scrollOffset = Math.max(0f, Math.min(scrollOffset, maxScroll));

		cardHits.clear();
		renderer.beginClip(new Rectangle(contentX, gridY, contentW, contentY + contentH - gridY));
		for (int i = 0; i < visible.size(); i++) {
			float cardX = gridX + (i % columns) * (cardW + CARD_GAP);
			float cardY = gridY + CONTENT_PADDING - scrollOffset + (i / columns) * (CARD_HEIGHT + CARD_GAP);
			drawCard(renderer, visible.get(i), cardX, cardY, cardW, mouseX, mouseY, labelBoldFont, smallFont,
					textShader, accentShader);
		}
		renderer.endClip();

		// Scrollbar
		if (maxScroll > 0f) {
			float trackH = contentY + contentH - gridY - 8f;
			float thumbH = Math.max(24f, trackH * (trackH / contentHeight));
			float thumbY = gridY + 4f + (trackH - thumbH) * (scrollOffset / maxScroll);
			renderer.drawRoundedBox(contentX + contentW - 5f, thumbY, 3f, thumbH, 1.5f, scrollbarShader);
		}
	}

	private void drawCard(Renderer2D renderer, Module module, float cardX, float cardY, float cardW, float mouseX,
			float mouseY, Font nameFont, Font smallFont, Shader textShader, Shader accentShader) {
		Rectangle cardRect = new Rectangle(cardX, cardY, cardW, CARD_HEIGHT);
		cardHits.add(new CardHit(module, cardRect));

		boolean hovered = cardRect.intersects(mouseX, mouseY);
		boolean enabled = module.state.getValue();
		Shader stateShader = enabled ? accentShader : offShader;

		renderer.drawRoundedBox(cardX, cardY, cardW, CARD_HEIGHT, 6f, hovered ? cardHoverShader : cardShader);

		// State accent bar on the left edge
		renderer.drawRoundedBox(cardX, cardY + 8f, 3f, CARD_HEIGHT - 16f, 1.5f, stateShader);

		// Module name
		String name = truncateToWidth(module.getName(), nameFont, cardW - 50f);
		renderer.drawString(name, cardX + 12f, cardY + 9f, textShader, nameFont, FONT_LABEL);

		// State pill, top right
		float pillW = 22f;
		float pillH = 9f;
		renderer.drawRoundedBox(cardX + cardW - pillW - 8f, cardY + 8f, pillW, pillH, pillH * 0.5f, stateShader);

		// Description
		String description = module.getDescription() == null ? "" : module.getDescription();
		renderer.drawString(truncateToWidth(description, smallFont, cardW - 22f), cardX + 12f, cardY + 26f,
				dimShader, smallFont, FONT_SMALL);

		// Keybind, bottom right
		Key bind = module.keyBind.getValue();
		if (bind.getValue() != GLFW.GLFW_KEY_UNKNOWN) {
			String bindName = "[" + bind.getDisplayName().getString() + "]";
			float bindWidth = Renderer2D.getStringWidth(bindName, smallFont);
			renderer.drawString(bindName, cardX + cardW - bindWidth - 8f, cardY + CARD_HEIGHT - 16f, dimShader,
					smallFont, FONT_SMALL);
		}
	}

	@Override
	public void onMouseClick(MouseClickEvent event) {
		if (!open)
			return;

		// The menu is modal; the game must not see any clicks while it is open.
		event.cancel();

		if (event.button != MouseButton.LEFT || event.action != MouseAction.DOWN)
			return;

		float mouseX = (float) event.mouseX;
		float mouseY = (float) event.mouseY;

		if (searchRect.intersects(mouseX, mouseY)) {
			setSearchFocused(true);
			return;
		}
		if (searchFocused)
			setSearchFocused(false);

		for (CategoryHit hit : categoryHits) {
			if (hit.bounds().intersects(mouseX, mouseY)) {
				selectedCategory = hit.category();
				searchText = "";
				scrollOffset = 0f;
				return;
			}
		}

		if (contentRect.intersects(mouseX, mouseY)) {
			for (CardHit hit : cardHits) {
				if (hit.bounds().intersects(mouseX, mouseY)) {
					hit.module().toggle();
					return;
				}
			}
			return;
		}

		// Clicking the dimmed backdrop closes the menu.
		if (!panelRect.intersects(mouseX, mouseY))
			Umbra.getInstance().guiManager.setFlatMenuOpen(false);
	}

	@Override
	public void onMouseScroll(MouseScrollEvent event) {
		if (!open)
			return;

		float maxScroll = Math.max(0f, contentHeight - contentRect.height());
		scrollOffset = Math.max(0f, Math.min(maxScroll, scrollOffset - (float) event.GetVertical() * SCROLL_STEP));
		event.cancel();
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		if (!open)
			return;

		int key = event.GetKey();

		// ESC unfocuses the search box first, then closes the menu.
		if (key == GLFW.GLFW_KEY_ESCAPE) {
			if (searchFocused)
				setSearchFocused(false);
			else
				Umbra.getInstance().guiManager.setFlatMenuOpen(false);
			event.cancel();
			return;
		}

		// The menu key closes the menu again. When the search box is not
		// focused GuiManager's key handler toggles the menu before this
		// listener runs; this covers the focused case.
		if (key == Umbra.getInstance().guiManager.normalGuiButton.getValue().getValue()) {
			setSearchFocused(false);
			Umbra.getInstance().guiManager.setFlatMenuOpen(false);
			event.cancel();
			return;
		}

		if (!searchFocused)
			return;

		// Search box text editing. Every key is swallowed so neither the game
		// nor other listeners react to typing.
		event.cancel();

		long windowHandle = MC.getWindow().handle();
		boolean ctrlDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
				|| GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;

		if (ctrlDown) {
			if (key == GLFW.GLFW_KEY_V) {
				String clipboard = GLFW.glfwGetClipboardString(windowHandle);
				if (clipboard != null && !clipboard.isEmpty())
					setSearchText(searchText + clipboard);
			}
			return;
		}

		if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
			setSearchFocused(false);
		} else if (key == GLFW.GLFW_KEY_BACKSPACE) {
			if (!searchText.isEmpty())
				setSearchText(searchText.substring(0, searchText.length() - 1));
		} else if (key == GLFW.GLFW_KEY_SPACE) {
			setSearchText(searchText + ' ');
		} else if (keyIsValid(key)) {
			String keyName = GLFW.glfwGetKeyName(key, event.GetScanCode());
			if (keyName != null && !keyName.isEmpty()) {
				char c = keyName.charAt(0);
				boolean shiftDown = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
						|| GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
				setSearchText(searchText + (shiftDown ? Character.toUpperCase(c) : Character.toLowerCase(c)));
			}
		}
	}

	private void setSearchText(String text) {
		searchText = text;
		scrollOffset = 0f;
	}

	private boolean keyIsValid(int key) {
		return key == 45 || (key >= 48 && key <= 57) || (key >= 65 && key <= 90) || (key >= 97 && key <= 122);
	}

	@Override
	public void onTick(TickEvent.Pre event) {
		if (!open || !searchFocused)
			return;

		caretTick++;
		if (caretTick >= 10) {
			caretVisible = !caretVisible;
			caretTick = 0;
		}

		// Continuous backspace deletion while the key is held down.
		long windowHandle = MC.getWindow().handle();
		if (GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_BACKSPACE) == GLFW.GLFW_PRESS) {
			backspaceDelayTimer++;
			// Initial delay threshold of 15 ticks, then repeats every 2 ticks
			if (backspaceDelayTimer > 15 && (backspaceDelayTimer - 15) % 2 == 0 && !searchText.isEmpty())
				setSearchText(searchText.substring(0, searchText.length() - 1));
		} else {
			backspaceDelayTimer = 0;
		}
	}

	@Override
	public void onTick(TickEvent.Post event) {

	}

	/**
	 * Y position for text of the given font size vertically centered within a
	 * box, using the same metrics as {@link net.umbra.gui.components.StringComponent}.
	 */
	private static float centeredTextY(float boxY, float boxHeight, float fontSize) {
		float lineHeight = Math.round(fontSize * 1.5f);
		float drawOffset = (fontSize - 6f) * 7f / 6f;
		return boxY + (boxHeight - lineHeight) * 0.5f + drawOffset;
	}

	private static String truncateToWidth(String text, Font font, float maxWidth) {
		if (font.width(text) <= maxWidth)
			return text;

		String ellipsis = "...";
		StringBuilder builder = new StringBuilder(text);
		while (builder.length() > 0 && font.width(builder + ellipsis) > maxWidth)
			builder.setLength(builder.length() - 1);
		return builder + ellipsis;
	}
}
