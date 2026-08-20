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

public class CmdSpam extends Command {

    public CmdSpam() {
        super("spam", "Spams the chat with a certain message.", "[times] [message]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length < 2)
            throw new InvalidSyntaxException(this);

        // Combines the "parameters" into a string to be printed.
        StringBuilder message = new StringBuilder();
        for (int msg = 1; msg < parameters.length; msg++) {
            message.append(parameters[msg]).append(" ");
        }

        // Prints out that message X number of times.
        for (int i = 0; i < Integer.parseInt(parameters[0]); i++) {
            mc.player.connection.sendChat(message.toString());
        }

    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            default:
                return new String[]{"Umbra is an amazing client!"};
        }
    }
}
