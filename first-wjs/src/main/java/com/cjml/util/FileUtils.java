package com.cjml.util;


import com.cjml.ocr.constant.CommonConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2021-11-10
 */
public class FileUtils {

    public static List<String> commonList;

    static {
        commonList = new ArrayList<>();
        commonList.add(CommonConstants.CACHE_CACH);
        commonList.add(CommonConstants.CROP_IMG);
        commonList.add(CommonConstants.FILE_STATE_TXT);
        commonList.add(CommonConstants.LABEL_TXT);
        commonList.add(CommonConstants.REC_GT_TXT);
        commonList.add(CommonConstants.TXT);
    }

    /**
     * 获取文件下的所有文件名
     *
     * @param fileDir            文件目录
     * @return 所有文件名集合
     */
    public static List<String> gainAllFileName(String fileDir) {
        // 列出目录的所有文件名
        File file = new File(fileDir);
        return Arrays.stream(Objects.requireNonNull(file.list())).collect(Collectors.toList());
    }


    /**
     * 获取两个文件名列表的交集, 排除公共文件
     *
     * @param oneList 一号文件名集合
     * @param twoList 二号文件名集合
     * @return 排除公共文件后的文件名交集
     */
    public static List<String> gainIntersection(List<String> oneList, List<String> twoList) {

        List<String> interSectionList = oneList.parallelStream()
                .filter(twoList::contains).collect(Collectors.toList());

        // 交集排除公共文件
        return interSectionList.stream()
                .filter(i -> commonList.stream().noneMatch(i::contains)).collect(Collectors.toList());
    }

    /**
     * 批量删除文件
     *
     * @param fileNameList 待删除文件名集合
     * @param filePath     待删除文件的目录名
     */
    public static void removeFiles(List<String> fileNameList, String filePath) {
        fileNameList.forEach(i -> {
            File file = new File(filePath + "/" + i);
            file.delete();
        });
    }

    /**
     * 在旧标签文件下创建新文件, 并将没有重复信息的标签信息写入文本文件
     *
     * @param oldLabelFile      可能存在交集信息的旧标签文件
     * @param intersectionList 交集信息集合
     */
    public static void writeNewFileLabel(String oldLabelFile, List<String> intersectionList) {

        // 获取文件目录
        File detLabelFile = new File(oldLabelFile);
        String detLabelParentDir = detLabelFile.getParent();

        // 获取文件名
        String detFileName = detLabelFile.getName();

        // 读取旧标签文件内容
        Stream<String> lines = gainFileContent(oldLabelFile);

        // 创建新文件, 并写入信息
        File newFile = new File(detLabelParentDir + "/new" + detFileName);
        assert lines != null;
        lines = lines.filter(l -> intersectionList.stream().noneMatch(l::contains));
        writeLinesToNewFile(newFile, lines);
    }

    /**
     * 将文件内容以流的形式读出
     *
     * @param filepath 文件路径
     * @return 读出的文件信息
     */
    public static Stream<String> gainFileContent(String filepath) {
        try {
            return Files.lines(Paths.get(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将流中的信息写入文件
     *
     * @param newFile 待写入的文件
     * @param lines 流中的行信息
     */
    public static void writeLinesToNewFile(File newFile, Stream<String> lines) {
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
}
