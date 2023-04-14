package com.seailz.stu.commands;

import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.model.component.ActionRow;
import com.seailz.discordjar.model.component.button.Button;
import com.seailz.discordjar.model.embed.Embeder;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.utils.Finals;

import java.awt.*;

@SlashCommandInfo(
        name = "ticketpanel",
        description = "Creates a ticket panel.",
        canUseInDms = false
)
public class CommandTicketPanel extends SlashCommandListener {
    @Override
    protected void onCommand(SlashCommandInteractionEvent command) {
        Embeder embeder = Embeder.e();
        embeder.title("Ticket Creation");
        embeder.description("Please select a button below to create a ticket");
        embeder.color(Color.decode("#0080ff"));

        Button report = Button.primary("Submit a Report", "report").setEmoji(Finals.REPORT);
        Button support = Button.secondary("Request Support", "support").setEmoji(Finals.SUPPORT);
        command.getInteraction().channel().asGuildChannel().asMessagingChannel().sendEmbeds(embeder).addComponents(ActionRow.of(report, support)).run();
        try {
            command.reply("Ticket panel created!").setEphemeral(true).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
