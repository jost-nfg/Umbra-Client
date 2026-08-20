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
import net.umbra.event.listeners.ParticleListener;
import net.minecraft.core.particles.ParticleOptions;

public class ParticleEvent extends AbstractEvent {
	private final ParticleOptions particleEffect;

	public ParticleEvent(ParticleOptions particleEffect) {
		this.particleEffect = particleEffect;
	}

	public ParticleOptions getParticleEffect() {
		return particleEffect;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			ParticleListener particleListener = (ParticleListener) listener;
			particleListener.onParticle(this);
		}
	}

	@Override
	public Class<ParticleListener> GetListenerClassType() {
		return ParticleListener.class;
	}
}
