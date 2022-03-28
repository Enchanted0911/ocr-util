package com.cjml.ocr;

import com.cjml.detection.Generator;
import com.cjml.util.FileUtils;
import org.junit.Test;

import java.util.List;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class GeneratorTest {
    @Test
    public void oneTest() {
        FileUtils.removeFiles(Generator.findNoLabel("D:\\wjs\\detection_data_set\\alpha\\images\\3000"
                , "D:\\wjs\\detection_data_set\\alpha\\annotations\\3000")
                , "D:\\wjs\\detection_data_set\\alpha\\images\\3000");
    }

    @Test
    public void twoTest() {
        String path = "D:\\wjs\\detection_data_set\\alpha\\annotations\\4315";
        String one = "<difficult>";
        String two = "<wujunshengwujunsheng>";
        String labelName = "0";

        FileUtils.processXmlFile(path, one, two, labelName);

    }

    @Test
    public void findMultiTest() {
        String fileDir = "D:\\wjs\\detection_data_set\\alpha\\annotations";
        List<String> resList = Generator.findMultiObjectLabel(fileDir);
        resList.forEach(System.out::println);
    }
}
