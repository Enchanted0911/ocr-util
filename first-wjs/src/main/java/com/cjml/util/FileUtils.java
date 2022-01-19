package com.cjml.util;


import com.cjml.clas.constant.CharConstant;
import com.cjml.ocr.constant.CommonConstants;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * @param fileDir 文件目录
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
     * @param oldLabelFile     可能存在交集信息的旧标签文件
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
     * @param lines   流中的行信息
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

    /**
     * 递归获取文件目录下的所有文件
     *
     * @param fileDir  文件目录
     * @param fileList 该文件目录下的所有文件, 包含子文件
     */
    public static void gainAllFile(File fileDir, List<File> fileList) {
        File[] fs = fileDir.listFiles();
        assert fs != null;
        for (File f : fs) {
            if (f.isDirectory()) {
                gainAllFile(f, fileList);
            }
            if (f.isFile()) {
                fileList.add(f);
            }
        }
    }

    /**
     * 将数据分成训练集和评估集
     *
     * @param originFileDir 原始数据目录
     * @param desFileDir 目的数据目录
     */
    public static void separateFile(String originFileDir, String desFileDir) {
        File originFile = new File(originFileDir);
        File[] fs = originFile.listFiles();
        for (int i = 0; i < Objects.requireNonNull(fs).length; i++) {
            Path path = Paths.get(desFileDir + CharConstant.SLASH + fs[i].getName());
            Path pathCreate = null;
            try {
                pathCreate = Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            File[] fileList = fs[i].listFiles();
            for (int j = 0; j < Objects.requireNonNull(fileList).length; j++) {
                if (j % 6 == 0) {
                    File toFile = new File(pathCreate + CharConstant.SLASH + fileList[j].getName());
                    fileList[j].renameTo(toFile);
                }
            }
        }
    }

    /**
     * 去除标签文件中的带有中文的标注记录
     *
     * @param labelPath 标签文件路径
     */
    public static void filterChinese(String labelPath) {
        Stream<String> fileContent = gainFileContent(labelPath);
        assert fileContent != null;
        Stream<String> filterList = fileContent.filter(s -> {
            Pattern p = Pattern.compile(com.cjml.clas.constant.CommonConstants.REGEX_CHINESE);
            Matcher m = p.matcher(s);
            return !m.find();
        });

        File labelFile = new File(labelPath);
        File newFile = new File(labelFile.getParent() + "/new_" + labelFile.getName());
        writeLinesToNewFile(newFile, filterList);
    }

    /**
     * 通过字符串形式的文件路径, 获取带有后缀的完整文件名
     *
     * @param filePath 文件路径
     * @return 带有文件类型后缀的文件名
     */
    public static String gainFullFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 通过字符串形式的文件路径, 获取不带有后缀的完整文件名
     *
     * @param filePath 文件路径
     * @return 不带有文件类型后缀的文件名
     */
    public static String gainJustFileName(String filePath) {
        String fullFileName = gainFullFileName(filePath);
        return fullFileName.substring(0, fullFileName.lastIndexOf(com.cjml.constant.CommonConstants.DOT));
    }

    /**
     * 将xml文件中的两个特定标签修改为特定值
     * 针对detection数据标签的清洗
     *
     * @param labelPath 标签路径
     * @param editLabelOne 待修改标签一
     * @param editLabelTwo 待修改标签二
     * @param newLabel 修改后标签
     */
    public static void processXmlFile(String labelPath, String editLabelOne, String editLabelTwo, String newLabel) {
        List<String> list = FileUtils.gainAllFileName(labelPath);

        list.forEach(x -> {
            Stream<String> content = FileUtils.gainFileContent(labelPath  + com.cjml.constant.CommonConstants.SLASH + x);
            List<String> fixedContent = new ArrayList<>();
            assert content != null;
            content.forEach(i -> {
                if (i.contains(editLabelOne) || i.contains(editLabelTwo)) {
                    i = i.substring(0, i.indexOf('>') + 1) + newLabel + i.substring(i.lastIndexOf('<'));
                }
                fixedContent.add(i);
            });
            File newFile = new File(labelPath + com.cjml.constant.CommonConstants.SLASH + x);
            FileUtils.writeLinesToNewFile(newFile, fixedContent.stream());
        });
    }
}
