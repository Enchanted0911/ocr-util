package com.cjml.ocr;

import com.cjml.ocr.constant.CommonConstants;
import com.cjml.util.FileUtils;
import com.cjml.util.ResourceUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class SingleTest {

    String trainDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_DET_DIR_KEY);
    String evalDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DET_DIR_KEY);
    String testDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TEST_DIR_KEY);
    String detLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DET_LABEL_KEY);
    String recDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_REC_DIR_KEY);


    String alignRecDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_EVAL_REC_DIR);
    String alignDetDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_EVAL_DET_DIR);
    String alignDetFile = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_EVAL_DET_FILE);
    String alignRecFile = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_EVAL_REC_FILE);


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
        System.out.println(trainDir);
    }

    @Test
    public void showFileContentTest() {
        System.out.println(testDir);
        File testFile = new File(testDir);
        List<String> trainFileList = Arrays.stream(testFile.list()).collect(Collectors.toList());
        trainFileList.forEach(System.out::println);
    }

    @Test
    public void readFileContentTest() {

        AtomicInteger i = new AtomicInteger();
        System.out.println(detLabelPath);
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
        List<String> trainFileList = FileUtils.gainAllFileName(trainDir);
        List<String> evalFileList = FileUtils.gainAllFileName(evalDir);

        // 求文件名交集
        List<String> interSectionList = FileUtils.gainIntersection(trainFileList, evalFileList);

        // 获取rec目录下的文件
        List<String> recFileList = FileUtils.gainAllFileName(recDir);

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
        File detLabelFile = new File(detLabelPath);
        String detLabelParentDir = detLabelFile.getParent();
        Stream<String> lines = null;
        try {
            lines = Files.lines(Paths.get(detLabelPath));

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
            assert lines != null;
            lines.forEach(l -> {
                try {
                    finalFileWriter.append(l).append("\n");
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
    public void regularDir() {
//        Single.regularizeDirInLabelFile("D:\\BaiduNetdiskDownload\\DataSet\\Chinese_dataset\\labels.txt", "D:/BaiduNetdiskDownload/DataSet/Chinese_dataset/images", false);
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_nameplate\\merge_data\\Label.txt", "det_nameplate_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_nameplate\\merge_data\\rec_gt.txt", "rec_nameplate_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_nameplate\\merge_data\\Label.txt", "det_nameplate_eval");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_nameplate\\merge_data\\rec_gt.txt", "rec_nameplate_eval");
//        Single.regularizeDirInLabelFile("D:\\wjs\\PycharmProjects\\end2end_eval\\Label_hard.txt", "200difficult_1");
        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_vin\\merge_data\\Label.txt", "det_vin_eval");
        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_vin\\merge_data\\rec_gt.txt", "rec_vin_eval");
        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_vin\\merge_data\\Label.txt", "det_vin_train");
        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_vin\\merge_data\\rec_gt.txt", "rec_vin_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train\\rec_gt.txt", "rec_data");
    }

    @Test
    public void regularDir2() {
        Single.regularizeDirInLabelFile("C:\\Users\\wujs.YANGCHE\\Downloads\\new_train.list", "train_images", false);
    }

    @Test
    public void opsDir() {
        Single.changeSpecialChar("D:\\wjs\\ocr_eval\\merge_data\\rec_gt_eval.txt", "\t", " ");
        Single.changeSpecialChar("D:/wjs/ocr_train/merge_data/rec_gt.txt", "\t", " ");
    }


    @Test
    public void fixEngLabel() {
//        Single.fixEngLabel("D:\\BaiduNetdiskDownload\\DataSet\\Chinese_dataset\\engTrainLabel.txt");
//        Single.fixEngLabel("D:\\BaiduNetdiskDownload\\DataSet\\Chinese_dataset\\engEvalLabel.txt");
        Stream<String> lines = FileUtils.gainFileContent("D:\\BaiduNetdiskDownload\\DataSet\\Chinese_dataset\\engEvalLabel.txt");

        assert lines != null;
        Stream<String> newLines = lines.map(l -> l.substring(l.indexOf('/') + 1));

        File oldLabelFile = new File("D:\\BaiduNetdiskDownload\\DataSet\\Chinese_dataset\\engEvalLabel.txt");

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, newLines);
    }

    @Test
    public void fixIndexLabel() {
        Single.fixIndexLabel("D:\\BaiduNetdiskDownload\\DataSet\\data_train.txt", "C:\\Users\\wujs.YANGCHE\\Desktop\\char_std_5990.txt");
    }

    @Test
    public void fix21WLabel() {
//        Single.fixEngLabel("D:\\BaiduNetdiskDownload\\DataSet\\Chinese_dataset\\engTrainLabel.txt");
//        Single.fixEngLabel("D:\\BaiduNetdiskDownload\\DataSet\\Chinese_dataset\\engEvalLabel.txt");
        Stream<String> lines = FileUtils.gainFileContent("C:\\Users\\wujs.YANGCHE\\Downloads\\train.list");

        assert lines != null;
        Stream<String> newLines = lines.map(l -> l.substring(l.indexOf('i')));

        File oldLabelFile = new File("C:\\Users\\wujs.YANGCHE\\Downloads\\train.list");

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, newLines);
    }

    @Test
    public void generateAugLabel() {
        int i = 353;
        String imageDir = "D:\\wjs\\processed_data_set\\aug_generate\\" + i;
        String desLabelPath = "D:\\wjs\\processed_data_set\\augLabel" + i + ".txt";
        String labelDir = "aug_generate/" + i + "/";
        String labelName = "G";
        Single.generateAugLabel(imageDir, desLabelPath, labelDir, labelName);
    }

    @Test
    public void mergeLabelContent() {
        String baseFilename = "D:\\wjs\\processed_data_set\\augLabel";
        String newFilePath = "D:\\wjs\\processed_data_set\\augLabel_01.txt";
        List<String> filenameList = new ArrayList<>();
        for (int i = 339; i < 354; i++) {
            String filename = baseFilename + i + ".txt";
            filenameList.add(filename);
        }
        FileUtils.mergeLabelContent(filenameList, newFilePath);
    }

    @Test
    public void departAugLabel() {
        String sLabel = "D:\\wjs\\processed_data_set\\augLabel.txt";
        String dLabel_hard = "D:\\wjs\\processed_data_set\\augLabel_hard.txt";
        String dLabel = "D:\\wjs\\processed_data_set\\augLabel_new.txt";
        String[] v = {"0", "1", "2", "3", "4", "5", "6", "7", "8"
                , "10", "11", "12", "13", "14", "15"
                , "17", "18", "19", "20", "21"
                , "24", "25", "26"
                , "28", "29", "30"
                , "38", "39"
                , "41", "42", "43", "44"
                , "47", "51", "52", "53", "55", "56", "58", "60", "75", "76", "77", "79", "80", "81", "82", "83"
                , "85", "86", "88", "89", "90", "91"
                , "107", "108", "119", "135", "158", "199", "213", "224", "281", "301"
                , "303", "304", "305", "306", "307", "308", "309"
                , "313", "321", "352", "353"
        };
        List<String> vList = Arrays.asList(v);
        Stream<String> hardLabel = FileUtils.gainFileContent(sLabel)
                .filter(s -> vList.contains(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))));
        Stream<String> label = FileUtils.gainFileContent(sLabel)
                .filter(s -> !vList.contains(s.substring(s.indexOf("/") + 1, s.lastIndexOf("/"))));
        FileUtils.writeLinesToNewFile(new File(dLabel_hard), hardLabel);
        FileUtils.writeLinesToNewFile(new File(dLabel), label);
    }

    @Test
    public void cleanNotExist() {
        String label = "D:\\wjs\\processed_data_set\\augLabel_hard.txt";
        String cleanDir = "D:\\wjs\\processed_data_set\\aug_hard";
        File cleanFile = new File(cleanDir);
        File[] list = cleanFile.listFiles();
        List<String> allList = new ArrayList<>();
        for (var s : list) {
            String[] names = s.list();
            String dirName = s.getName();
            for (var name : names) {
                allList.add("/" + dirName + "/" + name);
            }
        }
        Stream<String> stringStream = FileUtils.gainFileContent(label).filter(l -> allList.stream().anyMatch(l::contains));
        FileUtils.writeLinesToNewFile(new File("D:\\wjs\\processed_data_set\\augLabel_hard_clean.txt"), stringStream);
    }
}
