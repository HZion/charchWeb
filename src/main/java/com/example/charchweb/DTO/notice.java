package com.example.charchweb.DTO;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "notices")
public class notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String createdBy;

    private String updatedBy;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer viewCount;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean pinned;

    private String thumbnailUrl;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<noticeAttachment> attachments = new ArrayList<>();

    // 첨부파일 추가 메서드
    public void addAttachment(noticeAttachment attachment) {
        attachments.add(attachment);
        attachment.setNotice(this);
    }

    // 첨부파일 제거 메서드
    public void removeAttachment(noticeAttachment attachment) {
        attachments.remove(attachment);
        attachment.setNotice(null);
    }

    // 첨부파일 여부 확인 (getter 자동 생성됨)
    @Transient
    public boolean isHasAttachment() {
        return attachments != null && !attachments.isEmpty();
    }
}