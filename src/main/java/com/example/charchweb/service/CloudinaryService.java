package com.example.charchweb.service;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Cloudinary에 이미지 업로드
     * @param file 업로드할 이미지 파일
     * @param folder 저장할 폴더 (예: "church/albums")
     * @return 업로드 결과 Map (url, public_id 등 포함)
     * @throws IOException 업로드 실패 시
     */
    public Map uploadImage(MultipartFile file, String folder) throws IOException {
        return cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "public_id", UUID.randomUUID().toString(),
                        "resource_type", "auto"
                )
        );
    }

    /**
     * Cloudinary에서 이미지 삭제
     * @param publicId 삭제할 이미지의 public_id
     * @return 삭제 결과 Map
     * @throws IOException 삭제 실패 시
     */
    public Map deleteImage(String publicId) throws IOException {
        return cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.emptyMap()
        );
    }
}