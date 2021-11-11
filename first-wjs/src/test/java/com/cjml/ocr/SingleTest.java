package com.cjml.ocr;

import com.cjml.ocr.constant.CommonConstants;
import com.cjml.ocr.util.FileUtils;
import com.cjml.ocr.util.ResourceUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class SingleTest {

    @Test
    public void oneTest() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(CommonConstants.FILE_DIR_PROPERTIES);
        //遍历取值
        Enumeration<String> enumeration = resourceBundle.getKeys();
        while (enumeration.hasMoreElements()) {
            try {
                String value = resourceBundle.getString(enumeration.nextElement());
                System.out.println(new String(value.getBytes(StandardCharsets.ISO_8859_1), CommonConstants.GBK));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void twoTest() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(CommonConstants.FILE_DIR_PROPERTIES);
        //遍历取值
        String value = resourceBundle.getString("train-dir");
        String value2 = resourceBundle.getString("eval-dir");
        System.out.println(value);
        System.out.println(value2);
    }

    @Test
    public void threeTest() {
        String trainDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_DIR_KEY);
        System.out.println(trainDir);
    }

    @Test
    public void showFileContentTest() {
        String testDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TEST_DIR_KEY);
        System.out.println(testDir);
        File testFile = new File(testDir);
        List<String> trainFileList = Arrays.stream(testFile.list()).collect(Collectors.toList());
        trainFileList.forEach(System.out::println);
    }

    @Test
    public void readFileContentTest() {
        AtomicInteger i = new AtomicInteger();
        String detDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.DET_LABEL_KEY);
        String recDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.REC_LABEL_KEY);
        System.out.println(detDir);
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get("C:\\Users\\Administrator\\Desktop\\old_pic_2\\newLabel.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 随机行顺序进行数据处理
        assert lines != null;
        lines.forEachOrdered(l -> System.out.println(i.getAndIncrement() + " : " + l));
    }

    @Test
    public void showRecInter() {
        // 分别列出两个目录的所有文件名
        List<String> trainFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_DIR_KEY);
        List<String> evalFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DIR_KEY);

        // 求文件名交集
        List<String> interSectionList = FileUtils.gainIntersection(trainFileList, evalFileList);

        // 获取rec目录下的文件
        List<String> recFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.REC_DIR_KEY);

        // 获取rec数据集中的由以上代码得出的交集部分产生的图片
        List<String> recIntersectionList = recFileList.stream().filter(r -> interSectionList.stream()
                .anyMatch(i -> r.contains(i.substring(0, i.lastIndexOf("."))))).collect(Collectors.toList());
        System.out.println(interSectionList);
        System.out.println(interSectionList.size());
        System.out.println(recIntersectionList);
        System.out.println(recIntersectionList.size());
    }

    @Test
    public void writeFileContentTest() {
        String detLabelDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.DET_LABEL_KEY);
        File detLabelFile = new File(detLabelDir);
        String detLabelParentDir = detLabelFile.getParent();
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(detLabelDir));

        } catch (IOException e) {
            e.printStackTrace();
        }

        File newFile = new File(detLabelParentDir + "/newLabel.txt");
        FileWriter fileWriter = null;
        try {
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            fileWriter = new FileWriter(newFile);
            FileWriter finalFileWriter = fileWriter;
            lines.forEach(l -> {
                try {
                    finalFileWriter.append(l + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void cleanLabelTest() {
        String detLabelDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.DET_LABEL_KEY);

        // 分别列出两个目录的所有文件名
        List<String> trainFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_DIR_KEY);
        List<String> evalFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DIR_KEY);

        // 求文件名交集
        List<String> interSectionList = FileUtils.gainIntersection(trainFileList, evalFileList);

        FileUtils.writeNewFileLabel(detLabelDir, interSectionList);
    }
}
