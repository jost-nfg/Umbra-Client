/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.events;

import java.util.ArrayList;

import net.umbra.event.listeners.AbstractListener;

public abstract class AbstractEvent {
	boolean isCancelled;

	public AbstractEvent() {
		isCancelled = false;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public void cancel() {
		isCancelled = true;
	}

	public void setCancelled(boolean state) {
		isCancelled = state;
	}

	public abstract void Fire(ArrayList<? extends AbstractListener> listeners);

	public abstract <T extends AbstractListener> Class<T> GetListenerClassType();
}
