package com.gmail.nossr50.core.chat;

import com.gmail.nossr50.core.McmmoCore;
import com.gmail.nossr50.core.data.UserManager;
import com.gmail.nossr50.core.datatypes.party.Party;
import com.gmail.nossr50.core.events.chat.McMMOChatEvent;
import com.gmail.nossr50.core.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.core.locale.LocaleLoader;
import com.gmail.nossr50.core.mcmmo.entity.Player;

public abstract class ChatManager {
    protected boolean useDisplayNames;
    protected String chatPrefix;

    protected String senderName;
    protected String displayName;
    protected String message;

    protected ChatManager(boolean useDisplayNames, String chatPrefix) {
        this.useDisplayNames = useDisplayNames;
        this.chatPrefix = chatPrefix;
    }

    protected void handleChat(McMMOChatEvent event) {
        McmmoCore.getEventCommander().callEvent(event);

        if (event.isCancelled()) {
            return;
        }

        senderName = event.getSender();
        displayName = useDisplayNames ? event.getDisplayName() : senderName;
        message = LocaleLoader.formatString(chatPrefix, displayName) + " " + event.getMessage();

        sendMessage();

        /*
         * Party Chat Spying
         * Party messages will be copied to people with the mcmmo.admin.chatspy permission node
         */
        if (event instanceof McMMOPartyChatEvent) {
            //We need to grab the party chat name
            McMMOPartyChatEvent partyChatEvent = (McMMOPartyChatEvent) event;

            //Find the people with permissions
            for (Player player : event.getPlugin().getServer().getOnlinePlayers()) {
                //Check for toggled players
                if (UserManager.getPlayer(player).isPartyChatSpying()) {
                    Party adminParty = UserManager.getPlayer(player).getParty();

                    //Only message admins not part of this party
                    if (adminParty != null) {
                        //TODO: Incorporate JSON
                        if (!adminParty.getName().equalsIgnoreCase(partyChatEvent.getParty()))
                            player.sendMessage(LocaleLoader.getString("Commands.AdminChatSpy.Chat", partyChatEvent.getParty(), message));
                    } else {
                        player.sendMessage(LocaleLoader.getString("Commands.AdminChatSpy.Chat", partyChatEvent.getParty(), message));
                    }
                }
            }
        }
    }

    public void handleChat(String senderName, String message) {
        handleChat(senderName, senderName, message, false);
    }

    public void handleChat(Player player, String message, boolean isAsync) {
        handleChat(player.getName(), player.getDisplayName(), message, isAsync);
    }

    public void handleChat(String senderName, String displayName, String message) {
        handleChat(senderName, displayName, message, false);
    }

    public abstract void handleChat(String senderName, String displayName, String message, boolean isAsync);

    protected abstract void sendMessage();
}
