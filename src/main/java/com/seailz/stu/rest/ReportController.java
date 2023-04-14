package com.seailz.stu.rest;

import com.google.common.util.concurrent.RateLimiter;
import com.seailz.stu.ServiceTeamUnion;
import com.seailz.stu.managers.ReportManager;
import com.seailz.stu.saveables.Report;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReportController {

    private final Map<String, RateLimiter> rateLimiterMap = new HashMap<>();
    private final double requestsPerSecond = 2.0;

    private RateLimiter getRateLimiterForIp(String ip) {
        RateLimiter rateLimiter = rateLimiterMap.get(ip);
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(requestsPerSecond);
            rateLimiterMap.put(ip, rateLimiter);
        }
        return rateLimiter;
    }

    @GetMapping("/report/{id}")
    public ResponseEntity<String> getReportById(@PathVariable("id") String id, @RequestHeader("Authorization") String authHeader, @RequestHeader(name = "X-Forwarded-For", required=false) String xForwardedFor, HttpServletRequest request) {
        if (!ServiceTeamUnion.validateToken(authHeader))
            return ResponseEntity.status(401).build();

        String ip = xForwardedFor != null ? xForwardedFor : request.getRemoteAddr();
        RateLimiter rateLimiter = getRateLimiterForIp(ip);

        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        Report report = ReportManager.getReportById(id);
        if (report == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(report.toJson().toString());
    }

    @GetMapping("/report")
    public ResponseEntity<String> getLatestReport(@RequestHeader("Authorization") String authHeader, @RequestHeader(name = "X-Forwarded-For", required=false) String xForwardedFor, HttpServletRequest request) {
        if (!ServiceTeamUnion.validateToken(authHeader))
            return ResponseEntity.status(401).build();

        String ip = xForwardedFor != null ? xForwardedFor : request.getRemoteAddr();
        RateLimiter rateLimiter = getRateLimiterForIp(ip);

        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        if (ReportManager.getReports().isEmpty())
            return ResponseEntity.notFound().build();

        Report report = ReportManager.getLatestReport();
        if (report == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(report.toJson().toString());
    }

    @GetMapping("/user/{id}/reports")
    public ResponseEntity<String> getReportsByUserId(@PathVariable("id") String id, @RequestHeader("Authorization") String authHeader, @RequestHeader(name = "X-Forwarded-For", required=false) String xForwardedFor, HttpServletRequest request) {
        if (!ServiceTeamUnion.validateToken(authHeader))
            return ResponseEntity.status(401).build();

        String ip = xForwardedFor != null ? xForwardedFor : request.getRemoteAddr();
        RateLimiter rateLimiter = getRateLimiterForIp(ip);

        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(429).build(); // Too Many Requests
        }

        List<Report> reports = ReportManager.getReportsByReported(id);

        JSONArray array = new JSONArray();
        for (Report report : reports) {
            array.put(report.toJson());
        }
        return ResponseEntity.ok(array.toString());
    }
}