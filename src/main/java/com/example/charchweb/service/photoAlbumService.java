package com.example.charchweb.service;


import com.example.charchweb.DTO.photoAlbum;
import com.example.charchweb.repo.photoAlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.charchweb.DTO.photo;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class photoAlbumService {

    @Autowired
    private photoAlbumRepository photoAlbumRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // 앨범 생성
    @Transactional
    public photoAlbum createAlbum(photoAlbum album, MultipartFile thumbnailFile) throws IOException {
        // 썸네일 업로드 (있는 경우)
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            Map uploadResult = cloudinaryService.uploadImage(thumbnailFile, "church/albums/thumbnails");
            album.setThumbnailUrl((String) uploadResult.get("url"));
            album.setThumbnailPublicId((String) uploadResult.get("public_id"));
        }

        return photoAlbumRepository.save(album);
    }

    // 앨범 수정
    @Transactional
    public photoAlbum updateAlbum(Long id, photoAlbum updatedAlbum, MultipartFile thumbnailFile, boolean removeThumbnail) throws IOException {
        Optional<photoAlbum> albumOpt = photoAlbumRepository.findById(id);
        if (albumOpt.isEmpty()) {
            throw new RuntimeException("Album not found with id: " + id);
        }

        photoAlbum album = albumOpt.get();
        album.setTitle(updatedAlbum.getTitle());
        album.setDescription(updatedAlbum.getDescription());
        album.setEventDate(updatedAlbum.getEventDate());
        album.setCategory(updatedAlbum.getCategory());

        // 썸네일 처리
        if (removeThumbnail) {
            // 기존 썸네일 삭제
            if (album.getThumbnailPublicId() != null) {
                cloudinaryService.deleteImage(album.getThumbnailPublicId());
                album.setThumbnailUrl(null);
                album.setThumbnailPublicId(null);
            }
        } else if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // 새 썸네일 업로드
            // 기존 썸네일이 있으면 삭제
            if (album.getThumbnailPublicId() != null) {
                cloudinaryService.deleteImage(album.getThumbnailPublicId());
            }

            Map uploadResult = cloudinaryService.uploadImage(thumbnailFile, "church/albums/thumbnails");
            album.setThumbnailUrl((String) uploadResult.get("url"));
            album.setThumbnailPublicId((String) uploadResult.get("public_id"));
        }

        return photoAlbumRepository.save(album);
    }

    // 앨범 삭제
    @Transactional
    public void deleteAlbum(Long id) throws IOException {
        Optional<photoAlbum> albumOpt = photoAlbumRepository.findById(id);
        if (albumOpt.isEmpty()) {
            throw new RuntimeException("Album not found with id: " + id);
        }

        photoAlbum album = albumOpt.get();

        // 앨범에 속한 모든 사진 Cloudinary에서 삭제
        for (photo photo : album.getPhotos()) {
            cloudinaryService.deleteImage(photo.getPublicId());
        }

        // 썸네일 이미지 Cloudinary에서 삭제
        if (album.getThumbnailPublicId() != null) {
            cloudinaryService.deleteImage(album.getThumbnailPublicId());
        }

        // 앨범 삭제 (cascade로 연결된 사진들도 함께 삭제됨)
        photoAlbumRepository.delete(album);
    }

    // 앨범 조회
    @Transactional(readOnly = true)
    public Optional<photoAlbum> getAlbumById(Long id) {
        return photoAlbumRepository.findById(id);
    }

    // 앨범 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<photoAlbum> getAllAlbums(Pageable pageable) {
        return photoAlbumRepository.findAll(pageable);
    }

    // 키워드로 앨범 검색
    @Transactional(readOnly = true)
    public Page<photoAlbum> searchAlbumsByKeyword(String keyword, Pageable pageable) {
        return photoAlbumRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    }

    // 연도별 앨범 조회
    @Transactional(readOnly = true)
    public Page<photoAlbum> getAlbumsByYear(int year, Pageable pageable) {
        return photoAlbumRepository.findByEventDateYear(year, pageable);
    }

    // 카테고리별 앨범 조회
    @Transactional(readOnly = true)
    public Page<photoAlbum> getAlbumsByCategory(String category, Pageable pageable) {
        return photoAlbumRepository.findByCategory(category, pageable);
    }

    // 연도 및 카테고리별 앨범 조회
    @Transactional(readOnly = true)
    public Page<photoAlbum> getAlbumsByYearAndCategory(int year, String category, Pageable pageable) {
        return photoAlbumRepository.findByEventDateYearAndCategory(year, category, pageable);
    }

    // 전체 연도 목록 조회
    @Transactional(readOnly = true)
    public List<Integer> getAllYears() {
        return photoAlbumRepository.findDistinctYears();
    }

    // 전체 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        return photoAlbumRepository.findDistinctCategories();
    }

    // 다음 앨범 조회
    @Transactional(readOnly = true)
    public photoAlbum getNextAlbum(LocalDate currentDate, Long currentId) {
        // 1. 같은 날짜의 다음 ID 앨범을 찾음
        Optional<photoAlbum> sameDay = photoAlbumRepository
                .findFirstByEventDateEqualsAndIdGreaterThanOrderByIdAsc(currentDate, currentId);

        if (sameDay.isPresent()) {
            return sameDay.get();
        }

        // 2. 같은 날짜에 없으면 다음 날짜의 가장 작은 ID를 찾음
        return photoAlbumRepository
                .findFirstByEventDateAfterAndIdGreaterThanOrderByEventDateAscIdAsc(currentDate, currentId);    }

    // 이전 앨범 조회
    @Transactional(readOnly = true)
    public photoAlbum getPreviousAlbum(LocalDate currentDate, Long currentId) {
        Optional<photoAlbum> sameDay = photoAlbumRepository.findFirstByEventDateEqualsAndIdLessThanOrderByIdDesc(currentDate,currentId);
        if(sameDay.isPresent()) {
            return sameDay.get();
        }

        return photoAlbumRepository
                .findFirstByEventDateBeforeAndIdLessThanOrderByEventDateDescIdDesc(currentDate ,currentId);
    }

    @Transactional(readOnly = true)
    public Page<photoAlbum> getAlbumsByYearAndMonth(int year, int month, Pageable pageable) {
        // 해당 연도와 월의 시작일과 종료일 계산
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return photoAlbumRepository.findByEventDateBetween(startDate, endDate, pageable);
    }

}