package com.example.charchweb.repo;

import com.example.charchweb.DTO.photo;
import com.example.charchweb.DTO.photoAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface photoRepository extends JpaRepository<photo, Long> {

    // 앨범에 속한 모든 사진 조회
    List<photo> findByAlbumOrderByDisplayOrderAsc(photoAlbum album);

    // 앨범에 속한 사진 수 조회
    long countByAlbum(photoAlbum album);

    // 사진 캡션 업데이트
    @Modifying
    @Query("UPDATE photo p SET p.caption = :caption WHERE p.id = :id")
    void updateCaption(@Param("id") Long id, @Param("caption") String caption);

    // 사진 표시 순서 업데이트
    @Modifying
    @Query("UPDATE photo p SET p.displayOrder = :order WHERE p.id = :id")
    void updateDisplayOrder(@Param("id") Long id, @Param("order") Integer order);
}