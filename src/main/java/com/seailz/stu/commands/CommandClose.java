package com.seailz.stu.commands;

import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.model.message.Attachment;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.utils.Finals;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@SlashCommandInfo(
        name = "close",
        description = "Closes the ticket and generates a transcript."
)
public class CommandClose extends SlashCommandListener {
    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {
        InputStream st;
        try {
            st = event.getInteraction().channel().asGuildChannel().asMessagingChannel().transcript();
        } catch (IOException | DiscordRequest.UnhandledDiscordAPIErrorException e) {
            try {
                event.reply("An error occurred while trying to generate the transcript.").setEphemeral(true).run();
            } catch (DiscordRequest.UnhandledDiscordAPIErrorException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }

        try {
            event.reply("Closing ticket...").setEphemeral(true).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        File transcript = new File("transcript.html");
        try {
            FileUtils.copyInputStreamToFile(st, transcript);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            event.getBot().getTextChannelById(Finals.TRANSCRIPTS).sendAttachments(
                    Attachment.fromFile(0, transcript)
            ).addFile(transcript).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        try {
            event.getInteraction().channel().delete();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
