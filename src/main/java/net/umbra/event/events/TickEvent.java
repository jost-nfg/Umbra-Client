/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.event.events;

import java.util.ArrayList;
import java.util.List;

import net.umbra.event.listeners.AbstractListener;
import net.umbra.event.listeners.TickListener;

public class TickEvent {
	public static class Pre extends AbstractEvent {
		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : List.copyOf(listeners)) {
				TickListener tickListener = (TickListener) listener;
				tickListener.onTick(this);
			}
		}

		@Override
		public Class<TickListener> GetListenerClassType() {
			return TickListener.class;
		}
	}

	public static class Post extends AbstractEvent {
		@Override
		public void Fire(ArrayList<? extends AbstractListener> listeners) {
			for (AbstractListener listener : listeners) {
				TickListener tickListener = (TickListener) listener;
				tickListener.onTick(this);
			}
		}

		@Override
		public Class<TickListener> GetListenerClassType() {
			return TickListener.class;
		}
	}
}
