/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.command.commands;

import net.umbra.Umbra;
import net.umbra.UmbraClient;
import net.umbra.command.Command;
import net.umbra.gui.GuiManager;
import net.umbra.gui.font.UIFont;
import net.umbra.managers.CommandManager;
import net.umbra.command.InvalidSyntaxException;

public class CmdFont extends Command {

    public CmdFont() {
        super("font", "Sets the HUD font.", "[set] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        if (parameters.length != 2)
            throw new InvalidSyntaxException(this);

        UmbraClient umbra = Umbra.getInstance();

        switch (parameters[0]) {
            case "set":
                try {
                    String font = parameters[1];
                    UIFont t = umbra.fontManager.fonts.get(font);
                    if (t != null) {
                    	GuiManager.fontSetting.setFontName(font);
                    }
                } catch (Exception e) {
                    CommandManager.sendChatMessage("Invalid value.");
                }
                break;
            default:
                throw new InvalidSyntaxException(this);
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            case "set":
                UmbraClient umbra = Umbra.getInstance();

                String[] suggestions = new String[umbra.fontManager.fonts.size()];

                int i = 0;
                for (String fontName : umbra.fontManager.fonts.keySet())
                    suggestions[i++] = fontName;

                return suggestions;
            default:
                return new String[]{"set"};
        }
    }
}