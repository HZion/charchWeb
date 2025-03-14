package com.example.charchweb.Router.admin;


import com.example.charchweb.DTO.notice;
import com.example.charchweb.DTO.noticeAttachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/andio/notice")
public class noticeController {

    @Autowired
    private com.example.charchweb.service.noticeService noticeService;

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping("")
    public String noticeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            Model model) {

        // 정렬 조건을 최신순으로 유지하면서 모든 공지사항 가져오기
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<notice> noticePage;
        if (keyword != null && !keyword.isEmpty()) {
            noticePage = noticeService.searchNotices(keyword, pageable);
        } else {
            noticePage = noticeService.getAllNotices(pageable);
        }

        // 디버깅용 로그 추가
        System.out.println("Total notices: " + noticePage.getTotalElements());
        System.out.println("Current page notices: " + noticePage.getContent().size());

        model.addAttribute("noticePage", noticePage);
        model.addAttribute("keyword", keyword);

        return "admin/noticeList";
    }

    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        Optional<notice> notice = noticeService.getNoticeById(id);

        if (notice.isPresent()) {
            model.addAttribute("notice", notice.get());

            // 수정 이력 로그
            model.addAttribute("logs", noticeService.getNoticeLogs(id));

            return "admin/noticeDetail";
        }

        return "redirect:/andio/notice";
    }

    /**
     * 공지사항 작성 폼
     */
    @GetMapping("/add")
    public String noticeAddForm(Model model) {
        // 빈 공지사항 객체 생성
        notice notice = new notice();
        notice.setPinned(false);

        model.addAttribute("notice", notice);

        return "admin/noticeDetail";
    }

    /**
     * 공지사항 등록 처리
     */
    @PostMapping("/add")
    public String noticeAdd(
            @ModelAttribute notice notice,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            // 공지사항 저장
            notice.setCreatedAt(LocalDateTime.now());
            notice.setViewCount(0);
            notice.setCreatedBy(authentication.getName());

            // 첨부파일이 있는 경우 처리
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String fileUrl = noticeService.saveAttachmentFile(file);
                        noticeAttachment attachment = new noticeAttachment();
                        attachment.setOriginalFilename(file.getOriginalFilename());
                        attachment.setFileUrl(fileUrl);
                        attachment.setFileSize(file.getSize());
                        notice.addAttachment(attachment);
                    }
                }
            }

            noticeService.saveNotice(notice);

            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 성공적으로 등록되었습니다.");
            return "redirect:/andio/notice";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/andio/notice/add";
        }
    }

    /**
     * 공지사항 수정 폼
     */
    @GetMapping("/edit/{id}")
    public String noticeEditForm(@PathVariable Long id, Model model) {
        Optional<notice> notice = noticeService.getNoticeById(id);

        if (notice.isPresent()) {
            model.addAttribute("notice", notice.get());
            return "admin/noticeDetail";
        }

        return "redirect:/andio/notice";
    }

    /**
     * 공지사항 수정 처리
     */
    @PostMapping("/edit/{id}")
    public String noticeEdit(
            @PathVariable Long id,
            @ModelAttribute notice notice,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "keepAttachments", required = false) List<Long> keepAttachments,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<com.example.charchweb.DTO.notice> existingNotice = noticeService.getNoticeById(id);

            if (existingNotice.isPresent()) {
                com.example.charchweb.DTO.notice updatedNotice = existingNotice.get();

                // 기본 정보 업데이트
                updatedNotice.setTitle(notice.getTitle());
                updatedNotice.setContent(notice.getContent());
                updatedNotice.setPinned(notice.isPinned());
                updatedNotice.setUpdatedAt(LocalDateTime.now());
                updatedNotice.setUpdatedBy(authentication.getName());

                // 첨부파일 처리
                // 1. 보존할 첨부파일 이외는 모두 삭제
                if (keepAttachments != null) {
                    noticeService.deleteAttachmentsExcept(id, keepAttachments);
                } else {
                    noticeService.deleteAllAttachments(id);
                }

                // 2. 새로운 첨부파일 추가
                if (files != null && !files.isEmpty()) {
                    for (MultipartFile file : files) {
                        if (!file.isEmpty()) {
                            String fileUrl = noticeService.saveAttachmentFile(file);
                            noticeAttachment attachment = new noticeAttachment();
                            attachment.setOriginalFilename(file.getOriginalFilename());
                            attachment.setFileUrl(fileUrl);
                            attachment.setFileSize(file.getSize());
                            updatedNotice.addAttachment(attachment);
                        }
                    }
                }

                noticeService.saveNotice(updatedNotice);

                redirectAttributes.addFlashAttribute("successMessage", "공지사항이 성공적으로 수정되었습니다.");
                return "redirect:/andio/notice";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 수정 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/andio/notice/edit/" + id;
    }

    /**
     * 공지사항 삭제 처리
     */
    @PostMapping("/delete/{id}")
    public String noticeDelete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("successMessage", "공지사항이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "공지사항 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

        return "redirect:/andio/notice";
    }
}