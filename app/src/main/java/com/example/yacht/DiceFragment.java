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

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class DiceFragment extends Fragment {

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

        gameManager = GameManager.getInstance();

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

        // --- 초기 주사위 이미지를 모두 숨기는 코드 추가 ---
        for (ImageView diceImageView : diceImageViews) {
            diceImageView.setVisibility(View.GONE);
        }
        // --- 코드 추가 끝 ---

        rollDiceButton.setOnClickListener(v -> handleDiceRoll());

        return view;
    }

    private void handleDiceRoll() {
        if (gameManager.getRollCount() >= gameManager.getMaxRollsPerTurn()) {
            Toast.makeText(getContext(), "이번 턴에 더 이상 주사위를 굴릴 수 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        rollDiceButton.setEnabled(false);
        rollDiceButton.setText("굴리는 중...");

        // 모든 주사위 ImageView 숨기기 (이미 숨겨져 있지만 안전을 위해 다시 호출)
        for (ImageView dice : diceImageViews) {
            dice.setVisibility(View.GONE);
        }

        rollingDiceAnimationView.setVisibility(View.VISIBLE);
        Glide.with(this).asGif().load(R.drawable.rolldice).into(rollingDiceAnimationView);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            gameManager.rollDice();

            rollingDiceAnimationView.setVisibility(View.GONE);

            // 주사위 굴림 후 UI 업데이트
            updateDiceUI();

            Toast.makeText(getContext(), "주사위 굴림 횟수: " + gameManager.getRollCount(), Toast.LENGTH_SHORT).show();

            rollDiceButton.setEnabled(true);
            rollDiceButton.setText("굴리기");

            // TODO: nowPlayerFragment의 점수표 업데이트 로직 호출
        }, 3000);
    }

    private void updateDiceUI() {
        int[] diceValues = gameManager.getDiceValues();
        boolean[] heldDiceStatus = gameManager.getHeldDiceStatus();
        String[] diceNames = {"one", "two", "three", "four", "five", "six"};

        for (int i = 0; i < diceImageViews.length; i++) {
            diceImageViews[i].setVisibility(View.VISIBLE);
            int resId;
            // 주사위 값은 1부터 6까지이므로, diceNames 배열의 인덱스(0~5)와 맞추기 위해 -1을 합니다.
            int diceValue = diceValues[i];

            // 주사위 값이 0일 경우 (초기 상태), 이미지를 표시하지 않습니다.
            // 이 로직은 이제 필요 없지만, 혹시 모를 경우를 대비해 둡니다.
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
                diceImageViews[i].setImageResource(resId);
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

        // 주사위가 굴려지지 않은 상태(값이 0)에서는 고정할 수 없습니다.
        if (gameManager.getDiceValues()[index] == 0) {
            return;
        }

        gameManager.toggleDiceHold(index);
        updateDiceUI();
    }
}