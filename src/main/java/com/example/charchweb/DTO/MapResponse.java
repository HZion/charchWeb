package com.example.charchweb.DTO;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MapResponse {
    private double latitude;
    private double longitude;
    private String address;
}
