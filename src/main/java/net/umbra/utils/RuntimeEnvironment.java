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

package net.umbra.utils;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * Detects the runtime environment the client is executing in.
 *
 * Umbra targets Lunar Client 26.2, which loads this mod through its bundled
 * Fabric loader (the "ichor"/Genesis pipeline). Lunar itself is not a Fabric
 * mod, so it cannot be detected via the mod list; instead we probe for the
 * presence of Lunar's own classes (package com.moonsworth.*) on the classpath.
 */
public final class RuntimeEnvironment {
	private static final Logger LOGGER = LogUtils.getLogger();

	/** Classes/packages shipped by Lunar Client that are visible to mod classloaders. */
	private static final String[] LUNAR_CLASS_PROBES = {
			"com.moonsworth.webosr.NativeHandle", // Ultralight web-UI bridge (unobfuscated)
			"com.moonsworth.lunar.genesis.Genesis", // ichor/Genesis bootstrap
			"com.moonsworth.lunar.client.LunarClient" };

	private static volatile Boolean lunar = null;

	private RuntimeEnvironment() {
	}

	/**
	 * Returns true if the client appears to be running inside Lunar Client.
	 * The result is computed once and cached.
	 */
	public static boolean isLunar() {
		Boolean result = lunar;
		if (result == null) {
			result = detectLunar();
			lunar = result;
			LOGGER.info("[Umbra] Runtime environment: {}", result ? "Lunar Client" : "standalone Fabric");
		}
		return result;
	}

	private static boolean detectLunar() {
		ClassLoader cl = RuntimeEnvironment.class.getClassLoader();

		for (String probe : LUNAR_CLASS_PROBES) {
			try {
				Class.forName(probe, false, cl);
				return true;
			} catch (Throwable ignored) {
			}
		}

		// Fall back to checking whether any com.moonsworth package is already loaded.
		try {
			for (Package p : Package.getPackages()) {
				if (p.getName().startsWith("com.moonsworth"))
					return true;
			}
		} catch (Throwable ignored) {
		}

		// Last resort: ichor passes its module list as a launch argument.
		try {
			String cmd = System.getProperty("sun.java.command");
			if (cmd != null && (cmd.contains("ichor") || cmd.contains("moonsworth")))
				return true;
		} catch (Throwable ignored) {
		}

		return false;
	}
}
