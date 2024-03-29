package com.cjml.clas;

import com.cjml.clas.constant.CharConstant;
import com.cjml.clas.constant.CommonConstants;
import com.cjml.util.FileUtils;
import com.cjml.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author johnson
 * @date 2021-12-12
 */
public class PreProcess {

    public static String trainClassDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.TRAIN_CLASS_DIR);
    public static String evalClassDir = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.EVAL_CLASS_DIR);
    public static String trainFilterChineseLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.FILTER_TRAIN_CHINESE_PATH);
    public static String evalFilterChineseLabelPath = ResourceUtils.gainValueByKey(CommonConstants.FILE_DIR_PROPERTIES, CommonConstants.FILTER_EVAL_CHINESE_PATH);


    public static void main(String[] args) {

        String dir = "C:\\Users\\wujs.YANGCHE\\Desktop\\temp_sort";

        // 分割训练集和评估集
//        FileUtils.separateFile(trainClassDir, evalClassDir);
//
//        // 生成标注文件
        dataSetProcess(trainClassDir);
        dataSetProcess(evalClassDir);
//        dataSetProcess(dir);

//         去除中文标签
        FileUtils.filterChinese(trainFilterChineseLabelPath);
        FileUtils.filterChinese(evalFilterChineseLabelPath);
//        FileUtils.filterChinese(dir + "\\Label.txt");
    }

    /**
     * 完整的一次文件处理成文件标签
     *
     * @param classDir 待处理的文件目录
     */
    public static void dataSetProcess(String classDir) {
        File file = new File(classDir);
        List<File> fileList = new ArrayList<>();
        List<String> fileLabelList = new ArrayList<>();

        FileUtils.gainAllFile(file, fileList);
        processFilename(fileList, fileLabelList);

        File newFile = new File(classDir + CharConstant.SLASH + "Label.txt");
        FileUtils.writeLinesToNewFile(newFile, fileLabelList.stream());
    }



    /**
     * 以上级文件夹名作为文件标签, 处理文件为文件标签
     *
     * @param fileList 待处理的文件
     * @param fileLabelList 处理后的文件标签
     */
    public static void processFilename(List<File> fileList, List<String> fileLabelList) {
        fileList.forEach(f -> {
            String fileClass = f.getParent().substring(f.getParent().lastIndexOf(File.separator) + 1);
            String fileLabel = fileClass + CharConstant.SLASH + f.getName() + CharConstant.SPACE_CHAR + fileClass;
            if (!f.getName().equals(CommonConstants.THUMBS)) {
                fileLabelList.add(fileLabel);
            }
        });
    }
}
