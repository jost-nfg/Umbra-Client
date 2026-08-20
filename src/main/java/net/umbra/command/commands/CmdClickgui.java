/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.command.commands;

import com.mojang.blaze3d.platform.InputConstants;
import net.umbra.Umbra;
import net.umbra.command.Command;
import net.umbra.command.InvalidSyntaxException;

public class CmdClickgui extends Command {

    public CmdClickgui() {
        super("clickgui", "Allows the player to see chest locations through ESP", "[set/open] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        switch (parameters[0]) {
            case "set":
                if (parameters.length != 2)
                    throw new InvalidSyntaxException(this);
                char keybind = Character.toUpperCase(parameters[1].charAt(0));
                Umbra.getInstance().guiManager.clickGuiButton.setValue(InputConstants.Type.KEYSYM.getOrCreate((int) keybind));
                break;
            case "open":
                Umbra.getInstance().guiManager.setClickGuiOpen(true);
                break;
            default:
                throw new InvalidSyntaxException(this);
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            default:
                return new String[]{"set", "open"};
        }
    }
}
