package com.example.yacht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Map;

public class SumScoreFragment extends Fragment implements GameManager.OnGameUpdateListener {

    private static final String ARG_PLAYER_NAME = "player_name";
    private String playerName;

    private TextView playerNameTextView;
    private TextView totalScoreTextView;

    private GameManager gameManager;

    public SumScoreFragment() {
        // Required empty public constructor
    }

    public static SumScoreFragment newInstance(String playerName) {
        SumScoreFragment fragment = new SumScoreFragment();
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
        View view = inflater.inflate(R.layout.fragment_sum_score, container, false);

        playerNameTextView = view.findViewById(R.id.sumPlayerName);
        totalScoreTextView = view.findViewById(R.id.sumPlayerScore);

        if (playerName != null) {
            playerNameTextView.setText(playerName);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // UI가 준비된 후 리스너 등록
        if (gameManager != null) {
            gameManager.addListener(this);
            // 초기 점수 업데이트
            updatePlayerTotalScore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 프래그먼트 소멸 시 리스너 해제
        if (gameManager != null) {
            gameManager.removeListener(this);
        }
    }

    //----------------------------------------------------------------
    // GameManager.OnGameUpdateListener 구현 메서드들
    //----------------------------------------------------------------

    @Override
    public void onPlayerTurnChanged(String newPlayerName) {
        // 모든 플레이어의 총점은 계속 업데이트되므로, 턴 변경 시에도 총점을 갱신합니다.
        updatePlayerTotalScore();
    }

    @Override
    public void onDiceRolled(int[] diceValues) {
        // SumScoreFragment에서는 주사위 굴림 이벤트를 직접 처리하지 않습니다.
    }


    @Override
    public void onPossibleScoresCalculated(Map<String, Integer> scores) {
        // SumScoreFragment에서는 가능한 점수 이벤트를 직접 처리하지 않습니다.
    }

    @Override
    public void onScoreRecorded(String playerName, String category, int score) {
        // 점수가 기록되면 해당 플레이어의 총점을 갱신합니다.
        if (this.playerName.equals(playerName)) {
            updatePlayerTotalScore();
        }
    }

    @Override
    public void onGameOver() {
        // SumScoreFragment에서 게임 종료 처리가 필요하면 여기에 구현합니다.
    }

    /**
     * Updates the player's total score displayed in this fragment.
     */
    public void updatePlayerTotalScore() {
        if (playerName != null && gameManager != null) {
            ScorePerPlayer playerScores = gameManager.getPlayerScoresMap().get(playerName);
            if (playerScores != null) {
                int totalScore = playerScores.getGrandTotal();
                totalScoreTextView.setText(String.valueOf(totalScore));
            }
        }
    }
}