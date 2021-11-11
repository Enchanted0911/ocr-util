package com.cjml.ocr;

import com.cjml.ocr.util.FileUtils;

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
//        alignLabelAndDataSet();
//        singleDetTrainEval();
    }

    /**
     * 验证集和训练集有时候会有重复数据, 这样在做模型评估的时候会影响评估指标, 所以写一个工具, 来去除验证集中已经存在于训练集中的数据
     * 同时更新用于det的label文件 以及 用于rec的rec_gt文件
     *
     * 图片数据删除就是直接删除, 使用前请备份!!!
     * 标签文件的修改不会影响原标签文件, 会新建一个标签文件, 文件内容就是修改后的标签内容
     *
     * @param trainDir 训练数据目录
     * @param evalDir 评估数据目录
     * @param evalRecDir 识别数据目录
     * @param detLabelPath 检测标签文件路径
     * @param recLabelPath 识别标签文件路径
     */
    public static void singleDetTrainEval(String trainDir, String evalDir, String evalRecDir, String detLabelPath, String recLabelPath) {

        // 分别列出两个目录的所有文件名
        List<String> trainFileList = FileUtils.gainAllFileName(trainDir);
        List<String> evalFileList = FileUtils.gainAllFileName(evalDir);

        // 求文件名交集
        List<String> interSectionList = FileUtils.gainIntersection(trainFileList, evalFileList);

        // 删除其中一个目录下交集中的文件
        FileUtils.removeFiles(interSectionList, evalDir);

        // 获取rec目录下的文件
        List<String> recFileList = FileUtils.gainAllFileName(evalRecDir);

        // 获取rec数据集中的由以上代码得出的交集部分产生的图片
        List<String> recIntersectionList = recFileList.stream().filter(r -> interSectionList.stream()
                .anyMatch(i -> r.contains(i.substring(0, i.lastIndexOf("."))))).collect(Collectors.toList());

        // 删除其中一个目录下交集中的图片
        FileUtils.removeFiles(recIntersectionList, evalRecDir);

        // 修改其中一个label中的交集信息标签
        FileUtils.writeNewFileLabel(detLabelPath, interSectionList);

        // 修改其中一个rec_gt中的交集信息标签
        FileUtils.writeNewFileLabel(recLabelPath, recIntersectionList);
    }

    /**
     * 对齐label文件和数据目录
     *
     * @param labelPath 标签文件路径
     * @param dataDir 数据文件目录
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

        // 获取rec - det 差集的文件名集合
        List<String> subList = recDataNameList.stream()
                .filter(r -> detDataNameList.stream()
                        .noneMatch(i -> r.contains(i.substring(0, i.lastIndexOf("."))))).collect(Collectors.toList());

        // 删除rec中的差集文件
        FileUtils.removeFiles(subList, recDir);
    }
}
