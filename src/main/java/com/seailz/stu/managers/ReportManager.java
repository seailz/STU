package com.seailz.stu.managers;

import com.seailz.stu.saveables.Report;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ReportManager {

    private static final List<Report> reports = new ArrayList<>();

    public static void init() throws IOException {
        File folder = new File("reports");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) return;
        for (File file : listOfFiles) {
            if (!file.isFile()) continue;
            if (!file.getName().endsWith(".json")) continue;

            String content = Files.readString(file.toPath());
            JSONObject obj = new JSONObject(content);
            Report report = Report.fromJson(obj);
            reports.add(report);
        }
    }
    public static List<Report> getReports() {
        return reports;
    }

    public static void addReport(Report report) {
        reports.add(report);
    }

    public static void removeReport(Report report) {
        reports.remove(report);
    }

    public static Report getReportById(String id) {
        for (Report report : reports) {
            if (report.getId().equals(id) && report.isAcc()) {
                return report;
            }
        }
        return null;
    }

    public static List<Report> getReportsByReported(String reported) {
        List<Report> reports = new ArrayList<>();
        for (Report report : getReports()) {
            if (report.getReported().equals(reported) && report.isAcc()) {
                reports.add(report);
            }
        }
        return reports;
    }

    public static Report getLatestReport() {
        // Find the report that is closest to the end of the list & has the _acc field set to true.
        Report latest = null;
        // invert getReports()
        for (int i = getReports().size() - 1; i >= 0; i--) {
            Report report = getReports().get(i);
            if (report.isAcc()) {
                latest = report;
                break;
            }
        }
        return latest;
    }

}
