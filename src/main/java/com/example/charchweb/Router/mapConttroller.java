package com.example.charchweb.Router;

import com.example.charchweb.DTO.MapResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class mapConttroller {
    @Value("${kakao.map.api)")
    private String kakaoApi;

    @GetMapping("/map-info")
    public MapResponse getMapInfo(){
        return MapResponse.builder()
                .latitude(34.5735)
                .longitude(126.5991)
                .address("전라남도 해남군 풍혈길 300 안디옥교회")
                .build();
    }
}
