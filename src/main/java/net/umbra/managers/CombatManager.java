/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.managers;

import net.umbra.Umbra;
import net.umbra.event.events.ReceivePacketEvent;
import net.umbra.event.events.TickEvent;
import net.umbra.event.events.TotemPopEvent;
import net.umbra.event.listeners.ReceivePacketListener;
import net.umbra.event.listeners.TickListener;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static net.umbra.UmbraClient.MC;

public class CombatManager implements TickListener, ReceivePacketListener {
    public HashMap<String, Integer> popList = new HashMap<>();

    public CombatManager() {
        Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
        Umbra.getInstance().eventManager.AddListener(ReceivePacketListener.class, this);
    }

    @Override
    public void onReceivePacket(ReceivePacketEvent event) {
        if (event.GetPacket() instanceof ClientboundEntityEventPacket entityStatusS2CPacket) {
            if (entityStatusS2CPacket.getEventId() == EntityEvent.PROTECTED_FROM_DEATH) {
                Entity entity = entityStatusS2CPacket.getEntity(MC.level);

                if (!(entity instanceof Player)) return;

                if (popList == null) {
                    popList = new HashMap<>();
                }

                if (popList.get(entity.getName().getString()) == null) {
                    popList.put(entity.getName().getString(), 1);
                } else if (popList.get(entity.getName().getString()) != null) {
                    popList.put(entity.getName().getString(), popList.get(entity.getName().getString()) + 1);
                }

                Umbra.getInstance().eventManager.Fire(new TotemPopEvent((Player) entity, popList.get(entity.getName().getString())));
            }
        }
    }

    @Override
    public void onTick(TickEvent.Pre event) {

    }
    
    @Override
    public void onTick(TickEvent.Post event) {
        for (Player player : MC.level.players()) {
            if (player.getHealth() <= 0 && popList.containsKey(player.getName().getString()))
                popList.remove(player.getName().getString(), popList.get(player.getName().getString()));
        }
    }

    public int getPops(@NotNull Player entity) {
        if (popList.get(entity.getName().getString()) == null) return 0;
        return popList.get(entity.getName().getString());
    }
}