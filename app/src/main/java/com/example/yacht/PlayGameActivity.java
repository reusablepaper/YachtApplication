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
import android.widget.TextView; // TextView import 추가
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
    private ImageView gameOverImageView;
    // --- 추가된 멤버 변수 ---
    private ImageView nextTurnImageView;
    private TextView nextTurnTextView;

    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_BUTTON_PRESS_DELAY = 2000;
    private Handler handler = new Handler(Looper.getMainLooper());
    private static final int GAME_OVER_DISPLAY_TIME = 2000;
    private static final int NEXT_TURN_DISPLAY_TIME = 1500; // 다음 턴 알림 표시 시간 (1.5초)

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

        // UI 참조
        gameOverImageView = findViewById(R.id.gameOverImage);
        restartButton = findViewById(R.id.restartButton);
        showRuleButton = findViewById(R.id.showRuleButton);
        showTotalScoreButton = findViewById(R.id.showTotalScoreButton);

        // --- 새로운 뷰 참조 ---
        nextTurnImageView = findViewById(R.id.nextTurnImage);
        nextTurnTextView = findViewById(R.id.textTurnTextView);

        // 초기 상태 설정
        gameOverImageView.setVisibility(View.GONE);
        nextTurnImageView.setVisibility(View.GONE);
        nextTurnTextView.setVisibility(View.GONE);

        restartButton.setOnClickListener(v -> handleRestartButtonClick());
        showRuleButton.setOnClickListener(v -> showGameRuleDialog());
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

    // GameManager.OnGameUpdateListener 구현

    @Override
    public void onPlayerTurnChanged(String newPlayerName) {
        if (isFinishing() || isDestroyed()) return;

        // 턴 변경 알림 팝업 표시
        showNextTurnOverlay(newPlayerName);
    }

    @Override
    public void onGameOver() {
        if (gameOverImageView != null) {
            gameOverImageView.setVisibility(View.VISIBLE);
        }

        handler.postDelayed(() -> {
            if (gameOverImageView != null) {
                gameOverImageView.setVisibility(View.GONE);
            }
            if (restartButton != null) {
                restartButton.setVisibility(View.VISIBLE);
            }
            showTotalScoreDialog();
        }, GAME_OVER_DISPLAY_TIME);
    }

    @Override
    public void onDiceRolled(int[] diceValues) {}
    @Override
    public void onPossibleScoresCalculated(Map<String, Integer> scores) {}
    @Override
    public void onScoreRecorded(String playerName, String category, int score) {}

    // --- 턴 오버레이 표시 메서드 ---
    private void showNextTurnOverlay(String playerName) {
        // 1. UI 텍스트 설정
        String message = String.format("턴 종료\n%s의 차례", playerName);
        nextTurnTextView.setText(message);

        // 2. 오버레이 표시
        nextTurnImageView.setVisibility(View.VISIBLE);
        nextTurnTextView.setVisibility(View.VISIBLE);

        // 3. 2초 후 제거
        handler.postDelayed(() -> {
            nextTurnImageView.setVisibility(View.GONE);
            nextTurnTextView.setVisibility(View.GONE);
        }, NEXT_TURN_DISPLAY_TIME);
    }
    // --- 턴 오버레이 표시 메서드 끝 ---

    private void handleRestartButtonClick() {
        ArrayList<String> playerNamesForRestart = new ArrayList<>(gameManager.getPlayerNames());
        gameManager.initializeGame(playerNamesForRestart);
        if (restartButton != null) {
            restartButton.setVisibility(View.GONE);
        }
        showTotalScoreButton.setEnabled(false);
        removeTotalScoreDialogBeforeRestart();
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
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_gamerule);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView imageView = dialog.findViewById(R.id.gameruleImageView);
        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private void showTotalScoreDialog() {
        getSupportFragmentManager().executePendingTransactions();

        TotalScoreDialog existingDialog =
                (TotalScoreDialog) getSupportFragmentManager().findFragmentByTag(TotalScoreDialog.TAG);

        if (existingDialog != null) {
            if (existingDialog.isHidden()) {
                getSupportFragmentManager().beginTransaction()
                        .show(existingDialog)
                        .commit();
            }

            Dialog dialog = existingDialog.getDialog();
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }

            existingDialog.updateAllColumnsData();

        } else {
            TotalScoreDialog newDialog = TotalScoreDialog.newInstance();
            newDialog.show(getSupportFragmentManager(), TotalScoreDialog.TAG);
        }
    }
    private void removeTotalScoreDialogBeforeRestart() {
        getSupportFragmentManager().executePendingTransactions();
        TotalScoreDialog dialog = (TotalScoreDialog)
                getSupportFragmentManager().findFragmentByTag(TotalScoreDialog.TAG);
        if (dialog != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(dialog)
                    .commitNowAllowingStateLoss();
            Log.d("PlayGameActivity", "TotalScoreDialog removed before recreate()");
        }
    }

}