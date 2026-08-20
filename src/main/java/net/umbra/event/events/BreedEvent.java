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
import net.umbra.event.listeners.BreedListener;
import net.minecraft.world.entity.Entity;

public class BreedEvent extends EntityEvent {

	public BreedEvent(Entity entity, byte id)  {
		super(entity, id);
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			BreedListener breedListener = (BreedListener) listener;
			breedListener.onBreed(this);
		}
	}

	@Override
	public Class<BreedListener> GetListenerClassType() {
		return BreedListener.class;
	}
}
