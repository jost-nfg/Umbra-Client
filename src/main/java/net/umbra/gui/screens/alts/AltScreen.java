/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.screens.alts;

import java.util.List;

import net.umbra.Umbra;
import net.umbra.gui.screens.UmbraPanorama;
import net.umbra.managers.altmanager.Alt;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AltScreen extends Screen {
	protected static final UmbraPanorama UMBRA_ROTATING_PANORAMA_RENDERER = new UmbraPanorama();

	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 33, 64);
	private final Screen parentScreen;
	private Button editButton;
	private Button deleteButton;
	private AltSelectionList altListSelector;

	public AltScreen(Screen parentScreen) {
		super(Component.nullToEmpty("Alt Manager"));
		this.parentScreen = parentScreen;
	}

	public void init() {
		super.init();

		altListSelector = new AltSelectionList(this, minecraft, width, height, 32, 36);
		altListSelector.updateAlts();
		layout.addToContents(altListSelector);

		LinearLayout topRow = LinearLayout.horizontal().spacing(4);
		topRow.addChild(Button.builder(Component.nullToEmpty("Direct Login"), b -> minecraft.gui.setScreen(new DirectLoginAltScreen(this)))
				.width(100).build());
		topRow.addChild(Button.builder(Component.nullToEmpty("Add Alt"), b -> minecraft.gui.setScreen(new AddAltScreen(this)))
				.width(100).build());

		LinearLayout bottomRow = LinearLayout.horizontal().spacing(4);
		deleteButton = Button.builder(Component.nullToEmpty("Delete Alt"), b -> deleteSelected())
				.width(100).build();
		deleteButton.active = false;
		bottomRow.addChild(deleteButton);

		editButton = Button.builder(Component.nullToEmpty("Edit Alt"), b -> editSelected())
				.width(100).build();
		editButton.active = false;
		bottomRow.addChild(editButton);

		bottomRow.addChild(Button.builder(Component.nullToEmpty("Cancel"), b -> minecraft.gui.setScreen(parentScreen))
				.width(100).build());

		LinearLayout footer = layout.addToFooter(LinearLayout.vertical().spacing(4));
		footer.addChild(topRow);
		footer.addChild(bottomRow);

		layout.arrangeElements();
		layout.visitWidgets(this::addRenderableWidget);
		altListSelector.updateSize(width, layout);
	}

	@Override
	protected void repositionElements() {
		layout.arrangeElements();
		if (altListSelector != null) {
			altListSelector.updateSize(width, layout);
		}
	}

	@Override
	public void tick() {
		AltSelectionList.Entry altselectionlist$entry = altListSelector.getSelected();
		if (altselectionlist$entry == null) {
		}
	}

    @Override
    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.centeredText(font,
				"Currently Logged Into: " + Minecraft.getInstance().getUser().getName(), width / 2, 20,
				0xFFFFFFFF);
    }

	public List<Alt> getAltList() {
		return Umbra.getInstance().altManager.getAlts();
	}

	public void refreshAltList() {
		minecraft.gui.setScreen(new AltScreen(parentScreen));
	}

	public void setSelected(AltSelectionList.Entry selected) {
		altListSelector.setSelected(selected);
		setEdittable();
	}

	protected void setEdittable() {
		editButton.active = true;
		deleteButton.active = true;
	}

	public void loginToSelected() {
		AltSelectionList.Entry altselectionlist$entry = altListSelector.getSelected();
		if (altselectionlist$entry == null) {
			return;
		}

		Alt alt = ((AltSelectionList.NormalEntry) altselectionlist$entry).getAltData();
		if (alt.isCracked()) {
			Umbra.getInstance().altManager.loginCracked(alt.getEmail());
		} else {
			Umbra.getInstance().altManager.login(alt);
		}
	}

	public void editSelected() {
		Alt alt = ((AltSelectionList.NormalEntry) altListSelector.getSelected()).getAltData();
		if (alt == null) {
			return;
		}
		minecraft.gui.setScreen(new EditAltScreen(this, alt));
	}

	public void deleteSelected() {
		Alt alt = ((AltSelectionList.NormalEntry) altListSelector.getSelected()).getAltData();
		if (alt == null) {
			return;
		}
		Umbra.getInstance().altManager.removeAlt(alt);
		refreshAltList();
	}

	@Override
	protected void extractPanorama(final GuiGraphicsExtractor graphics, final float a){
		try {
			UMBRA_ROTATING_PANORAMA_RENDERER.extractRenderState(graphics, this.width, this.height, true);
		} catch (IllegalStateException e) {
		}
	}
}
