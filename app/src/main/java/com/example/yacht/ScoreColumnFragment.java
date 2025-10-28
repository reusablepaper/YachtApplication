package com.example.yacht;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

public class ScoreColumnFragment extends Fragment {

    private static final String ARG_PLAYER_NAME = "player_name";
    private String playerName;

    private GameManager gameManager;

    private TextView playerNameTextView;
    // 족보 카테고리 이름과 UI TextView를 매핑하는 맵
    private final Map<String, TextView> scoreTextViews = new HashMap<>();
    private TextView upperTotalTextView;
    private TextView bonusTextView;
    private TextView grandTotalTextView;

    public ScoreColumnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ScoreColumnFragment newInstance(String playerName) {
        ScoreColumnFragment fragment = new ScoreColumnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYER_NAME, playerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameManager = GameManager.getInstance();
        if (getArguments() != null) {
            playerName = getArguments().getString(ARG_PLAYER_NAME);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 레이아웃 인플레이트
        View view = inflater.inflate(R.layout.dialog_score_column, container, false);

        // 뷰 참조 및 매핑
        playerNameTextView = view.findViewById(R.id.totalScorePlayerName);
        upperTotalTextView = view.findViewById(R.id.score_upperTotal);
        bonusTextView = view.findViewById(R.id.score_bonus);
        grandTotalTextView = view.findViewById(R.id.score_grandTotal);

        mapScoreTextViews(view);

        // 초기 플레이어 이름 및 점수 업데이트 (onCreateView에서 최초 로드)
        if (playerName != null) {
            updateScoreboard();
        }

        return view;
    }

    /**
     * XML의 TextView들을 맵에 매핑합니다.
     */
    private void mapScoreTextViews(View view) {
        // Upper Section
        scoreTextViews.put(ScorePerPlayer.CATEGORY_ACES, view.findViewById(R.id.score_aces));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_TWOS, view.findViewById(R.id.score_twos));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_THREES, view.findViewById(R.id.score_threes));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_FOURS, view.findViewById(R.id.score_fours));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_FIVES, view.findViewById(R.id.score_fives));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_SIXES, view.findViewById(R.id.score_sixes));

        // Lower Section
        scoreTextViews.put(ScorePerPlayer.CATEGORY_CHOICE, view.findViewById(R.id.score_choice));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_4_OF_A_KIND, view.findViewById(R.id.score_fourKind));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_FULL_HOUSE, view.findViewById(R.id.score_fullHouse));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_SMALL_STRAIGHT, view.findViewById(R.id.score_smallStraight));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_LARGE_STRAIGHT, view.findViewById(R.id.score_largeStraight));
        scoreTextViews.put(ScorePerPlayer.CATEGORY_YACHT, view.findViewById(R.id.score_yacht));
    }

    /**
     * GameManager에서 해당 플레이어의 점수를 가져와 UI를 업데이트합니다.
     * 이 메서드는 TotalScoreDialog가 열릴 때 호출되어 최신 점수를 표시합니다.
     */
    public void updateScoreboard() {
        ScorePerPlayer scores = gameManager.getPlayerScoresMap().get(playerName);
        if (scores == null) {
            Log.e("ScoreColumnFragment", "ScorePerPlayer object for " + playerName + " not found.");
            return;
        }

        playerNameTextView.setText(playerName);

        // 각 카테고리 점수 업데이트
        for (String category : scoreTextViews.keySet()) {
            TextView scoreTextView = scoreTextViews.get(category);
            int score = scores.getScore(category);

            if (scoreTextView != null) {
                if (scores.isCategoryUsed(category)) {
                    scoreTextView.setText(String.valueOf(score));
                } else {
                    scoreTextView.setText("-"); // 사용되지 않은 카테고리는 하이픈으로 표시
                }
            }
        }

        // 총점, 보너스, 최종 총점 업데이트
        upperTotalTextView.setText(String.valueOf(scores.getUpperSectionTotal()));
        bonusTextView.setText(String.valueOf(scores.getUpperSectionBonus()));
        grandTotalTextView.setText(String.valueOf(scores.getGrandTotal()));
    }
}