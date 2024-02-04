//package com.imooc.controller;
//
//import com.imooc.MinIOConfig;
//import com.imooc.result.GraceJSONResult;
//import com.imooc.utils.MinIOUtils;
//import com.imooc.utils.SMSUtils;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@Slf4j
//// log4j的注释
//@Api(tags = "文件上传 测试接口")
//public class FileController {
//    @Autowired
//    private MinIOConfig minIOConfig ;
//
//    @PostMapping("upload")
//    public Object upload(MultipartFile file) throws Exception{
//        String filename = file.getOriginalFilename();
//
//        MinIOUtils.uploadFile(minIOConfig.getBucketName(),filename,file.getInputStream());
//
//        String imgUrl = minIOConfig.getFileHost()
//                        + "/"
//                        + minIOConfig.getBucketName()
//                        + "/" + filename;
//
//
//        return GraceJSONResult.ok(imgUrl) ;
//    }
//
//}
