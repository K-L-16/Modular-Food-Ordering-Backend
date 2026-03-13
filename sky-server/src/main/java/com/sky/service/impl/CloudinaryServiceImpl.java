package com.sky.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sky.service.CloudinaryService;
import com.sky.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    //获取cloundinary对象
    @Autowired
    private Cloudinary cloudinary;

    @Override
    public String upload(MultipartFile file) throws IOException {

        // 目录：JavaTails/2026/02
        String folder = "SkyTakeOut/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

        // 扩展名：.png
        String original = file.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf(".")).toLowerCase();

        // uuid 文件名：不带扩展名
        String uuid = UUID.randomUUID().toString();

        Map result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", uuid + ext,
                        "resource_type", "auto",
                        "overwrite", false
                )
        );

        return result.get("secure_url").toString();
    }
}
