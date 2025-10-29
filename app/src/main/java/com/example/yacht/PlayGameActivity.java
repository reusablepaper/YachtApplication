package com.example.yacht;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayGameActivity extends AppCompatActivity implements GameManager.OnGameUpdateListener {

    private GameManager gameManager;
    private Button restartButton;
    private Button showRuleButton;
    private Button showTotalScoreButton;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_BUTTON_PRESS_DELAY = 2000;
    private Handler handler = new Handler(Looper.getMainLooper());

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
        gameManager.addListener(this);

        restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(v -> handleRestartButtonClick());

        showRuleButton = findViewById(R.id.showRuleButton);
        showRuleButton.setOnClickListener(v -> showGameRuleDialog());

        showTotalScoreButton = findViewById(R.id.showTotalScoreButton);
        showTotalScoreButton.setOnClickListener(v -> showTotalScoreDialog());


        if (playerNames != null && !playerNames.isEmpty()) {
            gameManager.initializeGame(playerNames);
            addSumScoreFragmentsForPlayers(playerNames);
        } else {
            Log.e("PlayGameActivity", "No player names provided.");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameManager != null) {
            gameManager.removeListener(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "뒤로가기를 한번 더 누르면 게임이 저장되지 않고 종료됩니다.", Toast.LENGTH_SHORT).show();

        handler.postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_BUTTON_PRESS_DELAY);
    }

    // This is the only listener method PlayGameActivity needs to implement.
    @Override
    public void onGameOver() {
        if (restartButton != null) {
            restartButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPlayerTurnChanged(String newPlayerName) {}
    @Override
    public void onDiceRolled(int[] diceValues) {}
    @Override
    public void onPossibleScoresCalculated(Map<String, Integer> scores) {}
    @Override
    public void onScoreRecorded(String playerName, String category, int score) {}

    private void handleRestartButtonClick() {
        // gameManager.getPlayerNames()의 반환 타입 List를 ArrayList로 변환
        ArrayList<String> playerNamesForRestart = new ArrayList<>(gameManager.getPlayerNames());
        gameManager.initializeGame(playerNamesForRestart);
        if (restartButton != null) {
            restartButton.setVisibility(View.GONE);
        }
        recreate();
    }

    private void addSumScoreFragmentsForPlayers(List<String> playerNames) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        LinearLayout sumScoreContainer = findViewById(R.id.sumScoreFragmentContainer);

        List<Fragment> currentFragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : currentFragments) {
            if (fragment instanceof SumScoreFragment) {
                transaction.remove(fragment);
            }
        }
        transaction.commitNow();
        transaction = getSupportFragmentManager().beginTransaction();

        for (String playerName : playerNames) {
            SumScoreFragment sumScoreFragment = SumScoreFragment.newInstance(playerName);
            transaction.add(sumScoreContainer.getId(), sumScoreFragment);
        }
        transaction.commit();
    }
    private void showGameRuleDialog() {
        // --- 수정된 부분: Dialog 초기화 방식 변경 ---
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gamerule);

        Window window = dialog.getWindow();
        if (window != null) {
            // 다이얼로그 배경을 투명하게 설정하여 이미지 외에 다른 요소가 보이지 않도록 함
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView imageView = dialog.findViewById(R.id.gameruleImageView);
        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        // --- 수정된 부분 끝 ---
    }

    /**
     * TotalScoreDialog를 표시합니다.
     * 기존 다이얼로그가 존재하면 hide 상태를 show로 전환하여 재활용합니다.
     */
    private void showTotalScoreDialog() {
        getSupportFragmentManager().executePendingTransactions();

        TotalScoreDialog existingDialog =
                (TotalScoreDialog) getSupportFragmentManager().findFragmentByTag(TotalScoreDialog.TAG);

        if (existingDialog != null) {
            // 기존 인스턴스가 존재하는 경우
            if (existingDialog.isHidden()) {
                // 1. FragmentTransaction으로 프래그먼트를 보이게 처리
                getSupportFragmentManager().beginTransaction()
                        .show(existingDialog)
                        .commit();
            }

            // 2. [핵심 수정]: Dialog 객체의 show()를 명시적으로 호출하여 Window를 다시 표시
            Dialog dialog = existingDialog.getDialog();
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }

            existingDialog.updateAllColumnsData();

        } else {
            // 인스턴스가 없으면 새로 생성하여 show()
            TotalScoreDialog newDialog = TotalScoreDialog.newInstance();
            newDialog.show(getSupportFragmentManager(), TotalScoreDialog.TAG);
        }
    }

}