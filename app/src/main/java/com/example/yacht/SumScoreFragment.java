package com.example.yacht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SumScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SumScoreFragment extends Fragment {
    private static final String ARG_PLAYER_NAME = "player_name"; // Bundle 키 정의
    private String playerName;
    private TextView playerNameTextView;
    private TextView totalScoreTextView; // 플레이어의 총점을 표시할 TextView
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SumScoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SumScore.
     */
    // TODO: Rename and change types and number of parameters
    public static SumScoreFragment newInstance(String param1, String param2) {
        SumScoreFragment fragment = new SumScoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    // 새 인스턴스를 생성할 때 플레이어 이름을 전달하는 팩토리 메서드
    public static SumScoreFragment newInstance(String playerName) {
        SumScoreFragment fragment = new SumScoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYER_NAME, playerName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerName = getArguments().getString(ARG_PLAYER_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sum_score, container, false);

        playerNameTextView = view.findViewById(R.id.sumPlayerName); // 예시 ID
        totalScoreTextView = view.findViewById(R.id.sumPlayerScore); // 예시 ID

        if (playerName != null) {
            playerNameTextView.setText(playerName);
        }

        // TODO: GameManager로부터 해당 플레이어의 총점을 가져와서 totalScoreTextView에 설정하는 로직
        // GameManager gameManager = GameManager.getInstance();
        // int totalScore = gameManager.getPlayerScoresMap().get(playerName).getTotalScore(); // getTotalScore()는 ScorePerPlayer에 있어야 함
        // totalScoreTextView.setText(String.valueOf(totalScore));

        return view;
    }

    // TODO: GameManager로부터 점수 업데이트 알림을 받았을 때 UI를 갱신하는 메서드
    public void updatePlayerTotalScore() {
        if (totalScoreTextView != null && playerName != null) {
            GameManager gameManager = GameManager.getInstance();
            ScorePerPlayer playerScores = gameManager.getPlayerScoresMap().get(playerName);
            if (playerScores != null) {
                // getTotalScore() 메서드는 ScorePerPlayer 클래스에 정의되어 있어야 합니다.
                int totalScore = playerScores.getTotalScore();
                totalScoreTextView.setText(String.valueOf(totalScore));
            }
        }
    }
}