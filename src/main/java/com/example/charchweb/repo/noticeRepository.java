package com.example.charchweb.repo;

import com.example.charchweb.DTO.notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface noticeRepository extends JpaRepository<notice, Long> {

    // 제목 또는 내용으로 검색
    Page<notice> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    // 상단 고정 공지 조회
    List<notice> findByPinnedTrueOrderByCreatedAtDesc();

    // 최신 공지사항 조회
    Optional<notice> findTopByOrderByCreatedAtDesc();

    // 이전 공지사항 찾기
    @Query("SELECT n FROM notice n WHERE n.id < :id ORDER BY n.id DESC LIMIT 1")
    Optional<notice> findPreviousNotice(@Param("id") Long id);

    // 다음 공지사항 찾기
    @Query("SELECT n FROM notice n WHERE n.id > :id ORDER BY n.id ASC LIMIT 1")
    Optional<notice> findNextNotice(@Param("id") Long id);
}