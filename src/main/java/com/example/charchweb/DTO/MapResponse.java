package com.example.charchweb.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapResponse {
    private String key;

    // 기본 생성자
    public MapResponse() {
    }

    // 매개변수가 있는 생성자
    public MapResponse(String key) {
        this.key = key;
    }

    // Getter와 Setter
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
