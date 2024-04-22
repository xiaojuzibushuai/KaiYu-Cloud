package com.kaiyu.media.util;

import com.kaiyu.media.domain.VideoDPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @program: kai-yu-cloud
 * @description: 字定义视频工具类
 * @author: xiaojuzi
 * @create: 2024-04-07 17:07
 **/
public class CustomVideoUtil{

    private static final Logger log = LoggerFactory.getLogger(CustomVideoUtil.class);


    public String getVideoDpiPath(int dpi) {
        VideoDPI videoDPI = VideoDPI.getByDpiValue(dpi);
        if (videoDPI != null) {
            return videoDPI.getPath();
        } else {
            return null;
        }
    }


    public String initHLSKey(String output_path, String dpi_path) {

        String[] pathParts = output_path.replace("\\", "/").split("/");
        String fileMd5 = pathParts[pathParts.length - 2];
        String baseKeyPath = new File(output_path).getParentFile().getParentFile().getParentFile().getAbsolutePath();
        String keyPath = (baseKeyPath + "/" + fileMd5 + "/" + dpi_path).replace("\\", "/");

        String keyInfoFilepath = (keyPath + "/keyinfo.txt").replace("\\", "/");

        File keyPathDir = new File(keyPath);
        if (!keyPathDir.exists()) {
            keyPathDir.mkdirs();
        }

        String keyFilepath = (keyPath + "/encrypt.key").replace("\\", "/");

        Process p1 = null;
        Process p2 = null;
        try {
            //生成密钥文件
            ProcessBuilder keyFileProcessBuilder = new ProcessBuilder();
            keyFileProcessBuilder.command("openssl","rand", "16");
            //将标准输入流和错误输入流合并，通过标准输入流程读取信息
            keyFileProcessBuilder.redirectErrorStream(true);
            p1 = keyFileProcessBuilder.start();
            // 读取命令输出
            try (InputStream inputStream = p1.getInputStream();
                 OutputStream outputStream = new FileOutputStream(keyFilepath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            p1.waitFor(10, TimeUnit.SECONDS);


            //生成iv
            ProcessBuilder ivProcessBuilder = new ProcessBuilder("openssl","rand","-hex", "16");
            //将标准输入流和错误输入流合并，通过标准输入流程读取信息
            ivProcessBuilder.redirectErrorStream(true);
            p2 = ivProcessBuilder.start();

            //写入本地
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(keyInfoFilepath))) {
                String iv = reader.readLine().trim();
                writer.write(keyFilepath + "\n");
                writer.write(keyFilepath + "\n");
                writer.write(iv + "\n");
            }

            p2.waitFor(10, TimeUnit.SECONDS);

            return keyInfoFilepath;

        } catch (IOException e) {
            log.error("读取IV或写入本地文件时发生异常:", e);
            return null;
        } catch (InterruptedException e) {
            log.error("子进程退出异常:",e);
            return null;
        } finally {
            if (p1 != null) {
                p1.destroy();
            }
            if (p2 != null) {
                p2.destroy();
            }
        }
    }

    public List<String> getTsList(String outputFilePath) {
        List<String> tsList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(outputFilePath))) {
            String line;
            String lastLine = "";

            while ((line = reader.readLine()) != null) {
                lastLine = line.trim();
                if (lastLine.endsWith(".ts")) {
                    tsList.add(lastLine);
                } else if (lastLine.equals("#EXT-X-ENDLIST")) {
                    return tsList;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return null;
    }

    public String waitFor(Process p) {
        InputStream in = null;
        InputStream error = null;
        String result = "error";
        int exitValue = -1;
        StringBuffer outputString = new StringBuffer();
        try {
            in = p.getInputStream();
            error = p.getErrorStream();
            boolean finished = false;
            int maxRetry = 3600;//每次休眠1秒，最长执行时间60分种
            int retry = 0;
            while (!finished) {
                if (retry > maxRetry) {
                    return "error";
                }
                try {
                    while (in.available() > 0) {
                        Character c = new Character((char) in.read());
                        outputString.append(c);
                        System.out.print(c);
                    }
                    while (error.available() > 0) {
                        Character c = new Character((char) in.read());
                        outputString.append(c);
                        System.out.print(c);
                    }
                    //进程未结束时调用exitValue将抛出异常
                    exitValue = p.exitValue();
                    finished = true;

                } catch (IllegalThreadStateException e) {
                    Thread.currentThread().sleep(1000);//休眠1秒
                    retry++;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return outputString.toString();

    }



}
