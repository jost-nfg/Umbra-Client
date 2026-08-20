/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * A class to represent Umbra Client and all of its functions.
 */
package net.umbra;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.umbra.api.IAddon;
import net.umbra.gui.GuiManager;
import net.umbra.gui.font.FontManager;
import net.umbra.managers.CombatManager;
import net.umbra.managers.CommandManager;
import net.umbra.managers.EntityManager;
import net.umbra.managers.EventManager;
import net.umbra.managers.ModuleManager;
import net.umbra.managers.SettingManager;
import net.umbra.managers.altmanager.AltManager;
import net.umbra.managers.macros.MacroManager;
import net.umbra.managers.proxymanager.ProxyManager;
import net.umbra.managers.rotation.RotationManager;
import net.umbra.mixin.interfaces.IMinecraft;
import net.umbra.module.Module;
import net.umbra.rendering.AbstractRenderer;
import net.umbra.rendering.Compositor;
import net.umbra.rendering.Renderer2D;
import net.umbra.rendering.Renderer3D;
import net.umbra.rendering.shaders.ShaderManager;
import net.umbra.settings.friends.FriendsList;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class UmbraClient {
	public static final String UMBRA_VERSION = "1.4.5";
	public static final KeyMapping.Category UMBRA_CATEGORY =
			new KeyMapping.Category(Identifier.fromNamespaceAndPath("umbra", "category"));

	public static Minecraft MC;
	public static IMinecraft IMC;

	// Systems
	public RotationManager rotationManager;
	public ModuleManager moduleManager;
	public CommandManager commandManager;
	public AltManager altManager;
	public ProxyManager proxyManager;
	public GuiManager guiManager;
	public FontManager fontManager;
	public CombatManager combatManager;
	public SettingManager settingManager;
	public FriendsList friendsList;
	public EventManager eventManager;
	public MacroManager macroManager;
	public EntityManager entityManager;
	public ShaderManager shaderManager;
	public Renderer2D render2D;
	public Renderer3D render3D;
	public Compositor compositor;

	public static List<IAddon> addons = new ArrayList<>();
	public static Logger LOGGER;

	/** True once loadAssets() has completed and all managers/renderers exist. */
	private volatile boolean fullyLoaded = false;

	/**
	 * Initializes Umbra Client and creates sub-systems.
	 */
	public void Initialize() {
		// Gets instance of Minecraft
		MC = Minecraft.getInstance();
		IMC = (IMinecraft) MC;
		LOGGER = LogUtils.getLogger();

		// The event manager must exist from the very start: Lunar Client creates
		// entities (e.g. a fake LocalPlayer for its menu) before resources finish
		// loading, and mixin-injected event hooks must never see a null manager.
		eventManager = new EventManager();
	}

	/**
	 * Returns true once the client has finished loading all of its systems.
	 * Mixin hooks that fire before that point must treat the client as absent.
	 */
	public boolean isFullyLoaded() {
		return fullyLoaded;
	}

	/**
	 * Initializes systems and loads any assets.
	 */
	public void loadAssets() {
		LOGGER.info("[Umbra] Starting Client");
		LOGGER.info("[Umbra] Detected runtime environment: {}",
				net.umbra.utils.RuntimeEnvironment.isLunar() ? "Lunar Client" : "standalone Fabric");

		// Register any addons.
		LogUtils.getLogger().info("[Umbra] Starting addon initialization");
		for (EntrypointContainer<IAddon> entrypoint : FabricLoader.getInstance().getEntrypointContainers("umbra",
				IAddon.class)) {
			IAddon addon = entrypoint.getEntrypoint();

			try {
				LOGGER.info("[Umbra] Initializing addon: " + addon.getName());
				addon.onInitialize();
				LOGGER.info("[Umbra] Addon initialized: " + addon.getName());
			} catch (Throwable e) {
				LOGGER.error("Error initializing addon: " + addon.getName(), e.getMessage());
			}

			addons.add(addon);
		}

		LOGGER.info("[Umbra] Reading Settings");
		settingManager = new SettingManager();

		LOGGER.info("[Umbra] Reading Friends List");
		friendsList = new FriendsList();

		LOGGER.info("[Umbra] Initializing Commands");
		commandManager = new CommandManager(addons);

		LOGGER.info("[Umbra] Initializing Font Manager");
		fontManager = new FontManager();
		fontManager.Initialize();

		LOGGER.info("[Umbra] Initializing Combat Manager");
		combatManager = new CombatManager();

		LOGGER.info("[Umbra] Initializing Entity Manager");
		entityManager = new EntityManager();

		LOGGER.info("[Umbra] Initializing Macro Manager");
		macroManager = new MacroManager();

		LOGGER.info("[Umbra] Initializing Shader Effects");
		// Fail-soft: if the render backend differs under Lunar Client, a shader or
		// renderer failure must not brick the whole client initialization.
		try {
			shaderManager = new ShaderManager();
		} catch (Throwable t) {
			LOGGER.error("[Umbra] Shader initialization failed; custom shaders will be unavailable", t);
		}

		try {
			LOGGER.info("[Umbra] Initializing Renderers");
			compositor = new Compositor();
			render2D = new Renderer2D();
			render3D = new Renderer3D();
		} catch (Throwable t) {
			LOGGER.error("[Umbra] Renderer initialization failed; custom rendering will be unavailable", t);
		}

		LOGGER.info("[Umbra] Initializing Rotation Manager");
		rotationManager = new RotationManager();
		
		LOGGER.info("[Umbra] Initializing Modules");
		moduleManager = new ModuleManager(addons);
		
		LOGGER.info("[Umbra] Initializing GUI");
		guiManager = new GuiManager();
		guiManager.Initialize();

		LOGGER.info("[Umbra] Loading Settings");
		SettingManager.loadGlobalSettings();
		SettingManager.loadSettings();

		LOGGER.info("[Umbra] Initializing Alt Manager");
		altManager = new AltManager();

		LOGGER.info("[Umbra] Initializing Proxy Manager");
		proxyManager = new ProxyManager();

		LOGGER.info("[Umbra] Registering Shutdown Hook");
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				endClient();
			} catch (Exception e) {
				LOGGER.error("[Umbra] Error during shutdown: ", e);
			}
		}));

		fullyLoaded = true;
		LOGGER.info("[Umbra] Umbra-chan initialized and ready to play!");
	}

	/**
	 * Called when the client is shutting down. Saves persistent data.
	 */
	public void endClient() {
		if (!fullyLoaded)
			return;
		LOGGER.info("[Umbra] Shutting down");
		try {
			SettingManager.saveSettings();
			altManager.saveAlts();
			friendsList.save();
			macroManager.save();
			moduleManager.modules.forEach(Module::onDisable);
		} catch (Exception e) {
			LOGGER.error("[Umbra] Error saving data", e);
		}

		try {
			if (render2D != null) render2D.close();
			if (render3D != null) render3D.close();
			if (compositor != null) compositor.close();
			AbstractRenderer.closeSharedResources();
		} catch (Exception e) {
			LOGGER.error("[Umbra] Error releasing renderer resources", e);
		}
	}
}
