package com.example.yacht;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ScoreBoardActivity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private static final int BACK_BUTTON_PRESS_DELAY = 2000;

    private SafeHandler handler = new SafeHandler(this);

    // --- 추가된 부분: helpButton 변수 ---
    private Button helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        ArrayList<String> playerNames = intent.getStringArrayListExtra("playerNames");

        // --- 수정된 부분: helpButton 초기화 및 리스너 설정 ---
        helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(v -> showGameRuleDialog());

        if (playerNames != null && !playerNames.isEmpty()) {
            LinearLayout scoreFragmentContainer = findViewById(R.id.scoreFragmentContainer);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            for (int i = 0; i < playerNames.size(); i++) {
                String playerName = playerNames.get(i);

                FrameLayout fragmentFrame = new FrameLayout(this);
                fragmentFrame.setId(View.generateViewId());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1.0f
                );
                fragmentFrame.setLayoutParams(params);

                ScoreFragment scoreFragment = ScoreFragment.newInstance(playerName, playerNames.size());
                transaction.add(fragmentFrame.getId(), scoreFragment, "scoreFragment_" + i);

                scoreFragmentContainer.addView(fragmentFrame);
            }
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "뒤로가기를 한번 더 누를 시 저장되지 않고 돌아갑니다.", Toast.LENGTH_SHORT).show();

        handler.postDelayed(() -> doubleBackToExitPressedOnce = false, BACK_BUTTON_PRESS_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * 게임 규칙 이미지를 화면 가득한 팝업으로 표시합니다.
     */
    private void showGameRuleDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        dialog.setContentView(R.layout.dialog_gamerule);


        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());

            window.setAttributes(params);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
        ImageView imageView = dialog.findViewById(R.id.gameruleImageView);
        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    private static class SafeHandler extends Handler {
        private final WeakReference<ScoreBoardActivity> activityRef;
        SafeHandler(ScoreBoardActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
    }
}