package com.yourcompany.rentalmanagement.util;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryService {
    private Dotenv dotenv;
    private Cloudinary cloudinary;

    public CloudinaryService() {
        dotenv = Dotenv.load();
        cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }

    public String uploadImage(File file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        System.out.println(uploadResult);
        return (String) uploadResult.get("secure_url");
    }

//    public String reloadImage
}
