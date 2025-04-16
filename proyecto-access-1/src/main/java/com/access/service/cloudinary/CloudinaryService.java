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
                String mimeType = detectImageMimeType(base64);
                String base64WithPrefix = "data:" + mimeType + ";base64," + base64;

                Map uploadResult = cloudinary.uploader().upload(
                        base64WithPrefix,
                        ObjectUtils.asMap("folder", "materias/")
                );
                urls.add(uploadResult.get("secure_url").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return urls;
    }

    private String detectImageMimeType(String base64) {
        if (base64.startsWith("/9j")) {
            return "image/jpeg";
        } else if (base64.startsWith("iVBOR")) {
            return "image/png";
        } else if (base64.startsWith("UklGR")) {
            return "image/webp";
        } else {
            return "image/jpeg"; // Fallback por defecto
        }
    }
    
    public void deleteImageCloudinary(String public_id) {
    	try {
    		@SuppressWarnings("unchecked")
			Map<String, Object> result = cloudinary.uploader().destroy(public_id, ObjectUtils.emptyMap());
    	} catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar imagen de Cloudinary");
        }
    }
}

