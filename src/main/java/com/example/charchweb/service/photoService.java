package com.example.charchweb.service;


import com.example.charchweb.DTO.photo;
import com.example.charchweb.DTO.photoAlbum;
import com.example.charchweb.repo.photoAlbumRepository;
import com.example.charchweb.repo.photoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class photoService {

    @Autowired
    private photoRepository photoRepository;

    @Autowired
    private photoAlbumRepository photoAlbumRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    // 사진 업로드
    @Transactional
    public List<photo> uploadPhotos(Long albumId, List<MultipartFile> files) throws IOException {
        Optional<photoAlbum> albumOpt = photoAlbumRepository.findById(albumId);
        if (albumOpt.isEmpty()) {
            throw new RuntimeException("Album not found with id: " + albumId);
        }

        photoAlbum album = albumOpt.get();
        List<photo> uploadedPhotos = new ArrayList<>();

        // 현재 앨범의 사진 수 조회 (표시 순서 계산용)
        long currentPhotoCount = photoRepository.countByAlbum(album);

        // 각 사진 업로드
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                Map uploadResult = cloudinaryService.uploadImage(file, "church/albums/photos");

                photo photo = new photo();
                photo.setImageUrl((String) uploadResult.get("url"));
                photo.setPublicId((String) uploadResult.get("public_id"));
                photo.setDisplayOrder((int) currentPhotoCount + i + 1);
                photo.setAlbum(album);

                album.getPhotos().add(photo);
                uploadedPhotos.add(photo);
            }
        }

        photoAlbumRepository.save(album);
        return uploadedPhotos;
    }

    // 사진 삭제
    @Transactional
    public void deletePhoto(Long photoId) throws IOException {
        Optional<photo> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isEmpty()) {
            throw new RuntimeException("Photo not found with id: " + photoId);
        }

        photo photo = photoOpt.get();
        photoAlbum album = photo.getAlbum();

        // Cloudinary에서 이미지 삭제
        cloudinaryService.deleteImage(photo.getPublicId());

        // 대표 이미지인 경우 앨범 썸네일 정보도 삭제
        if (album.getThumbnailPublicId() != null &&
                album.getThumbnailPublicId().equals(photo.getPublicId())) {
            album.setThumbnailUrl(null);
            album.setThumbnailPublicId(null);
        }

        // 앨범에서 사진 제거
        album.removePhoto(photo);

        // 사진 삭제 (DB)
        photoRepository.delete(photo);

        // 앨범 저장
        photoAlbumRepository.save(album);
    }

    // 앨범 대표 이미지 설정
    @Transactional
    public void setAlbumThumbnail(Long albumId, Long photoId) {
        Optional<photoAlbum> albumOpt = photoAlbumRepository.findById(albumId);
        Optional<photo> photoOpt = photoRepository.findById(photoId);

        if (albumOpt.isEmpty() || photoOpt.isEmpty()) {
            throw new RuntimeException("Album or Photo not found");
        }

        photoAlbum album = albumOpt.get();
        photo photo = photoOpt.get();

        // 선택한 사진이 해당 앨범에 속하는지 확인
        if (!photo.getAlbum().getId().equals(albumId)) {
            throw new RuntimeException("Selected photo does not belong to this album");
        }

        // 앨범 썸네일 업데이트
        album.setThumbnailUrl(photo.getImageUrl());
        album.setThumbnailPublicId(photo.getPublicId());

        photoAlbumRepository.save(album);
    }

    // 사진 캡션 업데이트
    @Transactional
    public void updatePhotoCaption(Long photoId, String caption) {
        photoRepository.updateCaption(photoId, caption);
    }

    // 사진 순서 변경
    @Transactional
    public void updatePhotoOrder(List<Long> photoIds) {
        for (int i = 0; i < photoIds.size(); i++) {
            photoRepository.updateDisplayOrder(photoIds.get(i), i + 1);
        }
    }

    // 앨범의 모든 사진 조회
    @Transactional(readOnly = true)
    public List<photo> getPhotosByAlbum(photoAlbum album) {
        return photoRepository.findByAlbumOrderByDisplayOrderAsc(album);
    }

    // 선택한 사진들 삭제
    @Transactional
    public void deleteSelectedPhotos(Long albumId, List<Long> photoIds) throws IOException {
        Optional<photoAlbum> albumOpt = photoAlbumRepository.findById(albumId);
        if (albumOpt.isEmpty()) {
            throw new RuntimeException("Album not found with id: " + albumId);
        }

        photoAlbum album = albumOpt.get();

        for (Long photoId : photoIds) {
            Optional<photo> photoOpt = photoRepository.findById(photoId);
            if (photoOpt.isPresent()) {
                photo photo = photoOpt.get();

                // 이 사진이 해당 앨범에 속하는지 확인
                if (photo.getAlbum().getId().equals(albumId)) {
                    // Cloudinary에서 이미지 삭제
                    cloudinaryService.deleteImage(photo.getPublicId());

                    // 대표 이미지인 경우 앨범 썸네일 정보도 삭제
                    if (album.getThumbnailPublicId() != null &&
                            album.getThumbnailPublicId().equals(photo.getPublicId())) {
                        album.setThumbnailUrl(null);
                        album.setThumbnailPublicId(null);
                    }

                    // 앨범에서 사진 제거
                    album.removePhoto(photo);

                    // 사진 삭제 (DB)
                    photoRepository.delete(photo);
                }
            }
        }

        // 앨범 저장
        photoAlbumRepository.save(album);
    }

    @Transactional(readOnly = true)
    public Optional<photo> getPhotoById(Long id) {
        return photoRepository.findById(id);
    }
}