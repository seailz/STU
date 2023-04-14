package com.seailz.stu.listeners;

import com.seailz.discordjar.events.DiscordListener;
import com.seailz.discordjar.events.annotation.EventMethod;
import com.seailz.discordjar.events.model.interaction.button.ButtonInteractionEvent;
import com.seailz.discordjar.model.component.ActionRow;
import com.seailz.discordjar.model.component.text.TextInput;
import com.seailz.discordjar.model.component.text.TextInputStyle;
import com.seailz.discordjar.model.interaction.modal.Modal;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import org.jetbrains.annotations.NotNull;

public class ReportListener extends DiscordListener {

    @Override
    @EventMethod
    public void onButtonClickInteractionEvent(@NotNull ButtonInteractionEvent event) {
        if (!event.getCustomId().equals("report")) return;
        Modal modal = new Modal("Report Details", "report-details");
        modal.addComponent(ActionRow.of(
                new TextInput(
                        "offender", TextInputStyle.SHORT, "Offender ID"
                ).setPlaceholder("947691195658797167").setMaxLength(21).setRequired(true)
        ));

        modal.addComponent(ActionRow.of(
                new TextInput(
                        "description", TextInputStyle.LONG, "Description"
                ).setPlaceholder("The user...").setMaxLength(1024).setRequired(true)
        ));

        modal.addComponent(ActionRow.of(
                new TextInput(
                        "evidence", TextInputStyle.LONG, "Evidence"
                ).setPlaceholder("https://cdn.discordapp.com/attachments/...").setMaxLength(1024).setRequired(true)
        ));

        modal.addComponent(ActionRow.of(
                new TextInput(
                        "timestamp", TextInputStyle.SHORT, "Date"
                ).setPlaceholder("12/12/2022").setMaxLength(1024).setRequired(true)
        ));

        modal.addComponent(ActionRow.of(
                new TextInput(
                        "service-team", TextInputStyle.SHORT, "Service Team The Incident Occurred In"
                ).setPlaceholder("... Services").setMaxLength(40).setRequired(true)
        ));

        try {
            event.replyModal(modal).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
