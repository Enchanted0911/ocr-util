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

import static com.cjml.ocr.AlphaSingle.generateCharAug;

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
    public void regularDir() {
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_nameplate\\merge_data\\Label.txt", "det_nameplate_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_nameplate\\merge_data\\rec_gt.txt", "rec_nameplate_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_nameplate\\merge_data\\Label.txt", "det_nameplate_eval");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_nameplate\\merge_data\\rec_gt.txt", "rec_nameplate_eval");
//        Single.regularizeDirInLabelFile("D:\\wjs\\PycharmProjects\\end2end_eval\\Label_hard.txt", "200difficult_1");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_vin\\merge_data\\Label.txt", "det_vin_eval");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_eval_vin\\merge_data\\rec_gt.txt", "rec_vin_eval");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_vin\\merge_data\\Label.txt", "det_vin_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_train_vin\\merge_data\\rec_gt.txt", "rec_vin_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_data_set_clean\\vin_250_license\\rec_gt.txt", "rec_temp_eval");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_data_set_clean\\second_1k\\rec_gt.txt", "rec_temp_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_temp_eval\\merge_data\\Label.txt", "rec_temp_eval");
        Single.regularizeDirInLabelFile("D:\\wjs\\ocr_temp_train\\merge_data\\Label.txt", "rec_temp_train");
//        Single.regularizeDirInLabelFile("D:\\wjs\\4label\\just_vin_500\\Label.txt", "det_vin_train");
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
        int i = 379;
        String imageDir = "D:\\wjs\\processed_data_set\\aug_generate\\" + i;
        String desLabelPath = "D:\\wjs\\processed_data_set\\augLabel" + i + ".txt";
        String labelDir = "aug_generate/" + i + "/";
        String labelName = "C";
        Single.generateAugLabel(imageDir, desLabelPath, labelDir, labelName);
    }

    @Test
    public void mergeLabelContent() {
        String baseFilename = "D:\\wjs\\processed_data_set\\augLabel";
        String newFilePath = "D:\\wjs\\processed_data_set\\augLabel_01.txt";
        List<String> filenameList = new ArrayList<>();
        for (int i = 366; i < 380; i++) {
            if (i == 374){
                continue;
            }
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
    public void departAugLabel2() {
        String sLabel = "D:\\wjs\\processed_data_set\\augLabel.txt";
        String dLabel_hard = "D:\\wjs\\processed_data_set\\augLabel_hard.txt";
        String dLabel = "D:\\wjs\\processed_data_set\\augLabel_new.txt";
        String[] v = {"16", "23", ""
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
        String label = "D:\\wjs\\processed_data_set\\augLabel_new_0915.txt";
        String cleanDir = "D:\\wjs\\processed_data_set\\aug_generate";
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
        FileUtils.writeLinesToNewFile(new File("D:\\wjs\\processed_data_set\\augLabel_new_0915_clean.txt"), stringStream);
    }

    @Test
    public void fixSomething(){
        String filepath = "D:\\wjs\\PycharmProjects\\end2end_eval\\diff_eval.txt";
        Stream<String> lines = FileUtils.gainFileContent(filepath);

        assert lines != null;
        Stream<String> newLines = lines.filter(l -> {
            var x = l.split(" ");
            if (x[2].length() == 4) {
                return true;
            }
            x[2] = x[2].substring(2, 19);
            return !x[1].equals(x[2]);
        }).map(l -> l.replace("['", "").replace("']", ""));

        File oldLabelFile = new File(filepath);

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, newLines);

    }

    @Test
    public void xxx11(){
        String x = "D:\\diff_vin_eval_5k_old\\1";
        String y = "D:\\wjs\\4label\\vin_1k";
//        FileUtils.cleanNotNeed(x, y);

        FileUtils.moveNotNeed(x, y);
    }

    @Test
    public void statistic(){
        String filePath = "D:\\wjs\\ocr_temp_train\\merge_data\\new_rec_gt.txt";
        var fileContent = FileUtils.gainFileContent(filePath).collect(Collectors.toList());
        var staMap = new HashMap<Character, Integer>(256);
        var staResList = new ArrayList<String>();
        fileContent.forEach(fc -> {
            var path = fc.split("\t")[0];
            var rec = fc.split("\t")[1].replace("\n", "");
            for (var i = 0; i < rec.length(); i++) {
                if (staMap.containsKey(rec.charAt(i))) {
                    staMap.put(rec.charAt(i), staMap.get(rec.charAt(i)) + 1);
                } else {
                    staMap.put(rec.charAt(i), 1);
                }
            }
        });
        for (var i = 'A'; i <= 'Z'; i++) {
            System.out.println(i + "---------" + staMap.get(i));
        }
        for (var i = '0'; i <= '9'; i++) {
            System.out.println(i + "---------" + staMap.get(i));
        }
    }

    @Test
    public void generateCharAugLabel(){
        generateCharAug();
    }
}
