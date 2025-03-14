package com.example.charchweb.DTO;


import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "photo_albums")
@Data
@Getter
@Setter
public class photoAlbum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    private String category;

    private String thumbnailUrl;

    private String thumbnailPublicId;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<photo> photos = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // 앨범에 사진 추가 메서드
    public void addPhoto(photo photo) {
        photos.add(photo);
        photo.setAlbum(this);

        // 표시 순서 설정 (마지막에 추가)
        if (photo.getDisplayOrder() == null) {
            photo.setDisplayOrder(photos.size());
        }
    }

    // 앨범에서 사진 제거 메서드
    public void removePhoto(photo photo) {
        photos.remove(photo);
        photo.setAlbum(null);
    }
}