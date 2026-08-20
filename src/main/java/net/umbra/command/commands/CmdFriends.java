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
import net.umbra.settings.friends.Friend;
import net.umbra.settings.friends.FriendsList;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

public class CmdFriends extends Command {

    public CmdFriends() {
        super("friends", "Allows the player to add and remove friends (Who will be excluded from many hacks)", "[add/remove/list] [value]");
    }

    @Override
    public void runCommand(String[] parameters) throws InvalidSyntaxException {
        FriendsList friendsList = Umbra.getInstance().friendsList;
        Minecraft MC = Minecraft.getInstance();
        switch (parameters[0]) {
            case "add": {
                String playerName = parameters[1];
                try {
                    ServerPlayer entity = MC.getSingleplayerServer().getPlayerList().getPlayerByName(playerName);
                    if (entity != null) {
                        Umbra.getInstance().friendsList.addFriend(entity.getName().getString(), entity.getUUID());
                        CommandManager.sendChatMessage("Player " + playerName + " was added to the friends list.");
                    } else {
                        CommandManager.sendChatMessage("Player " + playerName + " could not be found.");
                    }
                } catch (Exception e) {
                    CommandManager.sendChatMessage("Player " + playerName + " could not be added. Unknown error occured.");
                    return;
                }
            }
            break;
            case "remove": {
                String playerName = parameters[1];
                ServerPlayer entity = MC.getSingleplayerServer().getPlayerList().getPlayerByName(playerName);
                if (entity != null) {
                    Umbra.getInstance().friendsList.removeFriend(entity.getUUID());
                    CommandManager.sendChatMessage("Player " + playerName + " was removed from the friends list.");
                } else {
                    CommandManager.sendChatMessage("Player " + playerName + " could not be found.");
                }
            }
            break;
            case "list":
                StringBuilder friends = new StringBuilder("Friends: ");
                for (Friend friend : friendsList.getFriends()) {
                    friends.append(friend.getUsername()).append(", ");
                }
                friends.substring(0, friends.length() - 2);
                CommandManager.sendChatMessage(friends.toString());
                break;
        }
    }

    @Override
    public String[] getAutocorrect(String previousParameter) {
        switch (previousParameter) {
            case "add":
                return mc.getSingleplayerServer().getPlayerNames();
            case "remove":
                return new String[]{"xray", "delete"};
            default:
                return new String[]{"add", "remove", "list"};
        }
    }
}
