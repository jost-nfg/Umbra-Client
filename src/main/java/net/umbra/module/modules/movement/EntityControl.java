/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.movement;

import net.umbra.Umbra;
import net.umbra.event.events.TickEvent.Post;
import net.umbra.event.events.TickEvent.Pre;
import net.umbra.event.listeners.TickListener;
import net.umbra.interfaces.IAbstractHorse;
import net.umbra.module.Category;
import net.umbra.module.Module;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;

public class EntityControl extends Module implements TickListener
{
    public EntityControl()
    {
        super("EntityControl");
        setDescription("Allows you to control entities without needing a saddle.");
        setCategory(Category.of("Movement"));
    }

    @Override
    public void onDisable()
    {
        Umbra.getInstance().eventManager.RemoveListener(TickListener.class, this);

        if (MC.level != null)
        {
            for (Entity entity : Umbra.getInstance().entityManager.getEntities())
            {
                if (entity instanceof AbstractHorse)
                    ((IAbstractHorse) entity).setSaddled(false);
            }
        }
    }

    @Override
    public void onEnable()
    {
        Umbra.getInstance().eventManager.AddListener(TickListener.class, this);
    }

    @Override
    public void onToggle()
    {

    }

    @Override
    public void onTick(Pre event)
    {

    }

    @Override
    public void onTick(Post event)
    {
        if (MC.level != null)
        {
            for (Entity entity : Umbra.getInstance().entityManager.getEntities())
            {
                if (entity instanceof AbstractHorse)
                    ((IAbstractHorse) entity).setSaddled(true);
            }
        }
    }
}
