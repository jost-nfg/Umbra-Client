/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.combat;

import net.umbra.module.Category;
import net.umbra.module.Module;

public class AntiInvis extends Module {

    public AntiInvis() {
    	super("AntiInvis");
        setCategory(Category.of("Combat"));
        setDescription("Reveals players who are invisible.");
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onToggle() {

    }
}