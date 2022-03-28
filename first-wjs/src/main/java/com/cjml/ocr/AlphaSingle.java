package com.cjml.ocr;

import com.cjml.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class AlphaSingle {

    public static void main(String[] args) {
//        cleanDetIntersection("D:\\wjs\\ocr_data_set_clean");
//        cleanOthersIntersection("D:\\wjs\\ocr_data_set_clean");
        mergeDataSet("D:\\wjs\\ocr_train");
        mergeDataSet("D:\\wjs\\ocr_eval");
//        resetLabelCache("D:\\wjs\\ocr_data_set_clean");
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
     * @param dirPath 待清楚文件夹父目录
     */
    public static void cleanOthersIntersection(String dirPath) {
        File dirPathFile = new File(dirPath);
        File[] fs = dirPathFile.listFiles();
        assert fs != null;
        for (File f : fs) {
            alignDetAndRecDataSet(f.getAbsolutePath(), f.getAbsolutePath() + "\\crop_img");
            alignLabelAndDataSet(f.getAbsolutePath() + "\\Label.txt", f.getAbsolutePath());
            alignLabelAndDataSet(f.getAbsolutePath() + "\\rec_gt.txt"
                    , f.getAbsolutePath() + "\\crop_img");
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
                    List<String> content = Objects.requireNonNull(FileUtils.gainFileContent(subF.getAbsolutePath()))
                            .collect(Collectors.toList());
                    detContent.addAll(content);
                } else if ("rec_gt.txt".equals(subF.getName())) {
                    List<String> content = Objects.requireNonNull(FileUtils.gainFileContent(subF.getAbsolutePath()))
                            .collect(Collectors.toList());
                    recContent.addAll(content);
                }
            }
        }

        File mergeDetLabel = new File(mergeParentDir + "\\merge_data\\Label.txt");
        File detData = new File(mergeParentDir + "\\merge_data\\det_data");
        detData.mkdirs();
        File mergeRecLabel = new File(mergeParentDir + "\\merge_data\\rec_gt.txt");
        File recData = new File(mergeParentDir + "\\merge_data\\rec_data");
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
                        .copyFile(d, new File(mergeParentDir + "\\merge_data\\det_data\\" + d.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        recFileList.forEach(d -> {
            try {
                org.apache.commons.io.FileUtils
                        .copyFile(d, new File(mergeParentDir + "\\merge_data\\rec_data\\" + d.getName()));
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
    public static void alignLabelAndDataSet(String labelPath, String dataDir) {

        // 获取目录下的所有文件名集合
        List<String> dataNameList = FileUtils.gainAllFileName(dataDir);

        // 获取文本文件中的内容
        Stream<String> lines = FileUtils.gainFileContent(labelPath);
        assert lines != null;

        // 将文本文件中的内容过滤掉文件名集合中不存在的数据的记录
        Stream<String> filterLines = lines.filter(l -> dataNameList.stream().anyMatch(l::contains));

        List<String> filterList = filterLines.collect(Collectors.toList());
        Stream<String> filterLines2 = filterList.stream();
        FileUtils.writeLinesToNewFile(new File(labelPath), filterLines2);
    }

    public static void resetLabelCache(String dirPath){
        File dirPathFile = new File(dirPath);
        File[] fs = dirPathFile.listFiles();
        assert fs != null;
        for (File f : fs) {
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
}
