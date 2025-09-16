package com.example.yacht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SumScoreFragment extends Fragment {

    private static final String ARG_PLAYER_NAME = "player_name";
    private String playerName;

    private TextView playerNameTextView;
    private TextView totalScoreTextView;

    private GameManager gameManager;

    public SumScoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param playerName The name of the player for this score summary.
     * @return A new instance of fragment SumScoreFragment.
     */
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
            updatePlayerTotalScore(); // 초기 점수 업데이트
        }

        return view;
    }

    /**
     * Updates the player's total score displayed in this fragment.
     * This method can be called by the hosting activity or another fragment
     * when the game state changes.
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