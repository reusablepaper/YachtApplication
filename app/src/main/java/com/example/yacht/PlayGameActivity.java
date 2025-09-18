package com.example.yacht;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
}