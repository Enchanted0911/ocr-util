package com.cjml.ocr;

import com.cjml.ocr.constant.CommonConstants;
import com.cjml.ocr.util.FileUtils;
import com.cjml.ocr.util.ResourceUtils;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class Single {

    public static void main(String[] args) {
        alignLabelAndDataSet();
//        singleDetTrainEval();
    }

    /**
     * 验证集和训练集有时候会有重复数据, 这样在做模型评估的时候会影响评估指标, 所以写一个工具, 来去除验证集中已经存在于训练集中的数据
     * 同时更新用于det的label文件 以及 用于rec的rec_gt文件
     *
     * 图片数据删除就是直接删除, 使用前请备份!!!
     * 标签文件的修改不会影响原标签文件, 会新建一个标签文件, 文件内容就是修改后的标签内容
     *
     * 需要哪些数据 :
     *      1. det数据目录 (其中一个目录删除数据, 另一个只是配合比较取交集 两个)
     *      2. rec数据目录 (需要删除数据的目录 一个)
     *      3. det标签文件绝对路径 (需要修改的det标签文件 一个)
     *      4. rec标签文件绝对路径 (需要修改的rec标签文件 一个)
     *
     * 使用前请在resource下的properties文件中修改对应的value, 其他的代码请勿修改!
     */
    public static void singleDetTrainEval() {

        // 分别列出两个目录的所有文件名
        List<String> trainFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_DIR_KEY);
        List<String> evalFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DIR_KEY);

        // 求文件名交集
        List<String> interSectionList = FileUtils.gainIntersection(trainFileList, evalFileList);

        // 删除其中一个目录下交集中的文件
        FileUtils.removeFiles(interSectionList, ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_DIR_KEY));

        // 获取rec目录下的文件
        List<String> recFileList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.REC_DIR_KEY);

        // 获取rec数据集中的由以上代码得出的交集部分产生的图片
        List<String> recIntersectionList = recFileList.stream().filter(r -> interSectionList.stream()
                .anyMatch(i -> r.contains(i.substring(0, i.lastIndexOf("."))))).collect(Collectors.toList());

        // 删除其中一个目录下交集中的图片
        FileUtils.removeFiles(recIntersectionList, ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.REC_DIR_KEY));

        // 删除其中一个label中的交集信息标签
        String detLabelDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.DET_LABEL_KEY);
        FileUtils.writeNewFileLabel(detLabelDir, interSectionList);

        // 删除其中一个rec_gt中的交集信息标签
        String recLabelDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.REC_LABEL_KEY);
        FileUtils.writeNewFileLabel(recLabelDir, recIntersectionList);
    }

    /**
     * 对齐label文件和数据目录
     *
     * 需要的数据 :
     *      1. 目录名
     *      2. 文件绝对路径
     *
     * 使用前请在resource下的properties文件中修改对应的value, 其他的代码请勿修改!
     */
    public static void alignLabelAndDataSet() {
        String filepath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_FILE);
        List<String> dataNameList = FileUtils.gainAllFileName(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.ALIGN_DIR);
        Stream<String> lines = FileUtils.gainFileContent(filepath);
        assert lines != null;
        Stream<String> filterLines = lines.filter(l -> dataNameList.stream().anyMatch(l::contains));
        File oldLabelFile = new File(filepath);
        File newFile = new File(oldLabelFile.getParent() + "/new" + oldLabelFile.getName());
        FileUtils.writeLinesToNewFile(newFile, filterLines);
    }

}
