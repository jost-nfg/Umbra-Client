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
import net.umbra.event.listeners.GameLeftListener;

public class GameLeftEvent extends AbstractEvent {

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			GameLeftListener gameLeftListener = (GameLeftListener) listener;
			gameLeftListener.onGameLeft(this);
		}
	}

	@Override
	public Class<GameLeftListener> GetListenerClassType() {
		return GameLeftListener.class;
	}
}
