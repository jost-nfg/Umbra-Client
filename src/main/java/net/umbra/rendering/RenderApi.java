package net.umbra.rendering;

import java.util.Locale;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;

import net.umbra.UmbraClient;

/**
 * Represents a set of known / implemented Render APIS
 * for use with the rendering pipelines.
 */
public enum RenderApi {
	OPENGL,
	VULKAN;

	private static RenderApi cached;

	/**
	 * Returns the active rendering backend.
	 */
	public static RenderApi current() {
		if (cached == null) {
			GpuDevice device = RenderSystem.tryGetDevice();
			if (device == null) {
				// Device not initialized yet. Don't cache it yet.
				return OPENGL;
			}
			String backend = device.getDeviceInfo().backendName();
			cached = backend != null && backend.toLowerCase(Locale.ROOT).contains("vulkan") ? VULKAN : OPENGL;
			UmbraClient.LOGGER.info("[Umbra] Render API: " + (cached == VULKAN ? "Vulkan" : "OpenGL"));
		}
		return cached;
	}
}
