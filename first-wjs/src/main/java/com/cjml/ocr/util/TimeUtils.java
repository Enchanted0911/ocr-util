package com.cjml.ocr.util;

import com.cjml.ocr.constant.CommonConstants;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class TimeUtils {

    public static String subTime() {
        String startTime = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.START_TIME);
        String endTime = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.END_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(startTime, dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse(endTime, dateTimeFormatter);
        Duration duration = Duration.between(start, end);
        int hour = (int) duration.toHours();
        int min = duration.toMinutesPart();
        int second = duration.toSecondsPart();
        return hour + " h " + min + " m " + second + " s";
    }
}
