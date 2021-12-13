package com.cjml.ocr;

import com.cjml.clas.constant.CommonConstants;
import com.cjml.util.FileUtils;
import com.cjml.util.ResourceUtils;
import org.junit.Test;

/**
 * @author johnson
 * @date 2021-12-12
 */
public class ClasTest {

    public static String trainClassDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_CLASS_DIR);
    public static String evalClassDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_CLASS_DIR);
    public static String trainFilterChineseLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.FILTER_TRAIN_CHINESE_PATH);
    public static String evalFilterChineseLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.FILTER_EVAL_CHINESE_PATH);

    @Test
    public void separateFile() {
        FileUtils.separateFile(trainClassDir, evalClassDir);
    }

    @Test
    public void filterChinese() {
        FileUtils.filterChinese(trainFilterChineseLabelPath);
        FileUtils.filterChinese(evalFilterChineseLabelPath);
    }
}
