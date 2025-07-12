package com.example.devjobs.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Jackson ObjectMapper를 사용하여 객체와 JSON 문자열 간의 변환을 처리하는 유틸리티 클래스입니다.
 */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * List 객체를 JSON 문자열로 변환합니다.
     *
     * @param list 변환할 List 객체
     * @return JSON 문자열, 변환 실패 시 null
     */
    public static String convertListToJson(List<?> list) {
        try {
            if (list != null) {
                return objectMapper.writeValueAsString(list);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("List to JSON 변환에 실패했습니다.", e);
        }
        return null;
    }

    /**
     * JSON 문자열을 지정된 타입의 List 객체로 변환합니다.
     *
     * @param json  변환할 JSON 문자열
     * @param clazz 리스트 요소의 클래스 타입
     * @param <T>   리스트 요소의 제네릭 타입
     * @return 변환된 List 객��, 변환 실패 시 null
     */
    public static <T> List<T> convertJsonToList(String json, Class<T> clazz) {
        try {
            if (json != null && !json.isEmpty()) {
                return objectMapper.readValue(json,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON to List 변환에 실패했습니다.", e);
        }
        return null;
    }
}
