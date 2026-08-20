/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.listeners;

import net.umbra.event.events.ItemUsedEvent;

public interface ItemUsedListener extends AbstractListener {
	void onItemUsed(ItemUsedEvent.Pre event);

	void onItemUsed(ItemUsedEvent.Post event);
}
