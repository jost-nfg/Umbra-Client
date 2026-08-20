package net.umbra.event.listeners;

import net.umbra.event.events.StartAttackEvent;

public interface StartAttackListener extends AbstractListener {
	void onStartAttack(StartAttackEvent event);
}
