package com.seailz.stu.commands;

import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.model.component.ActionRow;
import com.seailz.discordjar.model.component.button.Button;
import com.seailz.discordjar.model.embed.Embeder;
import com.seailz.discordjar.utils.rest.DiscordRequest;

import java.awt.*;

@SlashCommandInfo(
        name = "tokenpanel",
        description = "Creates a token panel."
)
public class CommandTokenPanel extends SlashCommandListener {
    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {
        Embeder embeder = Embeder.e();
        embeder.title("API Details");
        embeder.description("STU offers an API that can be integrated into your own applications. The documentation for the API can be found [here](https://seailz.notion.site/STU-API-documentation-2f06a5f5fb834d969cb5b891809e95a5). To utilize the API, a token is required, which can be obtained by clicking the button below.");
        embeder.color(Color.decode("#0080ff"));

        Button tokenButton = Button.primary("Obtain Token", "token");
        event.getInteraction().channel().asGuildChannel().asMessagingChannel().sendEmbeds(embeder).addComponents(ActionRow.of(tokenButton)).run();

        try {
            event.reply("Token panel created!").setEphemeral(true).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
