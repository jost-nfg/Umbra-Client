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
import net.umbra.event.listeners.PlaySoundListener;
import net.minecraft.client.resources.sounds.SoundInstance;

public class PlaySoundEvent extends AbstractEvent {
	private final SoundInstance soundInstance;

	public PlaySoundEvent(SoundInstance soundInstance) {
		this.soundInstance = soundInstance;
	}

	public SoundInstance getSoundInstance() {
		return soundInstance;
	}

	@Override
	public void Fire(ArrayList<? extends AbstractListener> listeners) {
		for (AbstractListener listener : listeners) {
			PlaySoundListener playSoundListener = (PlaySoundListener) listener;
			playSoundListener.onPlaySound(this);
		}
	}

	@Override
	public Class<PlaySoundListener> GetListenerClassType() {
		return PlaySoundListener.class;
	}
}
