package com.example.devjobs.kakaomap.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

/**
 * Kakao Maps API와 통신하여 주소를 좌표로 변환하는 서비스를 제공합니다.
 */
@Service
public class KakaoMapService {

    @Value("${KAKAO_API_KEY}")
    private String kakaoApiKey;

    private static final String KAKAO_API_URL = "https://dapi.kakao.com/v2/local/search/address.json";

    public String getCoordinates(String address) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        String url = KAKAO_API_URL + "?query=" + address;
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    String.class
            );
            return response.getBody();
        } catch (Exception e) {
            // API 요청 실패 시 로깅을 고려할 수 있습니다.
            throw new RuntimeException("카카오 API 요청 중 오류가 발생했습니다.", e);
        }
    }
}
