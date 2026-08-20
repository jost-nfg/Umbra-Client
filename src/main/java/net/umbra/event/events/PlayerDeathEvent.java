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
import net.umbra.event.listeners.PlayerDeathListener;

public class PlayerDeathEvent extends AbstractEvent {
	public PlayerDeathEvent() {
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			PlayerDeathListener playerDeathListener = (PlayerDeathListener) listener;
			playerDeathListener.onPlayerDeath(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<PlayerDeathListener> GetListenerClassType() {
		return PlayerDeathListener.class;
	}
}