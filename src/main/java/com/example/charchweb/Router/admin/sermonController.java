package com.example.charchweb.Router.admin;

import com.example.charchweb.DTO.Sermon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/andio/sermon")
public class sermonController {

    @Autowired
    private com.example.charchweb.service.sermonService sermonService;

    // 설교 관리 페이지
    @GetMapping("")
    public String sermonPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String preacher,
            Model model) {

        // 페이지네이션 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());

        Page<Sermon> sermonsPage;

        // 검색 조건에 따라 다른 쿼리 실행
        if (keyword != null && !keyword.isEmpty()) {
            // 키워드 검색 (제목, 설교자, 성경구절)
            sermonsPage = sermonService.searchSermons(keyword, pageable);
        } else if (year != null && !year.isEmpty() || preacher != null && !preacher.isEmpty()) {
            // 연도나 설교자로 필터링
            sermonsPage = sermonService.filterSermons(year, preacher, pageable);
        } else {
            // 모든 설교 가져오기
            sermonsPage = sermonService.getAllSermons(pageable);
        }

        // 모델에 추가 (두 변수명 모두 추가하여 호환성 유지)
        model.addAttribute("sermonsPage", sermonsPage);
        model.addAttribute("sermons", sermonsPage); // 기존 템플릿과의 호환성을 위해

        // 설교자 목록 추가 (필터용)
        List<String> preachers = sermonService.getAllPreachers();
        model.addAttribute("preachers", preachers);

        return "admin/sermonList";
    }

    // 설교 추가 페이지
    @GetMapping("/add")
    public String addSermonPage(Model model) {
        // 빈 Sermon 객체 (폼용)
        model.addAttribute("sermon", new Sermon());
        model.addAttribute("isNew", true);  // 새 설교임을 표시

        return "admin/sermonForm";  // 통합된 폼 템플릿 사용
    }

    // 설교 수정 페이지
    @GetMapping("/edit/{id}")
    public String editSermonPage(@PathVariable Long id, Model model) {
        Optional<Sermon> optionalSermon = sermonService.getSermonById(id);

        if (optionalSermon.isPresent()) {
            model.addAttribute("sermon", optionalSermon.get());
            model.addAttribute("isNew", false);  // 기존 설교 수정임을 표시
            return "admin/sermonForm";
        } else {
            return "redirect:/andio/sermon";
        }
    }

    // 설교 상세 보기 페이지
    @GetMapping("/{id}")
    public String sermonDetailPage(@PathVariable Long id, Model model) {
        Optional<Sermon> optionalSermon = sermonService.getSermonById(id);

        if (optionalSermon.isPresent()) {
            model.addAttribute("sermon", optionalSermon.get());
            return "admin/sermonDetail";  // 설교 상세 보기 템플릿
        } else {
            return "redirect:/adio/sermon";
        }
    }

    // 설교 저장 (추가/수정)
    @PostMapping("/save")
    public String saveSermon(@ModelAttribute Sermon sermon,
                             @RequestParam(required = false) MultipartFile thumbnail) {

        // 썸네일이 있으면 저장
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailUrl = sermonService.saveThumbnail(thumbnail);
            sermon.setThumbnailUrl(thumbnailUrl);
        }

        sermonService.saveSermon(sermon);
        return "redirect:/andio/sermon";
    }

    // 설교 삭제
    @PostMapping("/delete/{id}")
    public String deleteSermon(@PathVariable Long id) {
        sermonService.deleteSermon(id);
        return "redirect:/andio/sermon";
    }
}