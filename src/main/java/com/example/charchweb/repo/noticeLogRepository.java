package com.example.charchweb.repo;

import com.example.charchweb.DTO.noticeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface noticeLogRepository extends JpaRepository<noticeLog, Long> {

    // 공지사항 ID로 로그 조회 (시간 역순)
    List<noticeLog> findByNoticeIdOrderByTimestampDesc(Long noticeId);
}