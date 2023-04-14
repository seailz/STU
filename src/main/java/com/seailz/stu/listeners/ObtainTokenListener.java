package com.seailz.stu.listeners;

import com.seailz.discordjar.events.DiscordListener;
import com.seailz.discordjar.events.annotation.EventMethod;
import com.seailz.discordjar.events.model.interaction.button.ButtonInteractionEvent;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.ServiceTeamUnion;
import org.jetbrains.annotations.NotNull;

public class ObtainTokenListener extends DiscordListener {

    @Override
    @EventMethod
    public void onButtonClickInteractionEvent(@NotNull ButtonInteractionEvent event) {
        if (!event.getCustomId().equals("token")) return;
        if (ServiceTeamUnion.hasUserGotToken(event.getMember().user().id())) {
            try {
                event.reply("You already have a token!").setEphemeral(true).run();
            } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        ServiceTeamUnion.markUserAsHavingToken(event.getMember().user().id());
        String token = ServiceTeamUnion.randomToken();
        if (token == null) {
            try {
                event.reply("There was an error generating your token. Please try again later.").setEphemeral(true).run();
            } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        try {
            event.reply("Your API token is: `" + token + "`").setEphemeral(true).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        try {
            event.getMember().user().createDM().sendMessage("Your API token is: `" + token + "`").run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            return;
        }
    }
}
