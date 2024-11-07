package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;



@RestController
@Tag(name = "通用接口")
@Slf4j
@RequestMapping("/admin/common")
public class CommonController {

    @Resource
    private AliOssUtil aliOssUtil;
    /*
        文件上传
        @param file
        @return
     */
    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        try{
            String originalFilename = file.getOriginalFilename();
            // eg. dfdfdf.png
            String extension=originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            // 构造新名称
            String objectName=UUID.randomUUID().toString()+extension;

            String filePath=aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAIL);
        }


    }
}
