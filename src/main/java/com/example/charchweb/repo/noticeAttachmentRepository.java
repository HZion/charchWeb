package com.example.charchweb.repo;

import com.example.charchweb.DTO.noticeAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface noticeAttachmentRepository extends JpaRepository<noticeAttachment, Long> {

    // 공지사항 ID로 첨부파일 목록 조회
    List<noticeAttachment> findByNoticeId(Long noticeId);

    // 공지사항 ID로 첨부파일 삭제
    void deleteByNoticeId(Long noticeId);

    // 지정한 ID 목록 제외하고 공지사항 첨부파일 조회
    List<noticeAttachment> findByNoticeIdAndIdNotIn(Long noticeId, List<Long> ids);
}