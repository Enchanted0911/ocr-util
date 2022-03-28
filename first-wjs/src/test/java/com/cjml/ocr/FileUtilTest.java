package com.cjml.ocr;

import com.cjml.util.FileUtils;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author johnson
 * @date 2022-03-24
 */
public class FileUtilTest {

    @Test
    public void test1() {
        List<String> tempList = new ArrayList<>();
        tempList.add("hello");
        tempList.add("world");
        tempList.add("java");
        Stream<String> lines = tempList.stream();

        FileUtils.writeLinesToNewFile(new File("D:\\test.txt"), lines);
    }

    @Test
    public void test2() {
        List<String> tempList = new ArrayList<>();
        tempList.add("hello");
        tempList.add("world");
        tempList.add("java");
        Stream<String> lines = tempList.stream();

        FileUtils.writeLinesToNewFile(new File("D:\\test1\\test.txt"), lines);
    }
}
