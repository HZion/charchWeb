package com.example.charchweb.Router;

import com.example.charchweb.DTO.MapResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class mapConttroller {
    @Value("${kakao.map.api.key}")
    private String kakaoApi;


    @GetMapping("/map-key")
    public Map<String, String> getMapKey(){
        Map<String, String> response = new HashMap<>();
        response.put("key", kakaoApi);
        return response;
    }
}
