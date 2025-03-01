package com.example.charchweb.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubMenu {
    private String title;    // 메뉴 제목
    private String url;      // 메뉴 URL
    private boolean active;  // 현재 활성화 여부

    public SubMenu(String title, String url) {
        this.title = title;
        this.url = url;
        this.active = false;
    }
}