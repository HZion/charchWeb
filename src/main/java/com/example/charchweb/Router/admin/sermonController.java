package com.example.charchweb.Router.admin;

import com.example.charchweb.DTO.Sermon;
import com.example.charchweb.service.sermonService;
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

import java.util.Optional;

@Controller
@RequestMapping("/admin/sermon")
public class sermonController {

    @Autowired
    private com.example.charchweb.service.sermonService sermonService;

    // 설교 관리 페이지
    @GetMapping("")
    public String sermonPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // 빈 Sermon 객체 (폼용)
        model.addAttribute("sermon", new Sermon());

        // 설교 목록을 DB에서 가져오기
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<Sermon> sermonsPage = sermonService.getAllSermons(pageable);
        model.addAttribute("sermonsPage", sermonsPage);

        return "admin/sermon";
    }

    // 설교 추가 페이지
    @GetMapping("/add")
    public String addSermonPage(Model model) {
        model.addAttribute("sermon", new Sermon());
        return "admin/sermon-form";
    }

    // 설교 수정 페이지
    @GetMapping("/edit/{id}")
    public String editSermonPage(@PathVariable Long id, Model model) {
        Optional<Sermon> optionalSermon = sermonService.getSermonById(id);

        if (optionalSermon.isPresent()) {
            model.addAttribute("sermon", optionalSermon.get());
            return "admin/sermon-form";
        } else {
            return "redirect:/admin/sermon";
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
        return "redirect:/admin/sermon";
    }

    // 설교 삭제
    @PostMapping("/delete/{id}")
    public String deleteSermon(@PathVariable Long id) {
        sermonService.deleteSermon(id);
        return "redirect:/admin/sermon";
    }
}
