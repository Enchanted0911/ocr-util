package com.cjml.ocr;

import com.cjml.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cjml.util.FileUtils.gainFileContent;
import static com.cjml.util.FileUtils.writeLinesToNewFile;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class AlphaSingle {

    public static void main(String[] args) {
//        cleanDetIntersection("D:\\wjs\\ocr_data_set_rec");
//        cleanDetIntersection("D:\\wjs\\ocr_temp_train");
//        cleanOthersIntersection("D:\\wjs\\ocr_data_set_rec");
//        mergeDataSet("D:\\wjs\\ocr_train_nameplate");
//        mergeDataSet("D:\\wjs\\ocr_eval_nameplate");
//        mergeDataSet("D:\\wjs\\ocr_eval_vin");
//        mergeDataSet("D:\\wjs\\ocr_train_vin");
//        mergeDataSet("D:\\wjs\\ocr_temp_train");
//        mergeDataSet("D:\\wjs\\ocr_temp_eval");
//        resetLabelCache("D:\\wjs\\ocr_data_set_rec", "vin_license_750");
//        departChEng("D:\\wjs\\ocr_data_set_clean\\vin_250_license\\new_rec_gt.txt");
        departChEng("D:\\wjs\\ocr_temp_train\\merge_data\\blur9.txt");
//        departChEng("D:\\wjs\\ocr_temp_eval\\merge_data\\rec_gt_eval.txt");

//        mergeDetData("D:\\wjs\\ocr_train_nameplate");
//        mergeDetData("D:\\wjs\\ocr_train_vin");
//        mergeDetData("D:\\wjs\\ocr_eval_vin");
//        mergeDetData("D:\\wjs\\ocr_eval_nameplate");
//        mergeDetData("D:\\wjs\\ocr_temp_eval", "rec_temp_eval");
//        mergeDetData("D:\\wjs\\ocr_temp_train", "rec_temp_train");
//        alignLabelAndDataSet("D:\\wjs\\ocr_train_vin\\merge_data\\new_Label.txt", "D:\\wjs\\ocr_train_vin\\merge_data\\det_vin_train", false);
//        alignLabelAndDataSet("D:\\wjs\\ocr_train_nameplate\\merge_data\\new_Label.txt", "D:\\wjs\\ocr_train_nameplate\\merge_data\\det_nameplate_train", false);
//        alignLabelAndDataSet("D:\\wjs\\ocr_eval_vin\\merge_data\\new_Label.txt", "D:\\wjs\\ocr_eval_vin\\merge_data\\det_vin_eval", false);
//        alignLabelAndDataSet("D:\\wjs\\ocr_eval_nameplate\\merge_data\\new_Label.txt", "D:\\wjs\\ocr_eval_nameplate\\merge_data\\det_nameplate_eval", false);
    }


    /**
     * 去除多个文件夹下的图片文件交集
     *
     * @param dirPath 多个文件夹父目录
     */
    public static void cleanDetIntersection(String dirPath) {
        int cnt = 0;
        File dirPathFile = new File(dirPath);
        File[] fs = dirPathFile.listFiles();

        HashSet<String> imgSet = new HashSet<>(2048);

        assert fs != null;
        for (File f : fs) {
            int subCnt = 0;
            File[] subFileList = f.listFiles();
            assert subFileList != null;
            for (File subF : subFileList) {
                if (!FileUtils.commonList.contains(subF.getName()) && !imgSet.add(subF.getName())) {
                    subCnt++;
                    cnt++;
                    subF.delete();
                }
            }
            System.out.println(f.getName() + " : " + subCnt);
        }
        System.out.println("all : " + cnt);
    }


    /**
     * 清除该文件夹下所有文件夹在det中已经不存在原图片，而rec中还保留的裁剪图片
     * 对齐label和图片
     *
     * @param dirPath 待清除文件夹父目录
     */
    public static void cleanOthersIntersection(String dirPath) {
        File dirPathFile = new File(dirPath);
        File[] fs = dirPathFile.listFiles();
        assert fs != null;
        for (File f : fs) {
            alignDetAndRecDataSet(f.getAbsolutePath(), f.getAbsolutePath() + "\\crop_img");
            alignLabelAndDataSet(f.getAbsolutePath() + "\\Label.txt", f.getAbsolutePath(), false);
            alignLabelAndDataSet(f.getAbsolutePath() + "\\rec_gt.txt"
                    , f.getAbsolutePath() + "\\crop_img", true);
        }
    }

    public static void mergeDataSet(String mergeParentDir) {
        File dirPathFile = new File(mergeParentDir);
        File[] fs = dirPathFile.listFiles();
        List<File> detFileList = new ArrayList<>();
        List<File> recFileList = new ArrayList<>();
        List<String> detContent = new ArrayList<>();
        List<String> recContent = new ArrayList<>();
        assert fs != null;
        for (File f : fs) {
            File[] subFileList = f.listFiles();
            assert subFileList != null;
            for (File subF : subFileList) {
                if (!FileUtils.commonList.contains(subF.getName())) {
                    detFileList.add(subF);
                } else if (subF.isDirectory()) {
                    recFileList.addAll(List.of(Objects.requireNonNull(subF.listFiles())));
                } else if ("Label.txt".equals(subF.getName())) {
                    List<String> content = Objects.requireNonNull(gainFileContent(subF.getAbsolutePath()))
                            .collect(Collectors.toList());
                    detContent.addAll(content);
                } else if ("rec_gt.txt".equals(subF.getName())) {
                    List<String> content = Objects.requireNonNull(gainFileContent(subF.getAbsolutePath()))
                            .collect(Collectors.toList());
                    recContent.addAll(content);
                }
            }
        }

        File mergeDetLabel = new File(mergeParentDir + "\\merge_data\\Label.txt");
        File detData = new File(mergeParentDir + "\\merge_data\\det_nameplate_train");
        detData.mkdirs();
        File mergeRecLabel = new File(mergeParentDir + "\\merge_data\\rec_gt.txt");
        File recData = new File(mergeParentDir + "\\merge_data\\rec_nameplate_train");
        recData.mkdirs();
        try {
            mergeDetLabel.createNewFile();
            mergeRecLabel.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.writeLinesToNewFile(mergeDetLabel, detContent.stream());
        FileUtils.writeLinesToNewFile(mergeRecLabel, recContent.stream());
        detFileList.forEach(d -> {
            try {
                org.apache.commons.io.FileUtils
                        .copyFile(d, new File(mergeParentDir + "\\merge_data\\det_nameplate_train\\" + d.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        recFileList.forEach(d -> {
            try {
                org.apache.commons.io.FileUtils
                        .copyFile(d, new File(mergeParentDir + "\\merge_data\\rec_nameplate_train\\" + d.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public static void mergeDetData(String mergeParentDir, String dirName) {
        File dirPathFile = new File(mergeParentDir);
        File[] fs = dirPathFile.listFiles();
        List<File> detFileList = new ArrayList<>();
        List<String> detContent = new ArrayList<>();

        List<String> cacheList = new ArrayList<>();
        assert fs != null;
        for (File f : fs) {
            File[] subFileList = f.listFiles();
            assert subFileList != null;
            for (File subF : subFileList) {
                if (!FileUtils.commonList.contains(subF.getName())) {
                    detFileList.add(subF);
                    cacheList.add(dirPathFile.getAbsolutePath() + File.separator + "merge_data" + File.separator + dirName + File.separator + subF.getName() + "\t1");
                } else if ("Label.txt".equals(subF.getName())) {
                    List<String> content = Objects.requireNonNull(gainFileContent(subF.getAbsolutePath()))
                            .collect(Collectors.toList());
                    detContent.addAll(content);
                }
            }
        }


        File mergeDetLabel = new File(mergeParentDir + "\\merge_data\\Label.txt");
        File detData = new File(mergeParentDir + "\\merge_data\\" + dirName);
        detData.mkdirs();

        try {
            mergeDetLabel.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.writeLinesToNewFile(new File(dirPathFile.getAbsolutePath() + File.separator + "merge_data" + "\\fileState.txt"), cacheList.stream());
        FileUtils.writeLinesToNewFile(mergeDetLabel, detContent.stream());
        detFileList.forEach(d -> {
            try {
                org.apache.commons.io.FileUtils
                        .copyFile(d, new File(mergeParentDir + "\\merge_data\\" + dirName + "\\" + d.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public static void mergeRecData(String mergeParentDir) {
        File dirPathFile = new File(mergeParentDir);
        File[] fs = dirPathFile.listFiles();
        List<File> recFileList = new ArrayList<>();
        List<String> recContent = new ArrayList<>();
        assert fs != null;
        for (File f : fs) {
            File[] subFileList = f.listFiles();
            assert subFileList != null;
            for (File subF : subFileList) {
                if (subF.isDirectory()) {
                    recFileList.addAll(List.of(Objects.requireNonNull(subF.listFiles())));
                } else if ("rec_gt.txt".equals(subF.getName())) {
                    List<String> content = Objects.requireNonNull(gainFileContent(subF.getAbsolutePath()))
                            .collect(Collectors.toList());
                    recContent.addAll(content);
                }
            }
        }

        File mergeRecLabel = new File(mergeParentDir + "\\merge_data\\rec_gt.txt");
        File recData = new File(mergeParentDir + "\\merge_data\\rec_nameplate_train");
        recData.mkdirs();
        try {
            mergeRecLabel.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileUtils.writeLinesToNewFile(mergeRecLabel, recContent.stream());
        recFileList.forEach(d -> {
            try {
                org.apache.commons.io.FileUtils
                        .copyFile(d, new File(mergeParentDir + "\\merge_data\\rec_nameplate_train\\" + d.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 对齐det和rec文件, 有时候从det里删除不需要的图片更方便, 此时rec仍然存在det中被删除的图片的裁剪图片, 此方法用来对齐。
     *
     * @param detDir 检测图片目录
     * @param recDir 识别图片目录
     */
    public static void alignDetAndRecDataSet(String detDir, String recDir) {
        // 获取目录下的所有文件名集合
        List<String> detDataNameList = FileUtils.gainAllFileName(detDir);
        List<String> recDataNameList = FileUtils.gainAllFileName(recDir);

        // det 文件目录排除非图片文件
        detDataNameList = detDataNameList.stream()
                .filter(i -> FileUtils.commonList.stream().noneMatch(i::contains)).collect(Collectors.toList());

        // 获取rec - det 差集的文件名集合
        List<String> finalDetDataNameList = detDataNameList;
        List<String> subList = recDataNameList.stream()
                .filter(r -> finalDetDataNameList.stream()
                        .noneMatch(i -> r.contains(i.substring(0, i.lastIndexOf("."))))).collect(Collectors.toList());

        System.out.println(recDir + " : " + subList.size());
        // 删除rec中的差集文件
        FileUtils.removeFiles(subList, recDir);
    }

    /**
     * 对齐label文件和数据目录,直接修改原标签
     *
     * @param labelPath 标签文件路径
     * @param dataDir   数据文件目录
     */
    public static void alignLabelAndDataSet(String labelPath, String dataDir, boolean flag) {
        // 获取目录下的所有文件名集合
        List<String> dataNameList = FileUtils.gainAllFileName(dataDir);

        // 获取文本文件中的内容
        Stream<String> lines = gainFileContent(labelPath);

        assert lines != null;
        // 将文本文件中的内容过滤掉文件名集合中不存在的数据的记录
        Stream<String> filterLines;
        if (flag) {
            filterLines = lines.filter(l -> dataNameList.stream().anyMatch(l::contains) && !l.contains("无法识别") && !l.contains("待识别") && !l.contains("TEMPORARY"));
        } else {
            filterLines = lines.filter(l -> dataNameList.stream().anyMatch(l::contains));
        }


        List<String> filterList = filterLines.collect(Collectors.toList());
        Stream<String> filterLines2 = filterList.stream();
        FileUtils.writeLinesToNewFile(new File(labelPath), filterLines2);
    }

    /**
     * 将ppocrlabel标注数据的确认标志置为1 方便导出识别数据
     *
     * @param dirPath 父目录
     * @param subDir  待重置的标注目录
     */
    public static void resetLabelCache(String dirPath, String subDir) {
        File dirPathFile = new File(dirPath);
        File[] fs = dirPathFile.listFiles();
        assert fs != null;
        for (File f : fs) {

//            if (!f.getName().equals(subDir)) {
//                System.out.println("next------------");
//                continue;
//            }

            List<String> cacheList = new ArrayList<>();
            File[] subFileList = f.listFiles();
            assert subFileList != null;
            for (File subF : subFileList) {
                if (!FileUtils.commonList.contains(subF.getName())) {
                    cacheList.add(subF.getAbsolutePath() + "\t1");
                }
            }
            FileUtils.writeLinesToNewFile(new File(f.getAbsolutePath() + "\\fileState.txt"), cacheList.stream());
        }
    }

    public static void departChEng(String recTxt) {
        List<String> recList = Objects.requireNonNull(gainFileContent(recTxt)).collect(Collectors.toList());
        var chList = new ArrayList<String>();
        var engList = new ArrayList<String>();
        recList.forEach(r -> {
            var x = r.split("\t")[1];
//            if (x.matches(".*[a-zA-Z0-9].*")) {
            if (x.matches("[a-zA-Z0-9]+") && x.length() == 17) {
                engList.add(r);
            } else {
                chList.add(r);
            }
        });
        File f = new File(recTxt);

        FileUtils.writeLinesToNewFile(new File(f.getParent() + "\\engList.txt"), engList.stream());
        FileUtils.writeLinesToNewFile(new File(f.getParent() + "\\chList.txt"), chList.stream());
    }

    public static void generateCharAug(){
        var charDir = "D:\\aug_char";
        var labelPath = charDir + File.separator + "char_aug.txt";
        File charDirFile = new File(charDir);
        var charDirList = charDirFile.listFiles();
        var labelList = new ArrayList<String>();
        assert charDirList != null;
        for(var f : charDirList) {
            var picList = f.listFiles();
            var rec = f.getName().replace("_aug", "");
            assert picList != null;
            for(var p : picList){
                var labelDir = charDirFile.getName() + "/" + f.getName() + "/" + p.getName();
                var label = labelDir + "\t" + rec;
                labelList.add(label);
            }
        }
        writeLinesToNewFile(new File(labelPath), labelList.stream());
    }
}
