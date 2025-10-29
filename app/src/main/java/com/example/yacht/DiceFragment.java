package com.example.yacht;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Map;

public class DiceFragment extends Fragment implements GameManager.OnGameUpdateListener { // OnGameUpdateListener 구현

    private ImageView[] diceImageViews = new ImageView[5];
    private Button rollDiceButton;
    private LinearLayout diceContainer;
    private ImageView rollingDiceAnimationView;

    private GameManager gameManager;

    public DiceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dice, container, false);

        rollDiceButton = view.findViewById(R.id.roll_dice_button);
        diceImageViews[0] = view.findViewById(R.id.dice_1);
        diceImageViews[1] = view.findViewById(R.id.dice_2);
        diceImageViews[2] = view.findViewById(R.id.dice_3);
        diceImageViews[3] = view.findViewById(R.id.dice_4);
        diceImageViews[4] = view.findViewById(R.id.dice_5);
        diceContainer = view.findViewById(R.id.dice_image_container);

        rollingDiceAnimationView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rollingDiceAnimationView.setLayoutParams(params);
        rollingDiceAnimationView.setVisibility(View.GONE);
        diceContainer.addView(rollingDiceAnimationView, 0);

        // 초기 주사위 이미지들을 모두 숨김
        for (ImageView diceImageView : diceImageViews) {
            diceImageView.setVisibility(View.GONE);
        }

        // 굴리기 버튼 클릭 리스너 설정
        rollDiceButton.setOnClickListener(v -> handleDiceRoll());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameManager = GameManager.getInstance();
        if (gameManager != null && gameManager.isInitialized()) {
            gameManager.addListener(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameManager != null) {
            gameManager.removeListener(this);
        }
    }

    // GameManager.OnGameUpdateListener 구현
    @Override
    public void onPlayerTurnChanged(String newPlayerName) {
        if (isAdded()) {
            // 새 턴이 시작되면 모든 주사위와 고정 상태를 초기화
            for (ImageView diceImageView : diceImageViews) {
                diceImageView.setVisibility(View.GONE);
            }
            // rollDiceButton도 초기 상태로
            rollDiceButton.setText("굴리기");
            rollDiceButton.setEnabled(true);
        }
    }

    @Override
    public void onDiceRolled(int[] diceValues) {
        if (isAdded()) {
            updateDiceUI();
        }
    }

    @Override
    public void onPossibleScoresCalculated(Map<String, Integer> scores) {}

    @Override
    public void onScoreRecorded(String playerName, String category, int score) {}

    @Override
    public void onGameOver() {}

    private void handleDiceRoll() {
        if (gameManager.getRollCount() >= gameManager.getMaxRollsPerTurn()) {
            Toast.makeText(getContext(), "이번 턴에 더 이상 주사위를 굴릴 수 없습니다.\n 점수를 확정해 주세요", Toast.LENGTH_SHORT).show();
            return;

        }
        if (gameManager.areAllDiceHeld() && gameManager.getRollCount() > 0) {
            Toast.makeText(getContext(), "굴릴 주사위가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        rollDiceButton.setEnabled(false);
        rollDiceButton.setText("굴리는 중...");

        for (ImageView dice : diceImageViews) {
            dice.setVisibility(View.GONE);
        }

        rollingDiceAnimationView.setVisibility(View.VISIBLE);

        Glide.with(this)
                .asGif()
                .load(R.drawable.rolldice)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(rollingDiceAnimationView);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            gameManager.rollDice();
            rollingDiceAnimationView.setVisibility(View.GONE);
            Toast.makeText(getContext(), "주사위 굴림 횟수: " + gameManager.getRollCount(), Toast.LENGTH_SHORT).show();
            rollDiceButton.setEnabled(true);
            rollDiceButton.setText("굴리기");
        }, 2000);
    }

    private void updateDiceUI() {
        int[] diceValues = gameManager.getDiceValues();
        boolean[] heldDiceStatus = gameManager.getHeldDiceStatus();
        String[] diceNames = {"one", "two", "three", "four", "five", "six"};

        for (int i = 0; i < diceImageViews.length; i++) {
            diceImageViews[i].setVisibility(View.VISIBLE);
            int resId;
            int diceValue = diceValues[i];

            if (diceValue == 0) {
                diceImageViews[i].setVisibility(View.GONE);
                continue;
            }

            String diceName = diceNames[diceValue - 1];

            if (heldDiceStatus[i]) {
                resId = getResources().getIdentifier(diceName + "_keep", "drawable", requireContext().getPackageName());
            } else {
                resId = getResources().getIdentifier(diceName, "drawable", requireContext().getPackageName());
            }

            if (resId != 0) {
                Glide.with(this).load(resId).into(diceImageViews[i]);
            } else {
                diceImageViews[i].setImageResource(R.drawable.one);
            }

            final int diceIndex = i;
            diceImageViews[i].setOnClickListener(v -> toggleDiceHold(diceIndex));
        }
    }

    private void toggleDiceHold(int index) {
        if (!rollDiceButton.isEnabled()) {
            Toast.makeText(getContext(), "주사위를 굴리는 중에는 고정할 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gameManager.getDiceValues()[index] == 0) {
            return;
        }

        gameManager.toggleDiceHold(index);
        updateDiceUI();
    }
}