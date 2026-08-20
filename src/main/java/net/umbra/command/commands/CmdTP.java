/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.command.commands;

import net.umbra.command.Command;
import net.umbra.command.InvalidSyntaxException;

public class CmdTP extends Command {

    public CmdTP() {
        super("tp", "Teleports the player certain blocks away (Vanilla only)", "[x] [y] [z]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length != 3)
            throw new InvalidSyntaxException(this);

        mc.player.setPos(Double.parseDouble(parameters[0]), Double.parseDouble(parameters[1]), Double.parseDouble(parameters[2]));
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        return new String[]{"0 0 0"};
    }
}
