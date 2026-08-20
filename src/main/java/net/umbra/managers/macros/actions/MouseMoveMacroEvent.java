/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.managers.macros.actions;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.umbra.mixin.interfaces.IMouseHandler;
import net.minecraft.client.MouseHandler;

public class MouseMoveMacroEvent extends MacroEvent {
	private double x = 0;
	private double y = 0;
	
	public MouseMoveMacroEvent() {
		
	}
	
	public MouseMoveMacroEvent(long timestamp, double x, double y) {
		super(timestamp);
		this.x = x;
		this.y = y;
	}

	@Override
	public void write(DataOutputStream fs) throws IOException {
		super.write(fs);
		fs.writeDouble(x);
		fs.writeDouble(y);
	}

	@Override
	public void read(DataInputStream in)throws IOException {
		super.read(in);
		x = in.readDouble();
		y = in.readDouble();
	}

	@Override
	public void execute() {
		MouseHandler mouse = MC.mouseHandler;
		IMouseHandler iMouse = (IMouseHandler)mouse;
		if(iMouse != null) {
			iMouse.executeOnCursorPos(MC.getWindow().handle(), x, y);
		}
	}
}
