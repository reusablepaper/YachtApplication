package com.example.yacht;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class NowPlayerFragment extends Fragment implements GameManager.OnGameUpdateListener {

    private GameManager gameManager;
    private TextView nowPlayerNameTextView;

    private final Map<String, Button> scoreButtons = new HashMap<>();
    private final Map<String, TextView> scoreTextViews = new HashMap<>();

    public NowPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameManager = GameManager.getInstance();

        // Logcat으로 GameManager 초기화 상태 확인
        if (gameManager == null) {
            Log.e("NowPlayerFragment", "GameManager is null in onCreate!");
        } else {
            Log.d("NowPlayerFragment", "GameManager instance found.");
            // 리스너는 onViewCreated에서 등록합니다.
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_player, container, false);

        nowPlayerNameTextView = view.findViewById(R.id.nowPlayerName);
        mapScoreViews(view);

        if (gameManager != null && gameManager.getCurrentPlayerName() != null) {
            nowPlayerNameTextView.setText(gameManager.getCurrentPlayerName());
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (gameManager != null) {
            gameManager.addListener(this);
            // 초기 상태 업데이트
            updateAllScoreViews();
            updateScoreboardTotals();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameManager != null) {
            gameManager.removeListener(this);
        }
    }

    // ... (mapScoreViews 메서드는 그대로)
    private void mapScoreViews(View view) {
        scoreButtons.put(ScorePerPlayer.CATEGORY_ACES, view.findViewById(R.id.upperButton_1));
        scoreButtons.put(ScorePerPlayer.CATEGORY_TWOS, view.findViewById(R.id.upperButton_2));
        scoreButtons.put(ScorePerPlayer.CATEGORY_THREES, view.findViewById(R.id.upperButton_3));
        scoreButtons.put(ScorePerPlayer.CATEGORY_FOURS, view.findViewById(R.id.upperButton_4));
        scoreButtons.put(ScorePerPlayer.CATEGORY_FIVES, view.findViewById(R.id.upperButton_5));
        scoreButtons.put(ScorePerPlayer.CATEGORY_SIXES, view.findViewById(R.id.upperButton_6));

        scoreButtons.put(ScorePerPlayer.CATEGORY_CHOICE, view.findViewById(R.id.choiceButton));
        scoreButtons.put(ScorePerPlayer.CATEGORY_4_OF_A_KIND, view.findViewById(R.id.fourKindButton));
        scoreButtons.put(ScorePerPlayer.CATEGORY_FULL_HOUSE, view.findViewById(R.id.fullHouseButton));
        scoreButtons.put(ScorePerPlayer.CATEGORY_SMALL_STRAIGHT, view.findViewById(R.id.smallStraightButton));
        scoreButtons.put(ScorePerPlayer.CATEGORY_LARGE_STRAIGHT, view.findViewById(R.id.largeStraightButton));
        scoreButtons.put(ScorePerPlayer.CATEGORY_YACHT, view.findViewById(R.id.yachtButton));

        scoreTextViews.put(ScorePerPlayer.CATEGORY_ACES, view.findViewById(R.id.upperScore_1));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_TWOS, view.findViewById(R.id.upperScore_2));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_THREES, view.findViewById(R.id.upperScore_3));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_FOURS, view.findViewById(R.id.upperScore_4));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_FIVES, view.findViewById(R.id.upperScore_5));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_SIXES, view.findViewById(R.id.upperScore_6));

        scoreTextViews.put(ScorePerPlayer.CATEGORY_CHOICE, view.findViewById(R.id.choiceScore));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_4_OF_A_KIND, view.findViewById(R.id.fourKindScore));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_FULL_HOUSE, view.findViewById(R.id.fullHouseScore));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_SMALL_STRAIGHT, view.findViewById(R.id.smallStraightScore));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_LARGE_STRAIGHT, view.findViewById(R.id.largeStraightScore));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_YACHT, view.findViewById(R.id.yachtScore));
    }


    //----------------------------------------------------------------------------------
    // GameManager.OnGameUpdateListener 구현 메서드들
    //----------------------------------------------------------------------------------

    @Override
    public void onPlayerTurnChanged(String newPlayerName) {
        if (isAdded()) {
            nowPlayerNameTextView.setText(newPlayerName);
            updateAllScoreViews();
            updateScoreboardTotals();
        }
    }

    @Override
    public void onDiceRolled(int[] diceValues) {
        // 이 프래그먼트에서는 직접 사용하지 않음
    }

    @Override
    public void onPossibleScoresCalculated(Map<String, Integer> scores) {
        if (isAdded()) {
            updatePossibleScores(scores);
        }
    }

    @Override
    public void onScoreRecorded(String playerName, String category, int score) {
        if (isAdded()) {
            if (gameManager.getCurrentPlayerName().equals(playerName)) {
                updateFinalScore(category, score);
                updateScoreboardTotals();
                disableAllScoreButtons();
            }
        }
    }

    @Override
    public void onGameEnd() {
        // TODO: 게임 종료 처리
    }

    //----------------------------------------------------------------------------------
    // UI 업데이트 로직
    //----------------------------------------------------------------------------------

    private void updateAllScoreViews() {
        ScorePerPlayer currentPlayerScore = gameManager.getCurrentPlayerScorePerPlayer();
        if (currentPlayerScore == null) {
            Log.e("NowPlayerFragment", "currentPlayerScore is null! GameManager not fully initialized.");
            return;
        }

        for (String category : scoreButtons.keySet()) {
            Button button = scoreButtons.get(category);
            TextView textView = scoreTextViews.get(category);

            if (button == null || textView == null) continue;

            if (currentPlayerScore.isCategoryUsed(category)) {
                button.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setText(String.valueOf(currentPlayerScore.getScore(category)));
            } else {
                button.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                // 주사위를 굴리기 전에는 점수가 없으므로 텍스트를 "0"으로 설정
                button.setText("0");
                button.setEnabled(true);
            }
        }
    }

    private void updatePossibleScores(Map<String, Integer> scores) {
        ScorePerPlayer currentPlayerScore = gameManager.getCurrentPlayerScorePerPlayer();
        if (currentPlayerScore == null) {
            Log.e("NowPlayerFragment", "currentPlayerScore is null!");
            return;
        }

        for (Map.Entry<String, Button> entry : scoreButtons.entrySet()) {
            String category = entry.getKey();
            Button button = entry.getValue();

            if (currentPlayerScore.isCategoryUsed(category)) {
                continue;
            }

            if (scores.containsKey(category)) {
                button.setText(String.valueOf(scores.get(category)));
            } else {
                button.setText("0");
            }
            button.setEnabled(true);
        }
    }

    private void updateFinalScore(String category, int score) {
        Button button = scoreButtons.get(category);
        TextView textView = scoreTextViews.get(category);

        if (button != null && textView != null) {
            button.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(String.valueOf(score));
        }
    }

    private void disableAllScoreButtons() {
        for (Button button : scoreButtons.values()) {
            button.setEnabled(false);
        }
    }

    private void updateScoreboardTotals() {
        ScorePerPlayer currentPlayerScore = gameManager.getCurrentPlayerScorePerPlayer();
        if (currentPlayerScore == null) return;

        // TODO: upperTotal2, bunus2, textView3 (총점) 텍스트뷰 참조 및 업데이트 로직 구현
        // 이 메서드는 ScorePerPlayer에 구현된 getTotalScore(), getUpperSectionTotal(), getUpperSectionBonus() 등을 사용해야 합니다.
    }
}