package controller;

import entity.FileSystem;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject:controller
 * @Author: JinHua
 * @CreateTime:2022/5/16
 * @Description:
 */
@Controller
public class FileAction {

    @Autowired
    private Redisson redisson;

    @Bean
    public Redisson redisson(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.200.150:6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }

    private static final String lock = "LOCK";

    @RequestMapping("upload")
    //MultipartHttpServletRequest：是httpservletRequest的强化版本，不仅可以装文本信息，还可以装图片文件信息
    public @ResponseBody List<FileSystem> upload(MultipartHttpServletRequest request) throws Exception {
        List<FileSystem> fileSystems = new ArrayList<>();
        List<MultipartFile> videoList = request.getFiles("videoList");
        if(videoList.size()>0){
            RLock rLock = redisson.getLock(FileAction.lock);
            rLock.lock(30, TimeUnit.SECONDS);
            try {
                for (MultipartFile multipartFile : videoList) {
                    fileSystems.add(uploadFileToDFS(multipartFile));
                }
                System.out.println(fileSystems);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rLock.unlock();
            }
        }
        return fileSystems;
    }


    public FileSystem uploadFileToDFS(MultipartFile file) throws Exception {
        FileSystem fileSystem = new FileSystem();
        /* 1、把文件保存到web服务器*/
        // 从页面请求中，获取上传的文件对象
        String oldFileName = file.getOriginalFilename();
        String hz = oldFileName.substring(oldFileName.lastIndexOf(".") + 1);
        String newFileName = UUID.randomUUID().toString()+"."+hz;

        File toSaveFile = new File("D:/upload/" + newFileName);
        file.transferTo(toSaveFile);
        String newFilePath = toSaveFile.getAbsolutePath();
        /* 2、把文件从web服务器上传到FastDFS*/
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        StorageServer storageServer = null;
        StorageClient1 client = new StorageClient1(trackerServer, null);
        NameValuePair[] list = new NameValuePair[1];
        list[0] = new NameValuePair("fileName",oldFileName);
        String fileId = client.upload_appender_file1(newFilePath, hz, list);
        trackerServer.close();

        fileSystem.setFileId(fileId);
        fileSystem.setFileName(oldFileName);
        fileSystem.setFilePath(fileId);
        //System.out.println(oldFileName);
        return fileSystem;

    }
}
