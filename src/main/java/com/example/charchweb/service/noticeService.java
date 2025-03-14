package com.example.charchweb.service;

import com.example.charchweb.DTO.notice;
import com.example.charchweb.DTO.noticeAttachment;
import com.example.charchweb.DTO.noticeLog;
import com.example.charchweb.repo.noticeAttachmentRepository;
import com.example.charchweb.repo.noticeLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class noticeService {

    @Autowired
    private com.example.charchweb.repo.noticeRepository noticeRepository;

    @Autowired
    private noticeAttachmentRepository attachmentRepository;

    @Autowired
    private noticeLogRepository logRepository;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 모든 공지사항 가져오기 (페이징)
     */
    public Page<notice> getAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    /**
     * 공지사항 검색
     */
    public Page<notice> searchNotices(String keyword, Pageable pageable) {
        return noticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }

    /**
     * ID로 공지사항 가져오기
     */
    public Optional<notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }

    /**
     * 최신 공지사항 가져오기
     */
    public Optional<notice> getLatestNotice() {
        return noticeRepository.findTopByOrderByCreatedAtDesc();
    }

    /**
     * 상단 고정 공지사항 목록 가져오기
     */
    public List<notice> getPinnedNotices() {
        return noticeRepository.findByPinnedTrueOrderByCreatedAtDesc();
    }

    /**
     * 이전 공지사항 가져오기
     */
    public Optional<notice> getPreviousNotice(Long id) {
        return noticeRepository.findPreviousNotice(id);
    }

    /**
     * 다음 공지사항 가져오기
     */
    public Optional<notice> getNextNotice(Long id) {
        return noticeRepository.findNextNotice(id);
    }

    /**
     * 공지사항 저장 (추가/수정)
     */
    @Transactional
    public notice saveNotice(notice notice) {
        return noticeRepository.save(notice);
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public void deleteNotice(Long id) {
        // 첨부파일 먼저 삭제
        deleteAllAttachments(id);

        // 공지사항 삭제
        noticeRepository.deleteById(id);
    }

    /**
     * 조회수 증가
     */
    @Transactional
    public void increaseViewCount(Long id) {
        Optional<notice> notice = noticeRepository.findById(id);
        if (notice.isPresent()) {
            com.example.charchweb.DTO.notice updatedNotice = notice.get();
            updatedNotice.setViewCount(updatedNotice.getViewCount() + 1);
            noticeRepository.save(updatedNotice);
        }
    }

    /**
     * 첨부파일 저장
     */
    public String saveAttachmentFile(MultipartFile file) throws IOException {
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
    }

    /**
     * 공지사항에 속한 모든 첨부파일 가져오기
     */
    public List<noticeAttachment> getAttachmentsByNoticeId(Long noticeId) {
        return attachmentRepository.findByNoticeId(noticeId);
    }

    /**
     * 특정 첨부파일 제외하고 모두 삭제
     */
    @Transactional
    public void deleteAttachmentsExcept(Long noticeId, List<Long> keepAttachmentIds) {
        List<noticeAttachment> toDelete = attachmentRepository.findByNoticeIdAndIdNotIn(noticeId, keepAttachmentIds);

        // 실제 파일 삭제 (필요한 경우)
        for (noticeAttachment attachment : toDelete) {
            // 파일 시스템에서 파일 삭제 로직
            String fileUrl = attachment.getFileUrl();
            if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
                try {
                    Path filePath = Paths.get(uploadPath, fileUrl.substring(9));
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    // 파일 삭제 실패 로깅
                }
            }
        }

        // DB에서 첨부파일 데이터 삭제
        attachmentRepository.deleteAll(toDelete);
    }

    /**
     * 공지사항에 속한 모든 첨부파일 삭제
     */
    @Transactional
    public void deleteAllAttachments(Long noticeId) {
        List<noticeAttachment> attachments = attachmentRepository.findByNoticeId(noticeId);

        // 실제 파일 삭제 (필요한 경우)
        for (noticeAttachment attachment : attachments) {
            // 파일 시스템에서 파일 삭제 로직
            String fileUrl = attachment.getFileUrl();
            if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
                try {
                    Path filePath = Paths.get(uploadPath, fileUrl.substring(9));
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    // 파일 삭제 실패 로깅
                }
            }
        }

        // DB에서 첨부파일 데이터 삭제
        attachmentRepository.deleteByNoticeId(noticeId);
    }

    /**
     * 공지사항 로그 가져오기
     */
    public List<noticeLog> getNoticeLogs(Long noticeId) {
        return logRepository.findByNoticeIdOrderByTimestampDesc(noticeId);
    }

    /**
     * 공지사항 로그 추가
     */
    @Transactional
    public void addNoticeLog(Long noticeId, String username, String action, String description) {
        noticeLog log = new noticeLog();
        log.setNoticeId(noticeId);
        log.setUsername(username);
        log.setAction(action);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());

        logRepository.save(log);
    }

    /**
     * 키워드로 공지사항 검색 (수정된 메소드)
     */
    public Page<notice> searchnotices(String keyword, Pageable pageable) {
        return noticeRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }
}