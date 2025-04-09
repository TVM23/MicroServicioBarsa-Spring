package com.access.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public List<String> uploadBase64Images(List<String> base64Images) {
        List<String> urls = new ArrayList<>();

        for (String base64 : base64Images) {
            try {
                Map uploadResult = cloudinary.uploader().upload("data:image/jpeg;base64," + base64,
                        ObjectUtils.asMap("folder", "materias/"));
                urls.add(uploadResult.get("secure_url").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return urls;
    }
}
