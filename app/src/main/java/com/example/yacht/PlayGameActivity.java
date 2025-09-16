package com.example.yacht;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class PlayGameActivity extends AppCompatActivity {

    private GameManager gameManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        ArrayList<String> playerNames = null;
        if (intent != null) {
            playerNames = intent.getStringArrayListExtra("playerNames");
        }

        gameManager = GameManager.getInstance();

        if (playerNames != null && !playerNames.isEmpty()) {
            gameManager.initializeGame(playerNames);

            // --- 초기 주사위 값을 0으로 설정 (숨겨진 상태) ---
            // 모든 주사위를 숨긴 상태로 시작하므로, 초기값은 0을 유지하도록 GameManager에서 처리.
            // DiceFragment가 View.GONE 처리하므로 여기서 특별히 rollDice()를 호출하지 않습니다.
            // gameManager.rollDice(); // <-- 이전에 추가했던 rollDice() 호출은 이제 필요 없습니다.
            // ----------------------------------------------

            // 플레이어 수에 따라 SumScoreFragment 동적 추가
            addSumScoreFragmentsForPlayers(playerNames);

        } else {
            // 플레이어 이름이 없으면 액티비티 종료
            finish();
        }

        // TODO: 여기에 다른 버튼 리스너 (showRuleButton, showTotalScoreButton) 설정
    }

    private void addSumScoreFragmentsForPlayers(List<String> playerNames) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // sumScoreFragmentContainer의 ID를 사용하여 프래그먼트를 추가합니다.
        // 이 컨테이너는 activity_play_game.xml에 정의된 LinearLayout입니다.
        int containerId = R.id.sumScoreFragmentContainer;

        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            SumScoreFragment sumScoreFragment = SumScoreFragment.newInstance(playerName);

            // 각 프래그먼트를 고유한 태그로 추가하여 나중에 찾을 수 있도록 합니다.
            // 여기서는 replace 대신 add를 사용하여 여러 프래그먼트를 나란히 배치합니다.
            transaction.add(containerId, sumScoreFragment, "sum_score_fragment_" + i);
        }
        transaction.commit(); // 모든 트랜잭션 커밋
    }
}