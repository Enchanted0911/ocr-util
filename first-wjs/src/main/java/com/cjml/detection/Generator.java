package com.cjml.detection;

import com.cjml.constant.CommonConstants;
import com.cjml.detection.constant.FilePathConstants;
import com.cjml.util.FileUtils;
import com.cjml.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2021-12-17
 */
public class Generator {
    public static String annotationPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, FilePathConstants.ANNOTATION_PATH);
    public static String imagePath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, FilePathConstants.IMAGE_PATH);
    public static String trainLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, FilePathConstants.TRAIN_LABEL_PATH);
    public static String evalLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, FilePathConstants.EVAL_LABEL_PATH);

    public static void main(String[] args) throws Exception {
        generateLabel2(annotationPath, imagePath, trainLabelPath, evalLabelPath);
    }

    /**
     * 指定标签和数据文件目录, 以及生成的train和eval标注文件路径, 生成训练和评估标注文件
     *
     * @param annotationPath 标签文件目录
     * @param imagePath 数据文件目录
     * @param trainLabelPath 训练集标注文件路径
     * @param evalLabelPath 评估集标注文件路径
     * @throws Exception 标签文件和数据文件内容数目不一致时抛出异常
     */
    public static void generateLabel(String annotationPath, String imagePath, String trainLabelPath, String evalLabelPath) throws Exception {
        List<String> allAnnotationFileName = FileUtils.gainAllFileName(annotationPath);
        List<String> allImageFileName = FileUtils.gainAllFileName(imagePath);

        if (allAnnotationFileName.size() != allImageFileName.size()) {
            throw new Exception("标注文件与数据不匹配!");
        }

        File annotationFile = new File(annotationPath);
        File imageFile = new File(imagePath);

        String annotationParentDir = annotationFile.getName();
        String imageParentDir = imageFile.getName();

        List<String> labelContentList = new ArrayList<>();

        allImageFileName.forEach(i -> {
            String fullFileName = FileUtils.gainFullFileName(i);
            String justFileName = FileUtils.gainJustFileName(i);
            String label = CommonConstants.DOT + CommonConstants.SLASH + imageParentDir
                    + CommonConstants.SLASH + fullFileName + CommonConstants.SPACE_CHAR
                    + CommonConstants.DOT + CommonConstants.SLASH + annotationParentDir
                    + CommonConstants.SLASH + justFileName + CommonConstants.DOT + FilePathConstants.XML;
            labelContentList.add(label);
        });

        List<String> trainLabel = new ArrayList<>();
        List<String> evalLabel = new ArrayList<>();
        int ratio = 8;
        AtomicInteger cnt = new AtomicInteger(0);
        labelContentList.forEach(i -> {
            boolean b = (cnt.getAndIncrement() % ratio == 0) ? evalLabel.add(i) : trainLabel.add(i);
        });

        FileUtils.writeLinesToNewFile(new File(trainLabelPath), trainLabel.stream());
        FileUtils.writeLinesToNewFile(new File(evalLabelPath), evalLabel.stream());
    }

    public static void generateLabel2(String annotationPath, String imagePath, String trainLabelPath, String evalLabelPath) throws Exception {
        List<File> annotationFileList = new ArrayList<>();
        FileUtils.gainAllFile(new File(annotationPath), annotationFileList);
        List<File> imageFileList = new ArrayList<>();
        FileUtils.gainAllFile(new File(imagePath), imageFileList);
        imageFileList = imageFileList.stream().filter(i -> !"Thumbs.db".equals(i.getName())).collect(Collectors.toList());

        if (annotationFileList.size() != imageFileList.size()) {
            throw new Exception("标注文件与数据不匹配!");
        }

        File annotationFile = new File(annotationPath);
        File imageFile = new File(imagePath);

        String annotationParentDir = annotationFile.getName();
        String imageParentDir = imageFile.getName();

        List<String> labelContentList = new ArrayList<>();

        imageFileList.stream().filter(i -> !"Thumbs.db".equals(i.toString())).forEach(i -> {
            String fullFileName = FileUtils.gainFullFileName(i.toString());
            String justFileName = FileUtils.gainJustFileName(i.toString());
            String label = CommonConstants.DOT + CommonConstants.SLASH + imageParentDir
                    + CommonConstants.SLASH + i.getParentFile().getName() + CommonConstants.SLASH
                    + fullFileName + CommonConstants.SPACE_CHAR
                    + CommonConstants.DOT + CommonConstants.SLASH + annotationParentDir
                    + CommonConstants.SLASH + i.getParentFile().getName() + CommonConstants.SLASH
                    + justFileName + CommonConstants.DOT + FilePathConstants.XML;
            labelContentList.add(label);
        });

        List<String> trainLabel = new ArrayList<>();
        List<String> evalLabel = new ArrayList<>();
        int ratio = 10;
        AtomicInteger cnt = new AtomicInteger(0);
        labelContentList.forEach(i -> {
            boolean b = (cnt.getAndIncrement() % ratio == 0) ? evalLabel.add(i) : trainLabel.add(i);
        });

        FileUtils.writeLinesToNewFile(new File(trainLabelPath), trainLabel.stream());
        FileUtils.writeLinesToNewFile(new File(evalLabelPath), evalLabel.stream());
    }

    /**
     * 找出没有标注文件的图片
     *
     * @param imagePath 图片目录
     * @param annotationPath 标注文件目录
     * @return 未标注的图片名集合
     */
    public static List<String> findNoLabel(String imagePath, String annotationPath) {
        List<String> noLabelList;
        // 分别列出两个目录的所有文件名
        List<String> imageFileList = FileUtils.gainAllFileName(imagePath);
        List<String> annotationFileList = FileUtils.gainAllFileName(annotationPath);

        noLabelList = imageFileList.stream().filter(i -> {
            for (var j : annotationFileList) {
                if (i.contains(j.substring(0, j.lastIndexOf(CommonConstants.DOT)))) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        return noLabelList;
    }

    /**
     * 找到标注了一个以上的目标的标注文件
     *
     * @param fileDir 待查找的目录
     * @return 所有标注了一个以上目标的标注文件名
     */
    public static List<String> findMultiObjectLabel(String fileDir) {
        List<String> filenameList = new ArrayList<>();
        List<File> annotationFileList = new ArrayList<>();
        FileUtils.gainAllFile(new File(fileDir), annotationFileList);
        annotationFileList.forEach(f -> {
            Stream<String> lines = FileUtils.gainFileContent(f.getAbsolutePath());
            long num = lines.filter(l -> l.contains("<object>")).count();
            if (num > 1) {
                filenameList.add(f.getAbsolutePath());
            }
        });
        return filenameList;
    }
}
