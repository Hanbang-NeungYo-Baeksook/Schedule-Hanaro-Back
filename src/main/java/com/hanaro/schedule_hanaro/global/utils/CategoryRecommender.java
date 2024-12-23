package com.hanaro.schedule_hanaro.global.utils;

import com.hanaro.schedule_hanaro.global.domain.enums.Category;
import java.util.*;
import java.util.stream.Collectors;
import com.hanaro.schedule_hanaro.global.utils.FAQTokenizer;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class CategoryRecommender {
    // 카테고리별 관련 키워드 매핑
    private static final Map<Category, List<String>> CATEGORY_KEYWORDS = new HashMap<>() {{
        put(Category.SIGNIN, Arrays.asList(
            "로그인", "접속", "아이디", "비밀번호", "인증", "보안", "로그아웃", "접근",
            "계정", "인증서", "보안카드", "OTP", "간편로그인", "생체인증"
        ));
        
        put(Category.SIGNUP, Arrays.asList(
            "회원가입", "가입", "신규", "등록", "개설", "약관", "동의", "본인인증",
            "실명인증", "신분증", "인증서", "계정생성"
        ));
        
        put(Category.AUTH, Arrays.asList(
            "인증", "보안", "인증서", "보안카드", "OTP", "생체인증", "지문", "안면",
            "본인확인", "실명인증", "신분증", "공동인증서", "금융인증서"
        ));
        
        put(Category.DEPOSIT, Arrays.asList(
            "예금", "적금", "이자", "금리", "통장", "계좌", "입금", "출금", "예치", "만기",
            "이율", "수익률", "저축", "예치금", "정기예금", "��유적금"
        ));
        
        put(Category.TRUST, Arrays.asList(
            "신탁", "투자", "수익률", "펀드", "자산관리", "위탁", "운용", "신탁자산",
            "수익증권", "원금", "이자", "배당", "수익분배"
        ));
        
        put(Category.FUND, Arrays.asList(
            "펀드", "투자", "수익률", "환매", "적립식", "거치식", "분배금", "기준가",
            "자산운용", "투자위험", "수익증권", "펀드매니저", "운용보고서"
        ));
        
        put(Category.LOAN, Arrays.asList(
            "대출", "담보", "신용", "이자율", "상환", "대부", "원금", "한도", "신용등급",
            "중도상환", "대출금", "이자", "원리금", "상환계획", "만기", "연체"
        ));
        
        put(Category.FOREX, Arrays.asList(
            "외환", "환전", "달러", "엔화", "유로", "환율", "해외송금", "외화", "해외",
            "외화예금", "외화적금", "국제송금", "해외계좌", "외국환"
        ));
        
        put(Category.INERNET_BANKING, Arrays.asList(
            "인터넷뱅킹", "모바일뱅킹", "앱", "로그인", "비밀번호", "보안", "���증서",
            "이체", "조회", "스마트폰", "온라인", "웹", "접속", "등록"
        ));
        
        put(Category.HANAONEQ, Arrays.asList(
            "하나원큐", "앱", "모바일", "스마트폰", "어플", "원큐", "하나", "이체",
            "조회", "알림", "푸시", "설정", "인증서", "보안"
        ));
        
        put(Category.PHONE_BANKING, Arrays.asList(
            "폰뱅킹", "전화", "통화", "ARS", "음성", "상담", "전화상담", "폰뱅킹",
            "자동응답", "상담원", "전화이체", "안내"
        ));
        
        put(Category.CD, Arrays.asList(
            "CD", "현금자동입출금기", "입금", "출금", "이체", "잔액조회", "계좌이체",
            "통장정리", "현금", "카드", "수수료"
        ));
        
        put(Category.ATM, Arrays.asList(
            "ATM", "현금자동입출금기", "입금", "출금", "이체", "잔액조회", "계좌이체",
            "통장정리", "현금", "카드", "수수료", "체크카드", "신용카드"
        ));
        
        put(Category.UTILITY_BILL, Arrays.asList(
            "공과금", "납부", "세금", "국민연금", "건강보험", "전���요��", "수도요금",
            "가스요금", "지방세", "국세", "범칙금", "과태료"
        ));
        
        put(Category.FOREIGN, Arrays.asList(
            "해외", "외국", "국제", "글로벌", "외화", "해외송금", "환전", "외환",
            "해외계좌", "해외카드", "여행", "비자"
        ));
        
        put(Category.BRANCH, Arrays.asList(
            "영업점", "지점", "창구", "은행", "방문", "상담", "직원", "업무시간",
            "위치", "주소", "전화번호", "상담원", "예약"
        ));
    }};

    public static Map.Entry<Category, Double> recommendCategory(String query) {
        // 질문 토큰화
        List<String> queryTokens = tokenizeQuery(query);
        
        // 각 카테고리별 점수 계산
        Map<Category, Double> categoryScores = calculateCategoryScores(queryTokens);
        
        // 가장 높은 점수의 카테고리와 신뢰도 반환
        return categoryScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(Map.entry(Category.INERNET_BANKING, 0.0)); // 기본값
    }

    private static List<String> tokenizeQuery(String query) {
        return FAQTokenizer.tokenizeNewQuestion(query);
    }

    private static Map<Category, Double> calculateCategoryScores(List<String> tokens) {
        Map<Category, Double> scores = new HashMap<>();
        
        for (Category category : CATEGORY_KEYWORDS.keySet()) {
            double score = calculateSimilarity(tokens, CATEGORY_KEYWORDS.get(category));
            scores.put(category, score);
        }
        
        return scores;
    }

    private static double calculateSimilarity(List<String> queryTokens, List<String> categoryKeywords) {
        // 모든 고유 단어 수집
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(queryTokens);
        allTerms.addAll(categoryKeywords);
        
        // 벡터 생성
        RealVector queryVector = createVector(queryTokens, allTerms);
        RealVector categoryVector = createVector(categoryKeywords, allTerms);
        
        // 코사인 유사도 계산
        return cosineSimilarity(queryVector, categoryVector);
    }

    private static RealVector createVector(List<String> tokens, Set<String> allTerms) {
        double[] vector = new double[allTerms.size()];
        List<String> termsList = new ArrayList<>(allTerms);
        
        for (int i = 0; i < termsList.size(); i++) {
            String term = termsList.get(i);
            vector[i] = Collections.frequency(tokens, term);
        }
        
        return new ArrayRealVector(vector);
    }

    private static double cosineSimilarity(RealVector vectorA, RealVector vectorB) {
        double dotProduct = vectorA.dotProduct(vectorB);
        double normA = vectorA.getNorm();
        double normB = vectorB.getNorm();
        
        if (normA == 0 || normB == 0) return 0.0;
        
        return dotProduct / (normA * normB);
    }
} 