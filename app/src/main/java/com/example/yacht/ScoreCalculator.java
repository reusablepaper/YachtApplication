// ScoreCalculator.java
package com.example.yacht;

import java.util.HashMap;
import java.util.Map;

public class ScoreCalculator {

    // 현재 주사위 값 배열을 받아서 특정 카테고리의 점수를 계산하는 메서드들
    public int calculateAces(int[] dice) {
        int score = 0;
        for (int die : dice) {
            if (die == 1) {
                score += 1;
            }
        }
        return score;
    }

    public int calculateYacht(int[] dice) {
        // 모든 주사위 값이 같으면 50점
        if (dice[0] == dice[1] && dice[1] == dice[2] && dice[2] == dice[3] && dice[3] == dice[4]) {
            return 50;
        }
        return 0;
    }

    // TODO: 다른 족보 계산 메서드들 추가 (Twos, Threes, Four of a Kind, Full House 등)

    // 모든 가능한 족보의 점수를 계산하여 Map으로 반환
    public Map<String, Integer> calculateAllScores(int[] dice) {
        Map<String, Integer> possibleScores = new HashMap<>();
        // 예시:
        possibleScores.put(ScorePerPlayer.CATEGORY_ACES, calculateAces(dice));
        possibleScores.put(ScorePerPlayer.CATEGORY_YACHT, calculateYacht(dice));
        // TODO: 다른 모든 카테고리에 대한 계산 결과 추가
        return possibleScores;
    }
}