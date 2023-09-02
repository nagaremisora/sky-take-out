package com.sky.controller.admin;

import com.sky.constant.OtherConstant;
import com.sky.result.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
public class CommonController {
    public static final String UPLOAD_DIR = "D:/BaiduNetdiskDownload/nginx-1.20.2/html/sky/img/upload";

    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestBody MultipartFile file, HttpServletRequest request) {
        if(file.isEmpty()){
            return Result.error("请选择要上传的文件");
        }
        String originalFilename = file.getOriginalFilename();
        String filename = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
        try {
            File newFile = new File(UPLOAD_DIR, filename);
            if(!newFile.isDirectory()){
                newFile.mkdir();
            }
            file.transferTo(newFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success( OtherConstant.UPLOAD_URI + filename);
    }
}
