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
import net.umbra.event.listeners.PlayerHealthListener;
import net.minecraft.world.damagesource.DamageSource;

public class PlayerHealthEvent extends AbstractEvent {
	private final float health;
	private final DamageSource source;

	public PlayerHealthEvent(DamageSource source, float health) {
		this.source = source;
		this.health = health;
	}

	public float getHealth() {
		return health;
	}

	public DamageSource getDamageSource() {
		return source;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			PlayerHealthListener playerHealthListener = (PlayerHealthListener) listener;
			playerHealthListener.onHealthChanged(this);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class<PlayerHealthListener> GetListenerClassType() {
		return PlayerHealthListener.class;
	}
}