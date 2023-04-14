package com.seailz.stu.saveables;

import com.seailz.discordjar.DiscordJar;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

public class Report {

    private final String id;
    private final String timestamp;
    private final String reported;
    private final String reportedName;
    private final String evidence;
    private final String extraInfo;
    private final String description;
    private final String serviceTeam;
    private final String reporter;
    // Is this report accepted? This is never false to the client, but used internally.
    private boolean _acc;

    public Report(String id, String timestamp, String reported, String reportedName, String evidence, String extraInfo, String description, String serviceTeam, boolean acc, String reporter) {
        this.id = id;
        this.timestamp = timestamp;
        this.reported = reported;
        this.evidence = evidence;
        this.extraInfo = extraInfo;
        this.description = description;
        this.serviceTeam = serviceTeam;
        this.reportedName = reportedName;
        _acc = acc;
        this.reporter = reporter;
    }

    public void setAccepted(boolean _acc) {
        this._acc = _acc;
    }

    public String getReporter() {
        return reporter;
    }

    public String getReportedName() {
        return reportedName;
    }

    public String getDescription() {
        return description;
    }

    public String getEvidence() {
        return evidence;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public String getId() {
        return id;
    }

    public String getReported() {
        return reported;
    }

    public String getServiceTeam() {
        return serviceTeam;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isAcc() {
        return _acc;
    }

    public static Report fromJson(JSONObject obj) {
        String id = obj.has("id") ? obj.getString("id") : null;
        String timestamp = obj.has("timestamp") ? obj.getString("timestamp") : null;
        String reported = obj.has("reported") ? obj.getString("reported") : null;
        String evidence = obj.has("evidence") ? obj.getString("evidence") : null;
        String extraInfo = obj.has("extra_info") ? obj.getString("extra_info") : null;
        String description = obj.has("description") ? obj.getString("description") : null;
        String serviceTeam = obj.has("service_team") ? obj.getString("service_team") : null;
        String reportedName = obj.has("reported_name") ? obj.getString("reported_name") : null;
        boolean acc = obj.has("_acc") && obj.getBoolean("_acc");
        String reporter = obj.has("_reporter") ? obj.getString("_reporter") : null;
        return new Report(id, timestamp, reported, reportedName, evidence, extraInfo, description, serviceTeam, acc, reporter);
    }

    public JSONObject toJson() {
        return new JSONObject()
                .put("id", id)
                .put("timestamp", timestamp)
                .put("reported", reported)
                .put("evidence", evidence)
                .put("extra_info", extraInfo)
                .put("description", description)
                .put("service_team", serviceTeam)
                .put("reported_name", reportedName)
                .put("_acc", _acc)
                .put("_reporter", reporter);
    }

    public File save() throws IOException {
        File file = new File("reports/" + this.id + ".json");
        System.out.println("saving" + this.toJson().toString());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        }

        PrintWriter writer = new PrintWriter(file);
        writer.print(this.toJson().toString());
        writer.close();
        return file;
    }

    public static Report findById(String id) throws IOException {
        File report = new File("reports/" + id + ".json");
        System.out.println("finding report " + id);
        System.out.println(report.exists());
        if (!report.exists()) return null;
        return fromJson(new JSONObject(Files.readString(report.toPath())));
    }
}
