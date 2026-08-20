/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.command.commands;

import net.umbra.command.Command;

import java.io.IOException;

public class CmdDiscord extends Command
{
    private static final String DISCORD_URL = "https://discord.gg/jEu6WfnyR6";

    public CmdDiscord()
    {
        super("discord", "Sends the Umbra Client Discord", "");
    }

    @Override
    public void runCommand(String[] parameters)
    {
        try
        {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win"))
            {
                Runtime.getRuntime().exec("cmd /c start " + DISCORD_URL);
            }
            else if (os.contains("mac"))
            {
                Runtime.getRuntime().exec("open " + DISCORD_URL);
            }
            else if (os.contains("nix") || os.contains("nux"))
            {
                Runtime.getRuntime().exec("xdg-open " + DISCORD_URL);
            }
            else
            {
                System.out.println("Unsupported OS. Please open the Discord link manually: " + DISCORD_URL);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter)
    {
        return new String[0];
    }
}
