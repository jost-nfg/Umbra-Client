/*
 * Umbra Client
 * Copyright (C) 2026 jost-nfg
 *
 * Licensed under the GNU General Public License, Version 3 or later.
 * See <http://www.gnu.org/licenses/>.
 */

package net.umbra.module.modules.misc;

import net.umbra.module.Category;
import net.umbra.module.Module;
import net.umbra.utils.discord.RPCManager;
import com.mojang.logging.LogUtils;

public class DiscordRPCModule extends Module {

    public DiscordRPCModule() {
        super("DiscordRPC");

        setCategory(Category.of("Misc"));
        setDescription("Toggles Discord RPC On and Off");
    }

    @Override
    public void onDisable() {
        RPCManager rpcManager = new RPCManager();
        rpcManager.stopRpc();
        // Turns the Discord RPC Off
    }

    @Override
    public void onEnable() {
        // Lunar Client provides its own Discord Rich Presence; running both conflicts.
        if (net.umbra.utils.RuntimeEnvironment.isLunar()) {
            LogUtils.getLogger().info("[Umbra] DiscordRPC module skipped: Lunar Client provides its own rich presence");
            return;
        }
        RPCManager rpcManager = new RPCManager();
        rpcManager.startRpc();
        // Turns the Discord RPC On
    }

    @Override
    public void onToggle() { // OnEnable And Disable Handle
    }
}
