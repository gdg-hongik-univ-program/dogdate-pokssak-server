package com.example.dogmeeting.controller;

import com.example.dogmeeting.dto.RegionData;
import com.example.dogmeeting.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    /**
     * 모든 시/도 목록 조회
     * GET /api/regions/cities
     */
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        List<String> cities = regionService.getCities();
        return ResponseEntity.ok(cities);
    }

    /**
     * 특정 시/도의 구/군 목록 조회
     * GET /api/regions/cities/{cityName}/districts
     */
    @GetMapping("/cities/{cityName}/districts")
    public ResponseEntity<List<String>> getDistrictsByCity(@PathVariable String cityName) {
        List<String> districts = regionService.getDistrictsByCity(cityName);
        return ResponseEntity.ok(districts);
    }

    /**
     * 모든 지역 데이터 조회 (시/도와 구/군 포함)
     * GET /api/regions/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<RegionData>> getAllRegions() {
        List<RegionData> regions = regionService.getAllRegions();
        return ResponseEntity.ok(regions);
    }
} 