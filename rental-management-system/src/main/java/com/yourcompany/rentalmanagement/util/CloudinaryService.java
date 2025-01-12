package com.yourcompany.rentalmanagement.util;
/**
 * @author FTech
 */
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloudinaryService {
    private Dotenv dotenv;
    private Cloudinary cloudinary;

    public CloudinaryService() {
        dotenv = Dotenv.load();
        cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }

    public String uploadImage(File file) throws IOException {
        Map<String, Object> uploadOptions = new HashMap<>();
        uploadOptions.put("transformation", new Transformation()
                .quality("auto")
                .fetchFormat("auto"));

        Map uploadResult = cloudinary.uploader().upload(file, uploadOptions);
        System.out.println(uploadResult);
        return (String) uploadResult.get("secure_url");
    }

    public void deleteImage(String secureUrl) throws IOException {
        try {
            String publicId = extractPublicId(secureUrl);
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String deletionResult = (String) result.get("result");

            if (!"ok".equals(deletionResult)) {
                // More detailed error handling
                if (result.containsKey("error")) {
                    Map error = (Map) result.get("error");
                    String message = (String) error.get("message");
                    throw new IOException("Cloudinary deletion failed: " + message);
                } else {
                    throw new IOException("Cloudinary deletion failed: Unknown error");
                }
            } else {
                System.out.println("Image with public_id " + secureUrl + " deleted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractPublicId(String secureUrl) {
        if (secureUrl == null || secureUrl.isEmpty()) {
            return null;
        }
        String regex = "(?:/upload/)(?:v\\d+/)?(?:.*?/)?([^/]+)\\.(?:.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(secureUrl);

        if (matcher.find()) {
            return matcher.group(1); // Group 1 captures the public_id
        } else {
            return null; // Or throw an IllegalArgumentException
        }
    }
}
