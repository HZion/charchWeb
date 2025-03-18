package com.example.charchweb.service;

import com.example.charchweb.DTO.ChurchSetting;
import org.springframework.stereotype.Service;

@Service
public class ChurchSettingService {
    private final ChurchSetting churchSetting;

    // Constructor-based initialization
    public ChurchSettingService() {
        // Initialize with default values
        this.churchSetting = new ChurchSetting();
    }

    public ChurchSetting getChurchSetting() {
        return churchSetting;
    }

    public void updateMotto(String motto) {
        churchSetting.setMotto(motto);
    }

    public void updatePractices(String practice, int index) {
        if (index >= 0 && index < churchSetting.getPractices().size()) {
            churchSetting.getPractices().set(index, practice);
        }
    }

    public void updatePromoVideoUrl(String promoVideoUrl) {
        churchSetting.setPromoVideoUrl(promoVideoUrl);
    }

    public void updateSlideContent(int index, String title, String subtitle) {
        if (index >= 0 && index < churchSetting.getSlides().size()) {
            ChurchSetting.SlideContent slide = churchSetting.getSlides().get(index);
            slide.setTitle(title);
            slide.setSubtitle(subtitle);
        }
    }
}