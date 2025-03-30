package com.example.charchweb.DTO;

import java.util.List;
import java.util.ArrayList;

public class ChurchSetting {
    private String motto;
    private List<String> practices;
    private String promoVideoUrl;
    private List<SlideContent> slides;

    // Constructor
    public ChurchSetting() {
        // Default values
        this.motto = "이끄심과 나타내심";
        this.practices = new ArrayList<>();
        this.practices.add("하나님의 의와 거룩함을 사모하라");
        this.practices.add("말씀과 기도의 능력 안에 채우라");
        this.practices.add("구령의 열정으로 외치라");
        this.practices.add("변화와 성숙을 기대하라");

        // 기본 홍보 영상 URL
        this.promoVideoUrl = "https://www.youtube.com/embed/qvRizocqDsk?si=q0-D61KzQAXRyyVl";

        // 기본 슬라이드 내용 (버튼 없음)
        this.slides = new ArrayList<>();
        this.slides.add(new SlideContent("변화와 성숙", "하나님의 백성으로서의 삶"));
        this.slides.add(new SlideContent("이끄심...나타내심", "모든 세대가 함께 성장하는 교회"));
        this.slides.add(new SlideContent("말씀으로 세워지는 교회", "복음의 진리를 통해 변화되는 새로운 삶"));
    }

    // Getters and Setters
    public String getMotto() {
        return motto;
    }

    public void setMotto(String motto) {
        this.motto = motto;
    }

    public List<String> getPractices() {
        return practices;
    }

    public void setPractices(List<String> practices) {
        this.practices = practices;
    }

    public String getPromoVideoUrl() {
        return promoVideoUrl;
    }

    public void setPromoVideoUrl(String promoVideoUrl) {
        this.promoVideoUrl = promoVideoUrl;
    }

    public List<SlideContent> getSlides() {
        return slides;
    }

    public void setSlides(List<SlideContent> slides) {
        this.slides = slides;
    }

    // 슬라이드 콘텐츠를 위한 내부 클래스 (버튼 없음)
    public static class SlideContent {
        private String title;
        private String subtitle;

        public SlideContent() {
        }

        public SlideContent(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }
    }
}