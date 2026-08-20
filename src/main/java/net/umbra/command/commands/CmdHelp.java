/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.command.commands;

import net.umbra.Umbra;
import net.umbra.command.Command;
import net.umbra.managers.CommandManager;
import net.umbra.module.Module;
import net.minecraft.ChatFormatting;
import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class CmdHelp extends Command {

    int indexesPerPage = 5;

    public CmdHelp() {
        super("help", "Shows the avaiable commands.", "[page, module]");
    }

    @Override
    public void runCommand(String[] parameters) {
        if (parameters.length <= 0) {
            ShowCommands(1);
        } else if (StringUtils.isNumeric(parameters[0])) {
            int page = Integer.parseInt(parameters[0]);
            ShowCommands(page);
        } else {
            Module module = Umbra.getInstance().moduleManager.getModuleByName(parameters[0]);
            if (module == null) {
                CommandManager.sendChatMessage("Could not find Module '" + parameters[0] + "'.");
            } else {
                String title = "------------ " + ChatFormatting.AQUA + module.getName() + " Help" + ChatFormatting.RESET + " ------------";
                String unformatted_title = "------------ " + module.getName() + " Help ------------";
                CommandManager.sendChatMessage(title);
                CommandManager.sendChatMessage("Name: " + ChatFormatting.AQUA + module.getName() + ChatFormatting.RESET);
                CommandManager.sendChatMessage("Description: " + ChatFormatting.AQUA + module.getDescription() + ChatFormatting.RESET);
                CommandManager.sendChatMessage("Keybind: " + ChatFormatting.AQUA + module.getBind().getValue().getName() + ChatFormatting.RESET);
                CommandManager.sendChatMessage("-".repeat(unformatted_title.length() - 2)); // mc font characters are not the same width but eh..
            }
        }

    }

    private void ShowCommands(int page) {
        String title = "------------ Help [Page " + page + " of 5] ------------";  // TODO: remove hardcoded page length
        CommandManager.sendChatMessage(title);
        CommandManager.sendChatMessage("Use " + ChatFormatting.AQUA + ".umbra help [n]" + ChatFormatting.RESET + " to get page n of help.");

        // Fetch the commands and dislays their syntax on the screen.
        Map<String, Command> commands = Umbra.getInstance().commandManager.getCommands();
        Set<String> keySet = commands.keySet();
        ArrayList<String> listOfCommands = new ArrayList<String>(keySet);

        for (int i = (page - 1) * indexesPerPage; i <= (page * indexesPerPage); i++) {
            if (i >= 0 && i < Umbra.getInstance().commandManager.getNumOfCommands()) {
                CommandManager.sendChatMessage(" .umbra " + listOfCommands.get(i));
            }
        }
        CommandManager.sendChatMessage("-".repeat(title.length() - 2)); // mc font characters are not the same width but eh..
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        CommandManager cm = Umbra.getInstance().commandManager;
        int numCmds = cm.getNumOfCommands();
        String[] commands = new String[numCmds];

        Set<String> cmds = Umbra.getInstance().commandManager.getCommands().keySet();
        int i = 0;
        for (String x : cmds)
            commands[i++] = x;

        return commands;
    }

}
