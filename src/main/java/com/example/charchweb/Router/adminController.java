package com.example.charchweb.Router;

import com.example.charchweb.DTO.ChurchSetting;
import com.example.charchweb.DTO.Sermon;
import com.example.charchweb.service.ChurchSettingService;
import com.example.charchweb.service.sermonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/andio")
public class adminController {

    @Autowired
    private ChurchSettingService churchSettingService;

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/andio";
        }
        return "admin/login";
    }

    @GetMapping("")
    public String adminPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            // Get church settings
            ChurchSetting churchSetting = churchSettingService.getChurchSetting();
            model.addAttribute("churchSetting", churchSetting);
            return "admin/dashBoard";
        }
        return "admin/login";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "admin/login";
        }

        // Get current church settings
        ChurchSetting churchSetting = churchSettingService.getChurchSetting();
        model.addAttribute("churchSetting", churchSetting);

        return "admin/settings";
    }

    @PostMapping("/settings/update-motto")
    public String updateMotto(@RequestParam String motto, RedirectAttributes redirectAttributes) {
        churchSettingService.updateMotto(motto);
        redirectAttributes.addFlashAttribute("successMessage", "교회 표어가 성공적으로 저장되었습니다.");
        return "redirect:/andio/settings";
    }

    @PostMapping("/settings/update-practice")
    public String updatePractice(@RequestParam String practice, @RequestParam int index,
                                 RedirectAttributes redirectAttributes) {
        churchSettingService.updatePractices(practice, index);
        redirectAttributes.addFlashAttribute("successMessage", "생활 실천이 성공적으로 저장되었습니다.");
        return "redirect:/andio/settings";
    }

    @PostMapping("/settings/update-promo-video")
    public String updatePromoVideo(@RequestParam String promoVideoUrl, RedirectAttributes redirectAttributes) {
        churchSettingService.updatePromoVideoUrl(promoVideoUrl);
        redirectAttributes.addFlashAttribute("successMessage", "홍보 영상 URL이 성공적으로 저장되었습니다.");
        return "redirect:/andio/settings";
    }

    @PostMapping("/settings/update-slide")
    public String updateSlide(
            @RequestParam int index,
            @RequestParam String title,
            @RequestParam String subtitle,
            RedirectAttributes redirectAttributes) {

        churchSettingService.updateSlideContent(index, title, subtitle);
        redirectAttributes.addFlashAttribute("successMessage", "슬라이드 내용이 성공적으로 저장되었습니다.");
        return "redirect:/andio/settings";
    }

    @GetMapping("/settings/get")
    @ResponseBody
    public ChurchSetting getSettings() {
        return churchSettingService.getChurchSetting();
    }
}