package com.example.yacht;

import java.util.HashMap;
import java.util.Map;

/**
 * 각 플레이어의 턴별/카테고리별 점수를 관리하는 클래스입니다.
 */
public class ScorePerPlayer {

    // 각 점수 카테고리(예: Aces, Full House)의 최종 점수
    private Map<String, Integer> finalScores;
    // 각 점수 카테고리가 해당 플레이어에 의해 이미 사용(기록)되었는지 여부
    private Map<String, Boolean> categoriesUsed;

    // Upper Section 총점 및 보너스 점수
    private int upperSectionTotal;
    private int bonusScore; // 63점 이상 시 35점 보너스
    private int totalScore; // 최종 총점

    // 야추 게임의 점수 카테고리 목록
    public static final String CATEGORY_ACES = "Aces";
    public static final String CATEGORY_TWOS = "Twos";
    public static final String CATEGORY_THREES = "Threes";
    public static final String CATEGORY_FOURS = "Fours";
    public static final String CATEGORY_FIVES = "Fives";
    public static final String CATEGORY_SIXES = "Sixes";
    public static final String CATEGORY_CHOICE = "Choice";
    public static final String CATEGORY_4_OF_A_KIND = "4 of a Kind";
    public static final String CATEGORY_FULL_HOUSE = "Full House";
    public static final String CATEGORY_SMALL_STRAIGHT = "Small Straight";
    public static final String CATEGORY_LARGE_STRAIGHT = "Large Straight";
    public static final String CATEGORY_YACHT = "Yacht";

    public ScorePerPlayer() {
        initializeScores();
    }

    private void initializeScores() {
        finalScores = new HashMap<>();
        categoriesUsed = new HashMap<>();

        // 모든 카테고리에 대해 초기 점수 0, 사용 안 됨으로 설정
        String[] categories = {
                CATEGORY_ACES, CATEGORY_TWOS, CATEGORY_THREES, CATEGORY_FOURS, CATEGORY_FIVES, CATEGORY_SIXES,
                CATEGORY_CHOICE, CATEGORY_4_OF_A_KIND, CATEGORY_FULL_HOUSE, CATEGORY_SMALL_STRAIGHT,
                CATEGORY_LARGE_STRAIGHT, CATEGORY_YACHT
        };

        for (String category : categories) {
            finalScores.put(category, 0);
            categoriesUsed.put(category, false);
        }

        upperSectionTotal = 0;
        bonusScore = 0;
        totalScore = 0;
    }

    /**
     * 특정 카테고리에 점수를 기록합니다.
     * @param category 점수를 기록할 카테고리
     * @param score 기록할 점수
     * @return 성공적으로 기록되었는지 여부 (이미 사용된 카테고리인 경우 false 반환)
     */
    public boolean recordScore(String category, int score) {
        if (categoriesUsed.getOrDefault(category, false)) {
            return false; // 이미 사용된 카테고리
        }
        finalScores.put(category, score);
        categoriesUsed.put(category, true);
        calculateTotalScores(); // 점수 기록 후 총점 다시 계산
        return true;
    }

    /**
     * 모든 점수를 기반으로 Upper Section 총점, 보너스, 최종 총점을 계산합니다.
     */
    public void calculateTotalScores() {
        upperSectionTotal = 0;
        upperSectionTotal += finalScores.getOrDefault(CATEGORY_ACES, 0);
        upperSectionTotal += finalScores.getOrDefault(CATEGORY_TWOS, 0);
        upperSectionTotal += finalScores.getOrDefault(CATEGORY_THREES, 0);
        upperSectionTotal += finalScores.getOrDefault(CATEGORY_FOURS, 0);
        upperSectionTotal += finalScores.getOrDefault(CATEGORY_FIVES, 0);
        upperSectionTotal += finalScores.getOrDefault(CATEGORY_SIXES, 0);

        bonusScore = (upperSectionTotal >= 63) ? 35 : 0;

        totalScore = upperSectionTotal + bonusScore;
        totalScore += finalScores.getOrDefault(CATEGORY_CHOICE, 0);
        totalScore += finalScores.getOrDefault(CATEGORY_4_OF_A_KIND, 0);
        totalScore += finalScores.getOrDefault(CATEGORY_FULL_HOUSE, 0);
        totalScore += finalScores.getOrDefault(CATEGORY_SMALL_STRAIGHT, 0);
        totalScore += finalScores.getOrDefault(CATEGORY_LARGE_STRAIGHT, 0);
        totalScore += finalScores.getOrDefault(CATEGORY_YACHT, 0);
    }

    // Getter 메서드들
    public int getScore(String category) {
        return finalScores.getOrDefault(category, 0);
    }

    public boolean isCategoryUsed(String category) {
        return categoriesUsed.getOrDefault(category, false);
    }

    public int getUpperSectionTotal() {
        return upperSectionTotal;
    }

    public int getBonusScore() {
        return bonusScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public Map<String, Integer> getFinalScores() {
        return finalScores;
    }
}