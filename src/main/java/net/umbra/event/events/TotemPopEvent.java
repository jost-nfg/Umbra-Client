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
import net.umbra.event.listeners.TotemPopListener;
import net.minecraft.world.entity.player.Player;

public class TotemPopEvent extends AbstractEvent {
	private final Player entity;
	private final int pops;

	public TotemPopEvent(Player entity, int pops) {
		this.entity = entity;
		this.pops = pops;
	}

	public Player getEntity() {
		return entity;
	}

	public int getPops() {
		return pops;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			TotemPopListener totemPopListener = (TotemPopListener) listener;
			totemPopListener.onTotemPop(this);

			if (isCancelled)
				break;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<TotemPopListener> GetListenerClassType() {
		return TotemPopListener.class;
	}
}
