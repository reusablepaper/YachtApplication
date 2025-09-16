package com.example.yacht;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {

    private static GameManager instance;

    public interface GameEndListener {
        void onGameEnd();
    }
    private GameEndListener gameEndListener;

    public void setGameEndListener(GameEndListener listener) {
        this.gameEndListener = listener;
    }

    private List<String> playerNames;
    private int totalPlayers;

    private int currentTurn;
    private final int maxTurns = 12;
    private int rollCount;
    private final int maxRollsPerTurn = 3;

    private List<Dice> diceList;
    private ScoreCalculator scoreCalculator;
    private Map<String, ScorePerPlayer> playerScoresMap;

    private String currentPlayerName;

    private GameManager() {
        this.diceList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            diceList.add(new Dice());
        }
        this.scoreCalculator = new ScoreCalculator();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void initializeGame(ArrayList<String> initialPlayerNames) {
        this.playerNames = initialPlayerNames;
        this.totalPlayers = initialPlayerNames.size();
        this.currentTurn = 1;
        this.rollCount = 0;



        this.playerScoresMap = new HashMap<>();
        for (String playerName : initialPlayerNames) {
            playerScoresMap.put(playerName, new ScorePerPlayer());
        }

        this.currentPlayerName = playerNames.get(0);
    }

    public void rollDice() {
        if (rollCount >= maxRollsPerTurn) {
            return;
        }

        for (Dice dice : diceList) {
            dice.roll();
        }
        rollCount++;
    }

    public void nextPlayer() {
        int currentIdx = playerNames.indexOf(currentPlayerName);
        int nextIdx = (currentIdx + 1) % totalPlayers;
        currentPlayerName = playerNames.get(nextIdx);

        if (nextIdx == 0) {
            currentTurn++;
            if (currentTurn > maxTurns) {
                if (gameEndListener != null) {
                    gameEndListener.onGameEnd();
                }
            }
        }

        for (Dice dice : diceList) {
            dice.reset();
        }
        rollCount = 0;
    }

    public void toggleDiceHold(int index) {
        if (index >= 0 && index < diceList.size()) {
            diceList.get(index).toggleHold();
        }
    }

    public Map<String, Integer> calculatePossibleScores() {
        int[] currentDiceValues = getDiceValues();
        return scoreCalculator.calculateAllScores(currentDiceValues);
    }

    public void recordScore(String playerName, String category, int score) {
        ScorePerPlayer playerScores = playerScoresMap.get(playerName);
        if (playerScores != null) {
            playerScores.recordScore(category, score);
        }
    }

    public List<String> getPlayerNames() {
        return playerNames;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public int getRollCount() {
        return rollCount;
    }

    public int getMaxRollsPerTurn() {
        return maxRollsPerTurn;
    }

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

    public Map<String, ScorePerPlayer> getPlayerScoresMap() {
        return playerScoresMap;
    }

    public String getCurrentPlayerName() {
        return currentPlayerName;
    }

    public ScorePerPlayer getCurrentPlayerScorePerPlayer() {
        return playerScoresMap.get(currentPlayerName);
    }
}