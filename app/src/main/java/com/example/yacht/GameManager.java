package com.example.yacht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

    private static GameManager instance;

    // Listener Interface for game events
    public interface OnGameUpdateListener {
        void onPlayerTurnChanged(String newPlayerName);
        void onDiceRolled(int[] diceValues);
        void onPossibleScoresCalculated(Map<String, Integer> scores);
        void onScoreRecorded(String playerName, String category, int score);
        void onGameOver(); // 게임 종료 시 호출될 새로운 리스너 메서드
    }
    private final List<OnGameUpdateListener> listeners = new ArrayList<>();

    public void addListener(OnGameUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(OnGameUpdateListener listener) {
        listeners.remove(listener);
    }

    // Game Information
    private List<String> playerNames;
    private int totalPlayers;

    // Game State
    private int currentTurn;
    private final int maxTurns = 12;
    private int rollCount;
    private final int maxRollsPerTurn = 3;

    // Game Elements
    private final List<Dice> diceList = new ArrayList<>();
    private final ScoreCalculator scoreCalculator = new ScoreCalculator();
    private Map<String, ScorePerPlayer> playerScoresMap;

    private String currentPlayerName;

    // GameManager는 싱글톤 패턴이므로, 생성자는 private입니다.
    private GameManager() {
        for (int i = 0; i < 5; i++) {
            diceList.add(new Dice());
        }
    }

    // GameManager 인스턴스를 얻는 메서드
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // 게임을 초기화하는 메서드
    public void initializeGame(ArrayList<String> initialPlayerNames) {
        this.playerNames = initialPlayerNames;
        this.totalPlayers = initialPlayerNames.size();
        this.currentTurn = 1;
        this.rollCount = 0;

        for (Dice dice : diceList) {
            dice.reset();
        }

        this.playerScoresMap = new HashMap<>();
        for (String playerName : initialPlayerNames) {
            playerScoresMap.put(playerName, new ScorePerPlayer());
        }

        this.currentPlayerName = playerNames.get(0);
    }

    // 주사위를 굴리는 메서드
    public void rollDice() {
        if (rollCount >= maxRollsPerTurn) {
            return;
        }

        for (Dice dice : diceList) {
            dice.roll();
        }
        rollCount++;

        int[] currentDiceValues = getDiceValues();
        Map<String, Integer> possibleScores = scoreCalculator.calculateAllScores(currentDiceValues);

        for (OnGameUpdateListener listener : listeners) {
            listener.onDiceRolled(currentDiceValues);
            listener.onPossibleScoresCalculated(possibleScores);
        }
    }

    // 다음 플레이어로 턴을 넘기는 메서드
    public void nextPlayer() {
        int currentIdx = playerNames.indexOf(currentPlayerName);
        int nextIdx = (currentIdx + 1) % totalPlayers;
        currentPlayerName = playerNames.get(nextIdx);

        if (nextIdx == 0) {
            currentTurn++;
            if (currentTurn > maxTurns) {
                for (OnGameUpdateListener listener : listeners) {
                    listener.onGameOver(); // 게임 종료 이벤트 호출
                }
                return;
            }
        }

        for (Dice dice : diceList) {
            dice.reset();
        }
        rollCount = 0;

        for (OnGameUpdateListener listener : listeners) {
            listener.onPlayerTurnChanged(currentPlayerName);
            listener.onDiceRolled(getDiceValues());
            listener.onPossibleScoresCalculated(new HashMap<>());
        }
    }

    // 특정 주사위를 고정/해제하는 메서드
    public void toggleDiceHold(int index) {
        if (index >= 0 && index < diceList.size()) {
            diceList.get(index).toggleHold();
            for (OnGameUpdateListener listener : listeners) {
                listener.onDiceRolled(getDiceValues());
                listener.onPossibleScoresCalculated(calculatePossibleScores());
            }
        }
    }

    // 현재 주사위 값으로 가능한 모든 족보 점수를 계산하여 반환
    public Map<String, Integer> calculatePossibleScores() {
        return scoreCalculator.calculateAllScores(getDiceValues());
    }

    // 특정 카테고리에 점수를 기록하는 메서드
    public void recordScore(String playerName, String category, int score) {
        ScorePerPlayer playerScores = playerScoresMap.get(playerName);
        if (playerScores != null) {
            playerScores.recordScore(category, score);
            for (OnGameUpdateListener listener : listeners) {
                listener.onScoreRecorded(playerName, category, score);
            }
        }
    }

    // Getters for UI to retrieve state
    public List<String> getPlayerNames() { return playerNames; }
    public int getCurrentTurn() { return currentTurn; }
    public int getRollCount() { return rollCount; }
    public int getMaxRollsPerTurn() { return maxRollsPerTurn; }

    public int[] getDiceValues() {
        int[] values = new int[diceList.size()];
        for (int i = 0; i < diceList.size(); i++) {
            values[i] = diceList.get(i).getValue();
        }
        return values;
    }

    public boolean[] getHeldDiceStatus() {
        boolean[] status = new boolean[diceList.size()];
        for (int i = 0; i < diceList.size(); i++) {
            status[i] = diceList.get(i).isHeld();
        }
        return status;
    }
    public boolean isInitialized() {
        return playerNames != null && currentPlayerName != null;
    }

    public Map<String, ScorePerPlayer> getPlayerScoresMap() { return playerScoresMap; }
    public String getCurrentPlayerName() { return currentPlayerName; }
    public ScorePerPlayer getCurrentPlayerScorePerPlayer() { return playerScoresMap.get(currentPlayerName); }
}