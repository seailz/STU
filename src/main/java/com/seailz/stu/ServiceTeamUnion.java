package com.seailz.stu;

import com.seailz.discordjar.DiscordJar;
import com.seailz.discordjar.model.status.Status;
import com.seailz.discordjar.model.status.StatusType;
import com.seailz.discordjar.model.status.activity.Activity;
import com.seailz.discordjar.model.status.activity.ActivityType;
import com.seailz.discordjar.utils.rest.DiscordRequest;
import com.seailz.stu.commands.*;
import com.seailz.stu.listeners.ObtainTokenListener;
import com.seailz.stu.listeners.ReportListener;
import com.seailz.stu.listeners.ReportModalListener;
import com.seailz.stu.listeners.SupportTicketListener;
import com.seailz.stu.managers.ReportManager;
import com.seailz.stu.rest.ReportController;
import com.seailz.stu.saveables.Report;
import com.seailz.stu.ws.SessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@ComponentScan(basePackages = "com.seailz.stu")
public class ServiceTeamUnion {

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, DiscordRequest.UnhandledDiscordAPIErrorException {
        SpringApplication.run(ServiceTeamUnion.class, args);
        DiscordJar bot = new DiscordJar(args[0], false);
        bot.registerCommands(new CommandVerify(), new CommandTicketPanel(), new CommandTokenPanel(), new CommandApprove(), new CommandClose());
        bot.registerListeners(new ReportListener(), new ReportModalListener(), new ObtainTokenListener(), new SupportTicketListener());

        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            bot.setStatus(new Status(
                    new Activity("Service Team Union", ActivityType.WATCHING),
                    StatusType.DO_NOT_DISTURB
            ));
        }).start();

        ReportManager.init();
    }

    public static boolean validateToken(String token) {
        File file = new File("utils/auth_tokens.json");
        if (!file.exists()) return false;
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray array;
        try {
            array = new JSONArray(fileContent);
        } catch (Exception e) {
            return false;
        }

        for (int i = 0; i < array.length(); i++) {
            if (array.getString(i).equals(token)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasUserGotToken(String userId) {
        File file = new File("utils/used_tokens.json");
        if (!file.exists()) return false;
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray array;
        try {
            array = new JSONArray(fileContent);
        } catch (Exception e) {
            return false;
        }
        if (array.length() == 0) return false;
        for (int i = 0; i < array.length(); i++) {
            if (array.getString(i).equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public static String markUserAsHavingToken(String userID) {
        File file = new File("utils/used_tokens.json");
        if (!file.exists()) return null;
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray array;
        try {
            array = new JSONArray(fileContent);
        } catch (Exception e) {
            return null;
        }
        array.put(userID);
        try {
            Files.writeString(file.toPath(), array.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return randomToken();
    }

    public static String randomToken() {
        File file = new File("utils/auth_tokens.json");
        if (!file.exists()) return null;
        String fileContent = null;
        try {
            fileContent = Files.readString(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JSONArray array;
        try {
            array = new JSONArray(fileContent);
        } catch (Exception e) {
            return null;
        }

        Random random = new Random();
        return array.getString(random.nextInt(array.length()));
    }

}
