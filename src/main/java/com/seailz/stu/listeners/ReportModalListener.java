package com.seailz.stu.listeners;

import com.seailz.discordjar.action.guild.channel.CreateGuildChannelAction;
import com.seailz.discordjar.events.DiscordListener;
import com.seailz.discordjar.events.annotation.EventMethod;
import com.seailz.discordjar.events.model.interaction.modal.ModalInteractionEvent;
import com.seailz.discordjar.model.channel.MessagingChannel;
import com.seailz.discordjar.model.channel.utils.ChannelType;
import com.seailz.discordjar.model.embed.Embeder;
import com.seailz.discordjar.model.permission.OverwriteType;
import com.seailz.discordjar.model.permission.PermissionOverwrite;
import com.seailz.discordjar.utils.permission.Permission;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.saveables.Report;
import com.seailz.stu.utils.Finals;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class ReportModalListener extends DiscordListener {

    @Override
    @EventMethod
    public void onModalInteractionEvent(@NotNull ModalInteractionEvent event) {
        if (!event.getCustomId().equals("report-details")) return;
        try {
            event.defer(true);
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        Random ran = new Random();
        int num = ran.nextInt(9999 - 1000) + 1000;
        MessagingChannel channel;
        try {
            CreateGuildChannelAction creator = event.getGuild().createChannel("report-" + num, ChannelType.GUILD_TEXT);
            creator.setCategory(event.getBot().getCategoryById(Finals.REPORT_CATEGORY));
            channel = creator.run().get().asMessagingChannel();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        try {
            channel.editChannelPermissions(new PermissionOverwrite(
                    event.getGuild().getEveryoneRole().id(),
                    OverwriteType.ROLE
            ).deny(Permission.VIEW_CHANNEL));

            channel.editChannelPermissions(new PermissionOverwrite(
                    event.getInteraction().member().user().id(),
                    OverwriteType.MEMBER
            ).allow(Permission.VIEW_CHANNEL));
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        UUID uuid = UUID.randomUUID();


        Embeder embeder = Embeder.e();
        embeder.title("Report");
        embeder.description("Thanks for submitting a user report. Our team will review this and reach a verdict if this report will be published.");
        embeder.field("Offender", "<@" + event.getValue("offender").value() + "> (" + event.getValue("offender").value() + ")", true);
        embeder.field("Service Team", event.getValue("service-team").value(), true);
        embeder.field("Date", event.getValue("timestamp").value(), true);
        embeder.field("Description", event.getValue("description").value(), false);
        embeder.field("Evidence", event.getValue("evidence").value(), false);
        embeder.footer("Report ID: " + uuid, null);
        embeder.color(Color.decode("#0080ff"));

        channel.sendEmbeds(embeder).setText(event.getMember().user().getAsMention() + " <@&" + Finals.MODERATOR_ROLE + ">").run();
        try {
            event.getHandler().followup("Report submitted!").setEphemeral(true).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        try {
            new Report(
                    channel.id(),
                    event.getValue("timestamp").value(),
                    event.getValue("offender").value(),
                    event.getBot().getUserById(event.getValue("offender").value()).username(),
                    event.getValue("evidence").value(),
                    "",
                    event.getValue("description").value(),
                    event.getValue("service-team").value(),
                    false,
                    event.getMember().user().id()
            ).save();
        } catch (Exception e) {
            try {
                new Report(
                        channel.id(),
                        event.getValue("timestamp").value(),
                        event.getValue("offender").value(),
                        "",
                        event.getValue("evidence").value(),
                        "",
                        event.getValue("description").value(),
                        event.getValue("service-team").value(),
                        false,
                        event.getMember().user().id()
                ).save();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
