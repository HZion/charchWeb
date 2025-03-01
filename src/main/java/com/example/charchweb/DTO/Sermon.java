package com.example.charchweb.DTO;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

// Sermon.java (엔티티 클래스)
@Entity
@Table(name = "sermons")
@Getter
@Setter
public class Sermon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;           // 제목

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;              // 날짜

    private String preacher;        // 설교자

    private String bibleVerse;      // 성경구절

    @Lob
    private String content;         // 본문

    private String thumbnailUrl;    // 썸네일 URL (선택적)

    // getter, setter, 생성자 등
}