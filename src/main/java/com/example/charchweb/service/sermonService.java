package com.example.charchweb.service;


import com.example.charchweb.repo.sermonRepository;
import com.example.charchweb.DTO.Sermon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class sermonService {

    @Autowired
    private sermonRepository sermonRepository;

    @Value("${upload.path}")
    private String uploadPath;

    // 최신 설교 한 개 가져오기
    public Optional<Sermon> getLatestSermon() {
        return sermonRepository.findTopByOrderByDateDesc();
    }

    // 최근 설교 리스트 가져오기 (최신 설교 제외)
    public List<Sermon> getRecentSermons(int count) {
        Pageable pageable = Pageable.ofSize(count);
        Optional<Sermon> latest = getLatestSermon();

        if (latest.isPresent()) {
            return sermonRepository.findByIdNotOrderByDateDesc(latest.get().getId(), pageable);
        } else {
            return sermonRepository.findAllByOrderByDateDesc(pageable).getContent();
        }
    }

    // 모든 설교 페이징하여 가져오기
    public Page<Sermon> getAllSermons(Pageable pageable) {
        return sermonRepository.findAll(pageable);
    }

    // 설교 검색
    public Page<Sermon> searchSermons(String keyword, Pageable pageable) {
        return sermonRepository.findByTitleContainingOrPreacherContainingOrBibleVerseContainingOrContentContaining(
                keyword, keyword, keyword, keyword, pageable);
    }

    // ID로 설교 가져오기
    public Optional<Sermon> getSermonById(Long id) {
        return sermonRepository.findById(id);
    }

    // 설교 저장 (추가/수정)
    public Sermon saveSermon(Sermon sermon) {
        return sermonRepository.save(sermon);
    }

    // 설교 삭제
    public void deleteSermon(Long id) {
        sermonRepository.deleteById(id);
    }

    // 썸네일 이미지 저장
    public String saveThumbnail(MultipartFile file) {
        try {
            // 업로드 디렉토리 확인 및 생성
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 고유한 파일명 생성
            String uuid = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = uuid + extension;

            // 파일 저장
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // 파일 URL 반환
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다: " + e.getMessage());
        }
    }
}