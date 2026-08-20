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
import net.umbra.command.InvalidSyntaxException;
import net.umbra.module.modules.movement.Noclip;

public class CmdNoclip extends Command {

	public CmdNoclip() {
		super("noclip", "Allows the player to phase through blocks.", "[toggle/speed] [value]");
	}

	@Override
	public void runCommand(String[] parameters) throws InvalidSyntaxException {
		if (parameters.length != 2)
			throw new InvalidSyntaxException(this);

		Noclip module = Umbra.getInstance().moduleManager.noclip;

		switch (parameters[0]) {
		case "speed":
			try {
				float speed = Float.parseFloat(parameters[1]);
				module.setSpeed(speed);
				CommandManager.sendChatMessage("Flight speed set to " + speed);

			} catch (Exception e) {
				CommandManager.sendChatMessage("Invalid value.");
			}
			break;
		case "toggle":
			String state = parameters[1].toLowerCase();
			if (state.equals("on")) {
				module.state.setValue(true);
				CommandManager.sendChatMessage("Noclip toggled ON");
			} else if (state.equals("off")) {
				module.state.setValue(false);
				CommandManager.sendChatMessage("Noclip toggled OFF");
			} else {
				CommandManager.sendChatMessage("Invalid value. [ON/OFF]");
			}
			break;
		default:
			throw new InvalidSyntaxException(this);
		}

	}

	@Override
	public String[] getAutocorrect(String previousParameter) {
		switch (previousParameter) {
		case "toggle":
			return new String[] { "on", "off" };
		case "speed":
			return new String[] { "0.0", "1.0", "5.0", "10.0" };
		default:
			return new String[] { "toggle" };
		}
	}

}
