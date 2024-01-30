package kr.nadeuli.common;

import java.time.Duration;
import java.time.LocalDateTime;

public class CalculateTimeAgo {

    public static String calculateTimeDifferenceString(LocalDateTime pastTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(pastTime, currentTime);
        long minutes = duration.toMinutes();

        if (minutes < 10) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (minutes < 24 * 60) {
            long hours = minutes / 60;
            return hours + "시간 전";
        } else if (minutes < 30 * 24 * 60) {
            long days = minutes / (24 * 60);
            return days + "일 전";
        } else if (minutes < 12 * 30 * 24 * 60) {
            long months = minutes / (30 * 24 * 60);
            return months + "달 전";
        } else {
            long years = minutes / (12 * 30 * 24 * 60);
            return years + "년 전";
        }
    }

}