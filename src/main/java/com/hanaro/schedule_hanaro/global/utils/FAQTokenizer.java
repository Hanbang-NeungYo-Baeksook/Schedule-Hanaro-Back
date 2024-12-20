package com.hanaro.schedule_hanaro.global.utils;

import org.openkoreantext.processor.OpenKoreanTextProcessorJava;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FAQTokenizer {

    private static final List<String> EXCLUDED_TOKENS = List.of("질문","수","더","때문","오니","시","생","은","요","로서","중","대해","란","무엇","기타","함","때","꼭","각종","대한","것"); // 제외할 단어
    private static final List<String> PRESERVE_TOKENS = List.of("하나은행", "홈페이지","영업점","해지","중도","하나원큐"); // 나누지 않고 포함할 단어
    private static final List<String> COMPOSITE_TOKENS = List.of("하나원큐","납입금"); // 합성어 리스트

    // Filters tokens to remove unwanted words like "질문" and preserve specific words
    public static List<String> filterTokens(List<String> tokens) {
        // Map tokens by combining composite words first
        return tokens.stream()
                .map(token -> token.split("\\(")[0].trim()) // 태그 제거 및 공백 제거
                .map(String::toLowerCase) // 소문자로 변환
                .filter(token -> !EXCLUDED_TOKENS.contains(token)) // 제외 단어 필터링
                .map(FAQTokenizer::preserveTokens) // 특정 단어 유지
                .map(FAQTokenizer::handleCompositeTokens) // 합성어 처리
                .collect(Collectors.toList());
    }
    public static List<String> tokenizeNewQuestion(String question) {
        List<String> tokens = KoreanTokenizerUtil.extractNouns(question); // 질문에서 명사 추출
        return filterTokens(tokens); // filterTokens를 통해 필터링된 토큰 반환
    }

    // Handle composite tokens like "하나원큐"
    private static String handleCompositeTokens(String token) {
        // If the token is part of the composite list, return it as is
        for (String composite : COMPOSITE_TOKENS) {
            if (token.contains(composite)) {
                return composite; // Return the whole composite token
            }
        }
        return token; // Otherwise, return the token as is
    }

    // Check if the token should be preserved and return it as-is or process it normally
    private static String preserveTokens(String token) {
        // PRESERVE_TOKENS에 포함된 단어는 그대로 반환
        for (String preserve : PRESERVE_TOKENS) {
            if (token.contains(preserve)) {
                return preserve; // 전체 단어를 반환
            }
        }
        return token; // 일반 단어는 그대로 유지
    }


}
