package com.example.yacht;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ScoreCalculator {

    // --- 개별 주사위 숫자 합산 족보 (Upper Section) ---

    public int calculateAces(int[] dice) {
        return countAndSum(dice, 1);
    }

    public int calculateTwos(int[] dice) {
        return countAndSum(dice, 2);
    }

    public int calculateThrees(int[] dice) {
        return countAndSum(dice, 3);
    }

    public int calculateFours(int[] dice) {
        return countAndSum(dice, 4);
    }

    public int calculateFives(int[] dice) {
        return countAndSum(dice, 5);
    }

    public int calculateSixes(int[] dice) {
        return countAndSum(dice, 6);
    }

    // 특정 숫자 주사위의 총합을 계산하는 헬퍼 메서드
    private int countAndSum(int[] dice, int targetNumber) {
        int score = 0;
        for (int die : dice) {
            if (die == targetNumber) {
                score += targetNumber;
            }
        }
        return score;
    }

    // --- 기타 족보 (Lower Section) ---

    // Choice: 모든 주사위 눈의 합계
    public int calculateChoice(int[] dice) {
        int sum = 0;
        for (int die : dice) {
            sum += die;
        }
        return sum;
    }

    // 4 of a Kind: 같은 눈의 주사위가 4개 이상일 경우, 모든 주사위 눈의 합계
    public int calculateFourOfAKind(int[] dice) {
        Map<Integer, Integer> counts = getDiceCounts(dice);
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() >= 4) { // 4개 이상 같은 눈이 있으면
                return calculateChoice(dice); // 모든 주사위 눈의 합계가 점수
            }
        }
        return 0; // 조건 불충족 시 0점
    }

    // Full House: 트리플(3개)과 페어(2개)가 동시에 존재할 경우, 모든 주사위 눈의 합계
    public int calculateFullHouse(int[] dice) {
        Map<Integer, Integer> counts = getDiceCounts(dice);

        // 숫자 종류가 2개여야 함 (예: 1,1,1,2,2)
        if (counts.size() == 2) {
            boolean hasTriple = false;
            boolean hasPair = false;
            for (int count : counts.values()) {
                if (count == 3) hasTriple = true;
                if (count == 2) hasPair = true;
            }
            if (hasTriple && hasPair) {
                return calculateChoice(dice); // 모든 주사위 눈의 합계가 점수
            }
        }
        return 0; // 조건 불충족 시 0점
    }

    // Small Straight (4개의 연속된 숫자): 1-2-3-4, 2-3-4-5, 3-4-5-6 이 있으면 30점
    public int calculateSmallStraight(int[] dice) {
        // 중복 제거 및 정렬
        Set<Integer> uniqueDice = new HashSet<>();
        for (int die : dice) {
            uniqueDice.add(die);
        }
        Integer[] sortedUniqueDice = uniqueDice.toArray(new Integer[0]);
        Arrays.sort(sortedUniqueDice);

        // 4개 이상의 고유한 주사위가 있어야 함
        if (sortedUniqueDice.length < 4) {
            return 0;
        }

        // 연속된 숫자가 4개 있는지 확인 (예: {1,2,3,4}, {2,3,4,5}, {3,4,5,6} 패턴)
        for (int i = 0; i <= sortedUniqueDice.length - 4; i++) {
            boolean isStraight = true;
            for (int j = 0; j < 3; j++) {
                if (sortedUniqueDice[i + j + 1] != sortedUniqueDice[i + j] + 1) { // 다음 숫자가 현재 숫자+1이 아니면 연속이 끊김
                    isStraight = false;
                    break;
                }
            }
            if (isStraight) {
                return 15; // 30점
            }
        }
        return 0; // 조건 불충족 시 0점
    }

    // Large Straight (5개의 연속된 숫자): 1-2-3-4-5 또는 2-3-4-5-6 이 있으면 40점
    public int calculateLargeStraight(int[] dice) {
        // 중복 제거 및 정렬 (Large Straight는 5개의 고유한 숫자가 필요하므로 Set으로 중복 제거)
        Set<Integer> uniqueDice = new HashSet<>();
        for (int die : dice) {
            uniqueDice.add(die);
        }

        // 5개의 고유한 주사위가 아니면 라지 스트레이트 아님
        if (uniqueDice.size() != 5) {
            return 0;
        }

        Integer[] sortedUniqueDice = uniqueDice.toArray(new Integer[0]);
        Arrays.sort(sortedUniqueDice);

        // 1-2-3-4-5 또는 2-3-4-5-6인지 확인
        if ((sortedUniqueDice[0] == 1 && sortedUniqueDice[1] == 2 && sortedUniqueDice[2] == 3 && sortedUniqueDice[3] == 4 && sortedUniqueDice[4] == 5) ||
                (sortedUniqueDice[0] == 2 && sortedUniqueDice[1] == 3 && sortedUniqueDice[2] == 4 && sortedUniqueDice[3] == 5 && sortedUniqueDice[4] == 6)) {
            return 30; // 40점
        }
        return 0; // 조건 불충족 시 0점
    }

    // Yacht: 모든 주사위 값이 같으면 50점
    public int calculateYacht(int[] dice) {
        if (dice[0] == dice[1] && dice[1] == dice[2] && dice[2] == dice[3] && dice[3] == dice[4]) {
            return 50;
        }
        return 0; // 조건 불충족 시 0점
    }

    /**
     * 주사위 눈금별 개수를 계산하여 Map으로 반환하는 헬퍼 메서드
     * 예: {1: 3, 2: 2} (주사위 1이 3개, 2가 2개)
     */
    private Map<Integer, Integer> getDiceCounts(int[] dice) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int die : dice) {
            counts.put(die, counts.getOrDefault(die, 0) + 1);
        }
        return counts;
    }

    // 모든 가능한 족보의 점수를 계산하여 Map<String, Integer> 형태로 반환합니다.
    public Map<String, Integer> calculateAllScores(int[] dice) {
        Map<String, Integer> possibleScores = new HashMap<>();

        // Upper Section (상단 섹션)
        possibleScores.put(ScorePerPlayer.CATEGORY_ACES, calculateAces(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_TWOS, calculateTwos(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_THREES, calculateThrees(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_FOURS, calculateFours(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_FIVES, calculateFives(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_SIXES, calculateSixes(dice));

        // Lower Section (하단 섹션)
        possibleScores.put(ScorePerPlayer.CATEGORY_CHOICE, calculateChoice(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_4_OF_A_KIND, calculateFourOfAKind(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_FULL_HOUSE, calculateFullHouse(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_SMALL_STRAIGHT, calculateSmallStraight(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_LARGE_STRAIGHT, calculateLargeStraight(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_YACHT, calculateYacht(dice));

        return possibleScores;
    }
}