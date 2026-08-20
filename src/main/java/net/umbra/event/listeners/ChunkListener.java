/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.listeners;

import net.umbra.event.events.ChunkEvent;

public interface ChunkListener extends AbstractListener {
	void onChunkLoaded(ChunkEvent.Loaded event);
	void onChunkUnloaded(ChunkEvent.Unloaded event);
}
