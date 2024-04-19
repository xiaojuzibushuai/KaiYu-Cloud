package com.kaiyu.learning.videoprocessor;

import com.kaiyu.learning.util.CustomVideoUtil;
import com.ruoyi.common.core.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: kai-yu-cloud
 * @description:
 * @author: xiaojuzi
 * @create: 2024-04-07 16:44
 **/
public class BaseVideoQualityStrategy {

    private static final Logger log = LoggerFactory.getLogger(BaseVideoQualityStrategy.class);

    public List<String> getCommand(String ffmpeg_path,String video_path,String keyinfo,String m3u8folder_path) {
        List<String> command = new ArrayList<String>();
        command.add(ffmpeg_path);
        command.add("-i");
        command.add(video_path);
        command.add("-c:v");
        command.add("libx264");
        command.add("-c:a");
        command.add("aac");
        command.add("-s");
        command.add("640x360");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("128k");
        command.add("-b:v");
        command.add("800k");
        command.add("-r");
        command.add("30");
        command.add("-hls_key_info_file");
        command.add(keyinfo);
        command.add("-f");
        command.add("hls");
        command.add("-hls_time");
        command.add("5");
        command.add("-hls_list_size");
        command.add("0");
        command.add("-hls_playlist_type");
        command.add("vod");
        command.add("-force_key_frames");
        command.add("expr:gte(t,n_forced*1)");
        command.add("-hls_segment_filename");
        command.add((m3u8folder_path + "/encrypt_slice_%05d.ts").replace("\\", "/"));
        command.add("-hls_flags");
        command.add("append_list");
        command.add((m3u8folder_path + "/encrypted_slice.m3u8").replace("\\", "/"));
        return command;
    }


    public String getBaseProcessVideoQuality(String ffmpegPath, String sourceVideoPath, String targetVideoPath, String DpiPath, ConcurrentHashMap<String,String> commandMap){
        //生成密钥
        CustomVideoUtil customVideoUtil = new CustomVideoUtil();
        String keyinfo = customVideoUtil.initHLSKey(targetVideoPath, DpiPath);
        if (keyinfo == null) {
            return null;
        }
//        System.out.println(keyinfo);

        File targetVideoDir = new File(targetVideoPath).getParentFile();
        if (!targetVideoDir.exists()) {
            targetVideoDir.mkdirs();
        }

        // 获取命令
        List<String> command = getCommand(ffmpegPath,sourceVideoPath,keyinfo,targetVideoDir.getAbsolutePath());
        //覆盖相关参数
        if(commandMap.size() > 0){
            String remove = command.remove(command.size() - 1);
            for (Map.Entry<String, String> entry : commandMap.entrySet()) {
                Boolean flag = true;
                for (int i = 1; i < command.size()-1; i+=2) {
                    if ((command.get(i).trim()).equalsIgnoreCase(entry.getKey().trim())) {
                        if(StringUtils.isEmpty(entry.getValue())){
                            command.remove(i);
                            //删除值
                            command.remove(i);
                        }else {
                            command.set(i + 1, entry.getValue());
                        }
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    command.add(entry.getKey());
                    command.add(entry.getValue());
                }
            }
            command.add(remove);

        }
//        System.out.println(command);

        //切片加密生成对应m3u8等文件
        Process p = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            //将标准输入流和错误输入流合并，通过标准输入流程读取信息
            builder.redirectErrorStream(true);
            p = builder.start();
            String s = new CustomVideoUtil().waitFor(p);
//            System.out.println(s);

        }catch (Exception e){
            log.error("将原视频{}转换为{}时失败:{}",sourceVideoPath,DpiPath,e.getMessage());
            return null;
        }finally {
            if (p != null) {
                p.destroy();
            }
        }

        //检查M3U8列表
        List<String> tsList = new CustomVideoUtil().getTsList((targetVideoDir+"/encrypted_slice.m3u8").replace("\\","/"));
        if(tsList.size() == 0) {
            log.info("将原视频{}转换为{}时失败:{}",sourceVideoPath,DpiPath,"M3U8列表为空");
            return null;
        }

        return tsList.toString();

    }

}
