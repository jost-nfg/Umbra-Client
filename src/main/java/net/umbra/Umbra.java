/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra;

import net.fabricmc.api.ModInitializer;

/**
 * Initializes and provides access to the Umbra Client singleton.
 */
public class Umbra implements ModInitializer {
	private static UmbraClient INSTANCE;

	@Override
	public void onInitialize() {
		INSTANCE = new UmbraClient();
		INSTANCE.Initialize();
	}

	/**
	 * @return Singleton instance of UmbraClient.
	 */
	public static UmbraClient getInstance() {
		return INSTANCE;
	}
}
