package com.seailz.stu.commands;

import com.seailz.discordjar.command.annotation.SlashCommandInfo;
import com.seailz.discordjar.command.listeners.slash.SlashCommandListener;
import com.seailz.discordjar.events.model.interaction.command.SlashCommandInteractionEvent;
import com.seailz.discordjar.model.embed.Embeder;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.managers.ReportManager;
import com.seailz.stu.saveables.Report;
import com.seailz.stu.utils.Finals;
import com.seailz.stu.ws.SessionManager;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;

@SlashCommandInfo(
        name = "approve",
        description = "Approves a report"
)
public class CommandApprove extends SlashCommandListener {
    @Override
    protected void onCommand(SlashCommandInteractionEvent event) {
        try {
            event.defer(false);
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }
        Report report;

        try {
            report = Report.findById(event.getInteraction().channel().id());
        } catch (IOException e) {
            try {
                event.getHandler().followup("An error occurred while trying to find the report.").setEphemeral(true).run();
            } catch (DiscordRequest.UnhandledDiscordAPIErrorException ex) {
                throw new RuntimeException(ex);
            }
            return;
        }

        if (report == null) {
            try {
                event.getHandler().followup("This channel is not a report ticket.").setEphemeral(true).run();
            } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        report.setAccepted(true);
        try {
            report.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ReportManager.addReport(report);
        SessionManager.broadcastToAllSessions(new JSONObject().put("op", 3).put("report", report.toJson()));

        Embeder embeder = Embeder.e();
        embeder.title("Report");
        embeder.description("New report submitted by " + "<@" + report.getReporter() + ">, report approved by " + "<@" + event.getInteraction().member().user().id() + ">.");
        embeder.field("Offender", "<@" + report.getReported() + "> (" + report.getReportedName() + ")", true);
        embeder.field("Service Team", report.getServiceTeam(), true);
        embeder.field("Date", report.getTimestamp(), false);
        embeder.field("Description", report.getDescription(), false);
        embeder.field("Evidence", report.getEvidence(), false);
        embeder.footer("Report ID: " + report.getId(), null);
        embeder.color(Color.decode("#0080ff"));

        try {
            event.getBot().getTextChannelById(Finals.PUBLIC_REPORTS).sendEmbeds(embeder).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }

        Embeder approved = Embeder.e();
        approved.title("Report Approved");
        approved.description("Your report has been approved by " + "<@" + event.getInteraction().member().user().id() + ">");
        approved.color(Color.decode("#0080ff"));
        try {
            event.getHandler().followup("<@" + report.getReporter() + ">").addEmbed(approved).run();
        } catch (DiscordRequest.UnhandledDiscordAPIErrorException e) {
            throw new RuntimeException(e);
        }


    }
}
