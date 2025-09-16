package com.example.yacht;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays; // Arrays.asList 사용을 위해 import

public class ScorePerPlayer {

    // final로 선언하여 한 번 할당된 맵은 변경되지 않도록 합니다.
    private final Map<String, Integer> finalScores; // 확정된 점수들을 저장
    private final Map<String, Boolean> categoriesUsed; // 각 카테고리가 사용되었는지 여부 저장

    // 족보 카테고리를 나타내는 상수들 (ScoreCalculator, GameManager, NowPlayerFragment 등에서 공통으로 사용)
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
        // 생성 시 맵들을 초기화합니다.
        this.finalScores = new HashMap<>();
        this.categoriesUsed = new HashMap<>();
    }

    /**
     * 특정 카테고리에 점수를 기록합니다. 이미 사용된 카테고리인 경우 기록하지 않습니다.
     * @param category 기록할 족보 카테고리 (ScorePerPlayer.CATEGORY_ACES 등 상수 사용)
     * @param score 기록할 점수
     * @return 점수 기록 성공 시 true, 이미 사용된 카테고리인 경우 false
     */
    public boolean recordScore(String category, int score) {
        // 해당 카테고리가 이미 사용되었는지 확인
        if (categoriesUsed.getOrDefault(category, false)) {
            return false; // 이미 사용되었으면 기록하지 않고 false 반환
        }
        finalScores.put(category, score); // 점수 기록
        categoriesUsed.put(category, true); // 해당 카테고리를 사용 완료로 표시
        return true; // 성공적으로 기록했으므로 true 반환
    }

    /**
     * 특정 카테고리의 기록된 점수를 반환합니다. 기록되지 않았다면 0을 반환합니다.
     * @param category 점수를 조회할 족보 카테고리
     * @return 기록된 점수, 없으면 0
     */
    public int getScore(String category) {
        return finalScores.getOrDefault(category, 0);
    }

    /**
     * 특정 카테고리가 이미 사용되어 점수가 기록되었는지 여부를 반환합니다.
     * @param category 확인할 족보 카테고리
     * @return 사용되었으면 true, 아니면 false
     */
    public boolean isCategoryUsed(String category) {
        return categoriesUsed.getOrDefault(category, false);
    }

    /**
     * 상단 섹션 점수(Aces, Twos, Threes, Fours, Fives, Sixes)의 총합을 계산하여 반환합니다.
     * (주사위 눈금으로 계산하는 것이 아니라, finalScores에 기록된 점수들을 합산합니다.)
     * @return 상단 섹션 점수 총합
     */
    public int getUpperSectionTotal() {
        return Arrays.asList(CATEGORY_ACES, CATEGORY_TWOS, CATEGORY_THREES, CATEGORY_FOURS, CATEGORY_FIVES, CATEGORY_SIXES)
                .stream() // 리스트의 각 카테고리에 대해 스트림 처리
                .mapToInt(category -> finalScores.getOrDefault(category, 0)) // 각 카테고리의 기록된 점수를 가져오고, 없으면 0
                .sum(); // 모든 점수를 합산
    }

    /**
     * 상단 섹션 보너스를 계산하여 반환합니다. (상단 총합이 63점 이상이면 35점)
     * @return 상단 섹션 보너스 점수
     */
    public int getUpperSectionBonus() {
        return getUpperSectionTotal() >= 63 ? 35 : 0; // 63점 이상이면 35, 아니면 0
    }

    /**
     * 하단 섹션 점수(Choice, 4 of a Kind, Full House, Small Straight, Large Straight, Yacht)의 총합을 계산하여 반환합니다.
     * (주사위 눈금으로 계산하는 것이 아니라, finalScores에 기록된 점수들을 합산합니다.)
     * @return 하단 섹션 점수 총합
     */
    public int getLowerSectionTotal() {
        return Arrays.asList(CATEGORY_CHOICE, CATEGORY_4_OF_A_KIND, CATEGORY_FULL_HOUSE, CATEGORY_SMALL_STRAIGHT, CATEGORY_LARGE_STRAIGHT, CATEGORY_YACHT)
                .stream() // 리스트의 각 카테고리에 대해 스트림 처리
                .mapToInt(category -> finalScores.getOrDefault(category, 0)) // 각 카테고리의 기록된 점수를 가져오고, 없으면 0
                .sum(); // 모든 점수를 합산
    }

    /**
     * 최종 총점(상단 총합 + 상단 보너스 + 하단 총합)을 계산하여 반환합니다.
     * @return 게임의 최종 총점
     */
    public int getGrandTotal() {
        return getUpperSectionTotal() + getUpperSectionBonus() + getLowerSectionTotal();
    }
}