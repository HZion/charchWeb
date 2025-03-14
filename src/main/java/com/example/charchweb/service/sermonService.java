package com.example.charchweb.service;


import com.example.charchweb.repo.sermonRepository;
import com.example.charchweb.DTO.Sermon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
/*
    @param year 필터링할 연도 (옵션)
    @param preacher 필터링할 설교자 (옵션)
    @param pageable 페이지네이션 정보
    @return 필터링된 설교 목록 (페이지 형태)
 */
    public Page<Sermon> filterSermons(String year, String preacher, Pageable pageable) {
    // 둘 다 비어있는 경우 모든 설교 반환
    if ((year == null || year.isEmpty()) && (preacher == null || preacher.isEmpty())) {
        return getAllSermons(pageable);
    }

    // 연도만 필터링
    if (year != null && !year.isEmpty() && (preacher == null || preacher.isEmpty())) {
        int yearValue = Integer.parseInt(year);
        return sermonRepository.findByDateBetween(
                java.time.LocalDate.of(yearValue, 1, 1),
                java.time.LocalDate.of(yearValue, 12, 31),
                pageable
        );
    }

    // 설교자만 필터링
    if ((year == null || year.isEmpty()) && preacher != null && !preacher.isEmpty()) {
        return sermonRepository.findByPreacher(preacher, pageable);
    }

    // 두 조건 모두 필터링
    int yearValue = Integer.parseInt(year);
    return sermonRepository.findByPreacherAndDateBetween(
            preacher,
            java.time.LocalDate.of(yearValue, 1, 1),
            java.time.LocalDate.of(yearValue, 12, 31),
            pageable
    );
}
    public List<String> getAllPreachers() {
        try {
            // JPA 쿼리로 직접 가져오기
            return sermonRepository.findDistinctPreachers();
        } catch (Exception e) {
            // 에러 발생 시 대체 구현으로 모든 설교를 가져와서 메모리에서 처리
            List<Sermon> allSermons = sermonRepository.findAll();
            return allSermons.stream()
                    .map(Sermon::getPreacher)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }
    // 이전 설교 가져오기
    public Optional<Sermon> getPreviousSermon(Long id) {
        return sermonRepository.findFirstByIdLessThanOrderByIdDesc(id);
    }

    // 다음 설교 가져오기
    public Optional<Sermon> getNextSermon(Long id) {
        return sermonRepository.findFirstByIdGreaterThanOrderByIdAsc(id);
    }

    // 관련 설교 가져오기 (같은 설교자 또는 유사한 성경 구절)
    public List<Sermon> getRelatedSermons(Sermon sermon, int count) {
        Pageable pageable = PageRequest.of(0, count);
        // 같은 설교자의 다른 설교 또는 비슷한 성경 구절의 설교
        return sermonRepository.findByPreacherAndIdNot(sermon.getPreacher(), sermon.getId(), pageable);
    }
    }
