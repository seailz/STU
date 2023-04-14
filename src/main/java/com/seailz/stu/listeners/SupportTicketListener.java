package com.seailz.stu.listeners;

import com.seailz.discordjar.action.guild.channel.CreateGuildChannelAction;
import com.seailz.discordjar.events.DiscordListener;
import com.seailz.discordjar.events.annotation.EventMethod;
import com.seailz.discordjar.events.model.interaction.button.ButtonInteractionEvent;
import com.seailz.discordjar.model.channel.MessagingChannel;
import com.seailz.discordjar.model.channel.utils.ChannelType;
import com.seailz.discordjar.model.embed.Embeder;
import com.seailz.discordjar.model.permission.OverwriteType;
import com.seailz.discordjar.model.permission.PermissionOverwrite;
import com.seailz.discordjar.utils.permission.Permission;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.utils.Finals;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class SupportTicketListener extends DiscordListener {

    @Override
    @EventMethod
    public void onButtonClickInteractionEvent(@NotNull ButtonInteractionEvent event) {
        if (!event.getCustomId().equals("support")) return;
        try {
            event.defer(true);
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        Random ran = new Random();
        int num = ran.nextInt(9999 - 1000) + 1000;
        MessagingChannel channel;
        try {
            CreateGuildChannelAction creator = event.getGuild().createChannel("support-" + num, ChannelType.GUILD_TEXT);
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

        Embeder emb = Embeder.e();
        emb.title("Support Ticket");
        emb.description("Thanks for creating a support ticket. Please let us know what we can do to help and one of our team will be with you as soon as possible!");
        emb.color(Color.decode("#0080ff"));

        channel.sendEmbeds(emb).setText("<@" + event.getMember().user().id() + ">").run();
        try {
            event.getHandler().followup("Ticket created!").setEphemeral(true).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

    }
}
