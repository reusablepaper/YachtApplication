package com.example.yacht;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class NowPlayerFragment extends Fragment implements GameManager.OnGameUpdateListener, ConfirmDialog.ConfirmListener {

    private GameManager gameManager;
    private TextView nowPlayerNameTextView;

    private final Map<String, Button> scoreButtons = new HashMap<>();
    private final Map<String, TextView> scoreTextViews = new HashMap<>();

    // 주사위 굴림 후 계산된 점수들을 임시 저장
    private Map<String, Integer> currentPossibleScores = new HashMap<>();

    // 총점 관련 TextView들
    private TextView upperTotalTextView;
    private TextView bonusTextView;
    private TextView grandTotalTextView;

    public NowPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now_player, container, false);

        // 뷰 요소 참조
        nowPlayerNameTextView = view.findViewById(R.id.nowPlayerName);
        upperTotalTextView = view.findViewById(R.id.upperTotal2);
        bonusTextView = view.findViewById(R.id.bunus2);
        grandTotalTextView = view.findViewById(R.id.textView3);

        mapScoreViews(view);
        setScoreButtonListeners();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gameManager = GameManager.getInstance();

        if (gameManager != null && gameManager.isInitialized()) {
            gameManager.addListener(this);
            // 초기 UI 상태 업데이트
            nowPlayerNameTextView.setText(gameManager.getCurrentPlayerName());
            updateAllScoreViews();
            updateScoreboardTotals();
        } else {
            Log.e("NowPlayerFragment", "GameManager is not initialized!");
            // GameManager가 초기화되지 않았을 경우, 액티비티를 종료하거나 오류 메시지를 표시
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameManager != null) {
            gameManager.removeListener(this);
        }
    }

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

    private void setScoreButtonListeners() {
        for (Map.Entry<String, Button> entry : scoreButtons.entrySet()) {
            String category = entry.getKey();
            Button button = entry.getValue();
            button.setOnClickListener(v -> onScoreButtonClick(category));
        }
    }

    private void onScoreButtonClick(String category) {
        if (gameManager.getRollCount() == 0) {
            Toast.makeText(getContext(), "주사위를 먼저 굴려주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gameManager.getCurrentPlayerScorePerPlayer().isCategoryUsed(category)) {
            Toast.makeText(getContext(), "이미 사용된 족보입니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        int score = currentPossibleScores.getOrDefault(category, 0);

        ConfirmDialog dialog = ConfirmDialog.newInstance(category, score);
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "ConfirmDialog");
    }

    //----------------------------------------------------------------------------------
    // ConfirmFragment.ConfirmListener implementation
    //----------------------------------------------------------------------------------
    @Override
    public void onConfirmed(String category, int score) {
        gameManager.recordScore(gameManager.getCurrentPlayerName(), category, score);
        gameManager.nextPlayer();
    }


    //----------------------------------------------------------------------------------
    // GameManager.OnGameUpdateListener implementation
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
        // Not used directly in this fragment
    }

    @Override
    public void onPossibleScoresCalculated(Map<String, Integer> scores) {
        if (isAdded()) {
            currentPossibleScores = scores;
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
    public void onGameOver() {
        // TODO: Handle game over
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
                button.setText("0");
                button.setEnabled(true);
            }
        }
    }

    private void updatePossibleScores(Map<String, Integer> scores) {
        ScorePerPlayer currentPlayerScore = gameManager.getCurrentPlayerScorePerPlayer();
        if (currentPlayerScore == null) return;

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

        upperTotalTextView.setText(String.valueOf(currentPlayerScore.getUpperSectionTotal()));
        bonusTextView.setText(String.valueOf(currentPlayerScore.getUpperSectionBonus()));
        grandTotalTextView.setText(String.valueOf(currentPlayerScore.getGrandTotal()));
    }
}