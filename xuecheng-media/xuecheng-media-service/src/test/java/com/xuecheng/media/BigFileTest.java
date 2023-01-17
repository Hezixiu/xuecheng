package com.xuecheng.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Linzkr
 * @description: TODO 大文件分块以及合并
 * @date 2023/1/17 10:22
 */
public class BigFileTest {
//  将一个文件分块
    @Test
    public void testChunk(){
        try {
//        C:\Users\Linzkr\Videos\Captures\千锋云学习平台1.mp4
//        这个是待分块的文件
            File file = new File("C:\\Users\\Linzkr\\Videos\\Captures\\千锋云学习平台2.mp4");
//        获取文件的后缀名
            String fileName = file.getName();
            String ext = fileName.substring(fileName.lastIndexOf("."));
//          创建读取流
            RandomAccessFile readFileStream = new RandomAccessFile(file, "r");

//        分块存储的路径
            File chunkPath = new File("C:\\Users\\Linzkr\\Videos\\chunk\\");
//        如果该文件夹不存在 就创建出该文件夹
            if (!chunkPath.exists()) {
                boolean mkdirs = chunkPath.mkdirs();
                System.out.println("是否创建出文件夹："+mkdirs);
            }
            System.out.println("存储路径已存在");
//        分块的大小
            int chunkSize = 1024 * 1024;
//        分块的数量
//        小技巧 将整数转为浮点数直接*1.0即可
            long chunkNumber = (long) Math.ceil(file.length() * 1.0 / chunkSize);
            byte[] bytes = new byte[1024];
            int len =-1;
            for (long i = 0; i < chunkNumber; i++) {
                String absolutePath = chunkPath.getAbsolutePath();
                File filePath= new File(absolutePath+"\\"+"chunk_"+ i+ext);
//                创建文件
                boolean newFile = filePath.createNewFile();
                if (newFile) {
                    RandomAccessFile writeFileStream = new RandomAccessFile(filePath, "rw");
                    while ((len = readFileStream.read(bytes)) != -1) {
                        writeFileStream.write(bytes, 0, len);
                        if (filePath.length() >= chunkSize) {
                            break;
                        }
                    }
                    writeFileStream.close();
                }
            }
            readFileStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    @Test
    public void testMerge() throws IOException {
//        合并后的文件
        File mergeFile = new File("C:\\Users\\Linzkr\\Videos\\chunk\\TEST.mp4");
        //分块存储的路径
        File chunkPath = new File("C:\\Users\\Linzkr\\Videos\\chunk\\");
//        获取所有分块的文件对象
        File[] listFiles = chunkPath.listFiles();
        if (listFiles==null){
            System.out.println("还没有分块文件");
            return;
        }
        List<File> files = Arrays.asList(listFiles);
        Collections.sort(files,new Comparator<File>(){

            @Override
            public int compare(File o1, File o2) {
                String o1Name = o1.getName();
                String o2Name = o2.getName();
                String _o1 = o1Name.substring(o1Name.lastIndexOf("_") + 1,o1Name.lastIndexOf("."));
                String _o2 = o2Name.substring(o2Name.lastIndexOf("_") + 1,o2Name.lastIndexOf("."));

                return Integer.parseInt(_o1)-Integer.parseInt(_o2);
            }
        });

        //          创建写入流
        RandomAccessFile writeFileStream = new RandomAccessFile(mergeFile, "rw");
        byte[] bytes = new byte[1024];
        for (File file : files) {
//            创建读取流
            RandomAccessFile readFileStream = new RandomAccessFile(file,"r");
            int len =-1;
            while ((len= readFileStream.read(bytes))!=-1){
                writeFileStream.write(bytes,0,len);
            }
        }
//        校验合并后的文件是否正确
        FileInputStream sourceFileStream = new FileInputStream("C:\\Users\\Linzkr\\Videos\\Captures\\千锋云学习平台2.mp4");
        FileInputStream mergeFileStream = new FileInputStream(mergeFile);
        String sourceMd5Hex = DigestUtils.md5Hex(sourceFileStream);
        String mergeMd5Hex = DigestUtils.md5Hex(mergeFileStream);
        if (sourceMd5Hex.equals(mergeMd5Hex)){
            System.out.println("合并成功");
        }

    }
    @Test
    public void test1(){
        //分块存储的路径
        File chunkPath = new File("C:\\Users\\Linzkr\\Videos\\chunk\\");
        File[] listFiles = chunkPath.listFiles();
        List<File> files = Arrays.asList(listFiles);
        Collections.sort(files,new Comparator<File>(){

            @Override
            public int compare(File o1, File o2) {
                String o1Name = o1.getName();
                String o2Name = o2.getName();
                String _o1 = o1Name.substring(o1Name.lastIndexOf("_") + 1,o1Name.lastIndexOf("."));
                String _o2 = o2Name.substring(o2Name.lastIndexOf("_") + 1,o2Name.lastIndexOf("."));

                return Integer.parseInt(_o1)-Integer.parseInt(_o2);
            }
        });
        files.forEach(file -> {
            System.out.println(file.getName());
        });

    }
}
