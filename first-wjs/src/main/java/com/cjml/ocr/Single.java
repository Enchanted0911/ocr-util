package com.cjml.ocr;

import com.cjml.ocr.constant.CommonConstants;
import com.cjml.util.FileUtils;
import com.cjml.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class Single {

    public static String trainDetDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_DET_DIR_KEY);
    public static String evalDetDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DET_DIR_KEY);
    public static String evalRecLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_REC_LABEL_KEY);
    public static String evalDetLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DET_LABEL_KEY);
    public static String evalRecDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_REC_DIR_KEY);

    public static String alignEvalRecDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants. ALIGN_EVAL_REC_DIR);
    public static String alignEvalDetDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants. ALIGN_EVAL_DET_DIR);
    public static String alignEvalDetFile = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_EVAL_DET_FILE);
    public static String alignEvalRecFile = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_EVAL_REC_FILE);

    public static String alignTrainRecDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants. ALIGN_TRAIN_REC_DIR);
    public static String alignTrainDetDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants. ALIGN_TRAIN_DET_DIR);
    public static String alignTrainDetFile = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_TRAIN_DET_FILE);
    public static String alignTrainRecFile = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_TRAIN_REC_FILE);

    public static void main(String[] args) {
//        alignLabelAndDataSet();
//        singleDetTrainEval();
        fullClean();
//        halfClean();
//        simpleSingleDetTrainEval(trainDetDir, evalDetDir, evalDetLabelPath);
//        regularizeDirInLabelFile(alignTrainRecFile, "rec_data");
//        alignLabelAndDataSet(alignTrainDetFile, alignTrainDetDir);
    }




    public static void fullClean() {

        // 先去交集
        singleDetTrainEval(trainDetDir, evalDetDir, evalRecDir, evalDetLabelPath, evalRecLabelPath);

        // 再对齐eval和train的det和rec的数据 以det为准
        alignDetAndRecDataSet(alignEvalDetDir, alignEvalRecDir);
        alignDetAndRecDataSet(alignTrainDetDir, alignTrainRecDir);

        // 分别对齐det和rec的label和data 以data为准
        alignLabelAndDataSet(alignEvalRecFile, alignEvalRecDir);
        alignLabelAndDataSet(alignEvalDetFile, alignEvalDetDir);

        alignLabelAndDataSet(alignTrainRecFile, alignTrainRecDir);
        alignLabelAndDataSet(alignTrainDetFile, alignTrainDetDir);
    }

    public static void halfClean() {

        alignDetAndRecDataSet("C:\\Users\\Administrator\\Desktop\\alpha_eval\\XZM", "C:\\Users\\Administrator\\Desktop\\alpha_eval\\XZM\\crop_img");

        alignLabelAndDataSet("C:\\Users\\Administrator\\Desktop\\alpha_eval\\XZM\\rec_gt.txt", "C:\\Users\\Administrator\\Desktop\\alpha_eval\\XZM\\crop_img");
        alignLabelAndDataSet("C:\\Users\\Administrator\\Desktop\\alpha_eval\\XZM\\Label.txt", "C:\\Users\\Administrator\\Desktop\\alpha_eval\\XZM");
    }

    /**
     * 验证集和训练集有时候会有重复数据, 这样在做模型评估的时候会影响评估指标, 所以写一个工具, 来去除验证集中已经存在于训练集中的数据
     * 同时更新用于det的label文件 以及 用于rec的rec_gt文件
     * <p>
     * 图片数据删除就是直接删除, 使用前请备份!!!
     * 标签文件的修改不会影响原标签文件, 会新建一个标签文件, 文件内容就是修改后的标签内容
     *
     * @param trainDir     训练数据目录
     * @param evalDir      评估数据目录
     * @param evalRecDir   识别数据目录
     * @param evalDetLabelPath 检测标签文件路径
     * @param evalRecLabelPath 识别标签文件路径
     */
    public static void singleDetTrainEval(String trainDir, String evalDir, String evalRecDir, String evalDetLabelPath, String evalRecLabelPath) {

        // 删除det的交集
        List<String> interSectionList = cleanIntersection(trainDir, evalDir);

        // 获取rec目录下的文件
        List<String> recFileList = FileUtils.gainAllFileName(evalRecDir);

        // 获取rec数据集中的由以上代码得出的交集部分产生的图片
        List<String> recIntersectionList = recFileList.stream().filter(r -> interSectionList.stream()
                .anyMatch(i -> r.contains(i.substring(0, i.lastIndexOf("."))))).collect(Collectors.toList());

        System.out.println(evalRecDir + " 下文件总量 : " + recFileList.size());
        System.out.println(evalRecDir + " 下交集文件数量 : " + recIntersectionList.size());
        // 删除其中一个目录下交集中的图片
        FileUtils.removeFiles(recIntersectionList, evalRecDir);

        // 修改其中一个label中的交集信息标签
        FileUtils.writeNewFileLabel(evalDetLabelPath, interSectionList);

        // 修改其中一个rec_gt中的交集信息标签
        FileUtils.writeNewFileLabel(evalRecLabelPath, recIntersectionList);
    }

    /**
     * 对齐label文件和数据目录
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

        File oldLabelFile = new File(labelPath);

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, filterLines);
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

        System.out.println("rec - det 差集文件数量 : " + subList.size());
        // 删除rec中的差集文件
        FileUtils.removeFiles(subList, recDir);
    }

    /**
     * 针对检测模型的简单版本去交集
     *
     * @param trainDir 训练图片目录
     * @param evalDir 验证图片目录
     * @param evalDetLabelPath 验证标签路径
     */
    public static void simpleSingleDetTrainEval(String trainDir, String evalDir, String evalDetLabelPath) {

        // 分别列出两个目录的所有文件名
        List<String> trainFileList = FileUtils.gainAllFileName(trainDir);
        List<String> evalFileList = FileUtils.gainAllFileName(evalDir);

        // 求文件名交集
        List<String> interSectionList = FileUtils.gainIntersection(trainFileList, evalFileList);

        System.out.println(trainDir + " 下文件数量 : " + trainFileList.size());
        System.out.println(evalDir + " 下文件数量 : " + evalFileList.size());
        System.out.println("交集文件数量 : " + interSectionList.size());
        // 删除其中一个目录下交集中的文件
        FileUtils.removeFiles(interSectionList, evalDir);

        // 修改其中一个label中的交集信息标签
        FileUtils.writeNewFileLabel(evalDetLabelPath, interSectionList);
    }

    /**
     * 规则化标签文件中记录标签的父目录, 并且去除相同的记录
     *
     * @param filepath 标签文件路径
     * @param parentDirName 规则化后的父目录名
     */
    public static void regularizeDirInLabelFile(String filepath, String parentDirName) {
        Stream<String> lines = FileUtils.gainFileContent(filepath);

        assert lines != null;
        Stream<String> newLines = lines.map(l -> parentDirName + l.substring(l.indexOf('/')))
                .collect(Collectors.collectingAndThen(Collectors
                        .toCollection(() -> new TreeSet<>(Comparator
                                .comparing(s -> s.substring(0, s.indexOf('.'))))), ArrayList::new)).stream();

        File oldLabelFile = new File(filepath);

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, newLines);
    }

    /**
     * 功能同上, 只不过这是针对特殊数据标签处理的, 有些数据标签没有父目录, 这个添加父目录
     *
     * @param filepath 同上
     * @param parentDirName 同上
     * @param flag 仅做重载区分
     */
    public static void regularizeDirInLabelFile(String filepath, String parentDirName, boolean flag) {
        Stream<String> lines = FileUtils.gainFileContent(filepath);

        assert lines != null;
        Stream<String> newLines = lines.map(l -> parentDirName + com.cjml.constant.CommonConstants.SLASH + l)
                .collect(Collectors.collectingAndThen(Collectors
                        .toCollection(() -> new TreeSet<>(Comparator
                                .comparing(s -> s.substring(0, s.indexOf('.'))))), ArrayList::new)).stream();

        File oldLabelFile = new File(filepath);

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, newLines);
    }

    /**
     * 修改标签文件中的空格为制表符
     *
     * @param filepath 待修改的文件路径
     * @param rawString 待修改的字符串
     * @param newString 新字符串
     */
    public static void changeSpecialChar(String filepath, String rawString, String newString) {
        Stream<String> lines = FileUtils.gainFileContent(filepath);

        assert lines != null;
        Stream<String> newLines = lines.map(l -> l.replace(rawString, newString));

        File oldLabelFile = new File(filepath);

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, newLines);
    }
//    /**
//     * 规则化标签文件中记录标签的父目录
//     *
//     * @param filepath 标签文件路径
//     * @param parentDirName 规则化后的父目录名
//     */
//    public static void regularizeDirInLabelFile(String filepath, String parentDirName) {
//        Stream<String> lines = FileUtils.gainFileContent(filepath);
//
//        assert lines != null;
//        Stream<String> newLines = lines.map(l -> parentDirName + l.substring(l.indexOf('/')));
//
//        File oldLabelFile = new File(filepath);
//
//        // 写入新文件
//        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
//        FileUtils.writeLinesToNewFile(newFile, newLines);
//    }

    /**
     * 删除其中一个目录中的交集文件
     *
     * @param trainDir 训练数据目录
     * @param evalDir 验证数据目录
     * @return 交集文件名list
     */
    public static List<String> cleanIntersection(String trainDir, String evalDir) {
        // 分别列出两个目录的所有文件名
        List<String> trainFileList = FileUtils.gainAllFileName(trainDir);
        List<String> evalFileList = FileUtils.gainAllFileName(evalDir);

        // 求文件名交集
        List<String> interSectionList = FileUtils.gainIntersection(trainFileList, evalFileList);

        System.out.println(trainDir + " 下文件数量 : " + trainFileList.size());
        System.out.println(evalDir + " 下文件数量 : " + evalFileList.size());
        System.out.println("交集文件数量 : " + interSectionList.size());
        // 删除其中一个目录下交集中的文件
        FileUtils.removeFiles(interSectionList, evalDir);

        return interSectionList;
    }


    /**
     * 针对开源数据集做的数据标签修正
     *
     * @param filepath 标签文件路径
     */
    public static void fixEngLabel(String filepath) {
        Stream<String> lines = FileUtils.gainFileContent(filepath);

        assert lines != null;
        Stream<String> newLines = lines.map(l -> {
            String real = l.substring(l.indexOf('_') + 1, l.lastIndexOf('_'));
            return l.substring(0, l.indexOf(' ') + 1) + real;
        });

        File oldLabelFile = new File(filepath);

        // 写入新文件
        File newFile = new File(oldLabelFile.getParent() + "/new_" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, newLines);
    }
}
