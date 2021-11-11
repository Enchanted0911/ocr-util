package com.cjml.ocr;

import com.cjml.ocr.constant.CommonConstants;
import com.cjml.ocr.util.ResourceUtils;
import com.cjml.ocr.util.TimeUtils;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeTest {

    @Test
    public void subTime() {
        System.out.println(TimeUtils.subTime());
    }
}
