/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.gui.screens.proxy;

import java.util.ArrayList;

import net.umbra.Umbra;
import net.umbra.gui.screens.UmbraPanorama;
import net.umbra.managers.proxymanager.Socks5Proxy;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ProxyScreen extends Screen {
	protected static final UmbraPanorama UMBRA_ROTATING_PANORAMA_RENDERER = new UmbraPanorama();

	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private final Screen parentScreen;
	private ProxySelectionList proxyListSelector;
	private Button editButton;
	private Button deleteButton;

	public ProxyScreen(Screen parentScreen) {
		super(Component.nullToEmpty("Proxy Manager"));
		this.parentScreen = parentScreen;
	}

	public void init() {
		super.init();

		proxyListSelector = new ProxySelectionList(this, minecraft, width, height, 32, 52);
		proxyListSelector.updateProxies();
		layout.addToContents(proxyListSelector);

		LinearLayout buttonRow = LinearLayout.horizontal().spacing(4);
		buttonRow.addChild(Button.builder(Component.nullToEmpty("Add Proxy"), b -> minecraft.gui.setScreen(new AddProxyScreen(this)))
				.width(100).build());

		editButton = Button.builder(Component.nullToEmpty("Edit Proxy"), b -> editSelected())
				.width(100).build();
		editButton.active = false;
		buttonRow.addChild(editButton);

		deleteButton = Button.builder(Component.nullToEmpty("Delete Proxy"), b -> deleteSelected())
				.width(100).build();
		deleteButton.active = false;
		buttonRow.addChild(deleteButton);

		buttonRow.addChild(Button.builder(Component.nullToEmpty("Cancel"), b -> minecraft.gui.setScreen(parentScreen))
				.width(100).build());

		layout.addToFooter(buttonRow);
		layout.arrangeElements();
		layout.visitWidgets(this::addRenderableWidget);
		proxyListSelector.updateSize(width, layout);
	}

	@Override
	protected void repositionElements() {
		layout.arrangeElements();
		if (proxyListSelector != null) {
			proxyListSelector.updateSize(width, layout);
		}
	}

	public ArrayList<Socks5Proxy> getProxyList() {
		return Umbra.getInstance().proxyManager.getProxies();
	}

	public void refreshProxyList() {
		minecraft.gui.setScreen(new ProxyScreen(parentScreen));
	}

	public void setSelected(ProxySelectionList.Entry selected) {
		proxyListSelector.setSelected(selected);
		setEdittable();
	}

	public void editSelected() {
		Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) proxyListSelector.getSelected()).getProxyData();
		if (proxy == null) {
			return;
		}
		minecraft.gui.setScreen(new EditProxyScreen(this, proxy));
	}

	public void deleteSelected() {
		Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) proxyListSelector.getSelected()).getProxyData();
		if (proxy == null) {
			return;
		}
		Umbra.getInstance().proxyManager.removeProxy(proxy);
		refreshProxyList();
	}

	protected void setEdittable() {
		editButton.active = true;
		deleteButton.active = true;
	}

	public boolean isActiveProxy(Socks5Proxy proxy) {
		return Umbra.getInstance().proxyManager.getActiveProxy() == proxy;
	}

	public void setActive() {
		ProxySelectionList.Entry proxyListSelectorSelectedOrNull = proxyListSelector.getSelected();

		if (proxyListSelectorSelectedOrNull == null) {
			return;
		}

		Socks5Proxy proxy = ((ProxySelectionList.NormalEntry) proxyListSelectorSelectedOrNull).getProxyData();
		Umbra.getInstance().proxyManager.setActiveProxy(proxy);
	}

	public void resetActive() {
		Umbra.getInstance().proxyManager.setActiveProxy(null);
	}

	@Override
	protected void extractPanorama(final GuiGraphicsExtractor graphics, final float a){
		try {
			UMBRA_ROTATING_PANORAMA_RENDERER.extractRenderState(graphics, this.width, this.height, true);
		} catch (IllegalStateException e) {
		}
	}
}
