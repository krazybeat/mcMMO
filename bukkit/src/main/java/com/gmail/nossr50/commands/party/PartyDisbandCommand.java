package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.party.Party;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.party.PartyManager;
import com.gmail.nossr50.core.events.party.McMMOPartyChangeEvent.EventReason;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PartyDisbandCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 1:
                Party playerParty = UserManager.getPlayer((Player) sender).getParty();
                String partyName = playerParty.getName();

                for (Player member : playerParty.getOnlineMembers()) {
                    if (!PartyManager.handlePartyChangeEvent(member, partyName, null, EventReason.KICKED_FROM_PARTY)) {
                        return true;
                    }

                    member.sendMessage(LocaleLoader.getString("Party.Disband"));
                }

                PartyManager.disbandParty(playerParty);
                return true;

            default:
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.1", "party", "disband"));
                return true;
        }
    }
}
