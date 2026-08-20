/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.command;

import net.umbra.managers.CommandManager;
import net.minecraft.ChatFormatting;
import java.io.Serial;

public class InvalidSyntaxException extends CommandException {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidSyntaxException(Command cmd) {
        super(cmd);
    }

    @Override
    public void PrintToChat() {
        CommandManager.sendChatMessage("Invalid syntax! Correct usage: " + ChatFormatting.AQUA + ".umbra " + cmd.getName() + " " + cmd.getSyntax() + ChatFormatting.RESET);
    }
}
