package com.seailz.stu.commands;

import com.seailz.discordjar.command.CommandOption;
import com.seailz.discordjar.command.CommandOptionType;
import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.utils.Finals;

// Nicks the user, and gives them the Verified Member role
@SlashCommandInfo(
        name = "verify",
        description = "Verifies you as a member of the server.",
        canUseInDms = false
)
public class CommandVerify extends SlashCommandListener {

    public CommandVerify() {
        addOption(new CommandOption(
                "team",
                "Which team do you work for?",
                CommandOptionType.STRING,
                true
        ));
    }

    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {
        String team = event.getOption("team").getAsString();
        try {
            event.getMember().nickname(team + " | " + event.getMember().user().username());
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
        try {
            event.getMember().addRole(event.getGuild().roles().stream().filter(role -> role.id().equals(Finals.VERIFIED_MEMBER_ROLE)).findAny().get());
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
        try {
            event.reply("You have been verified!").setEphemeral(true).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
