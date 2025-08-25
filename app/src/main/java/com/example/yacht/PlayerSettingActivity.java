package com.example.yacht;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class PlayerSettingActivity extends AppCompatActivity {

    private LinearLayout playerListContainer;
    private Button addPlayerButton;
    private Button nextButton;
    private int playerCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player_setting);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        playerListContainer = findViewById(R.id.playerListContainer);
        addPlayerButton = findViewById(R.id.addPlayerButton);
        nextButton = findViewById(R.id.nextButton);

        addPlayerItem();

        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playerCount < 4) {
                    addPlayerItem();
                    updatePlayerUi();
                } else {
                    Toast.makeText(PlayerSettingActivity.this, "플레이어는 최대 4명까지 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> playerNames = new ArrayList<>();
                for (int i = 0; i < playerCount; i++) {
                    View playerItemView = playerListContainer.getChildAt(i);
                    EditText playerNameEditText = playerItemView.findViewById(R.id.playerNameEditText);
                    String name = playerNameEditText.getText().toString().trim();
                    if (name.isEmpty()) {
                        name = "플레이어 " + (i + 1);
                    }
                    playerNames.add(name);
                }

                if (playerNames.isEmpty()) {
                    Toast.makeText(PlayerSettingActivity.this, "최소 한 명의 플레이어가 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(PlayerSettingActivity.this, ScoreBoardActivity.class);
                intent.putStringArrayListExtra("playerNames", playerNames);
                startActivity(intent);
            }
        });
    }

    private void addPlayerItem() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View playerItemView = inflater.inflate(R.layout.player_item, playerListContainer, false);

        Button deleteButton = playerItemView.findViewById(R.id.deletePlayer);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // --- 수정된 부분 시작 ---
                if (playerCount > 1) { // 플레이어가 1명 초과일 때만 삭제 가능
                    playerListContainer.removeView(playerItemView);
                    playerCount--;
                    updatePlayerUi();
                }
                // else 부분에서 Toast 메시지 호출 코드를 삭제했습니다.
                // --- 수정된 부분 끝 ---
            }
        });

        TextView playerLabel = playerItemView.findViewById(R.id.playerLabel);
        playerLabel.setText("P" + (playerCount + 1));

        playerListContainer.addView(playerItemView);
        playerCount++;
    }

    /**
     * 플레이어 수에 따라 UI를 업데이트합니다 (라벨 재정렬, 버튼 가시성)
     */
    private void updatePlayerUi() {
        for (int i = 0; i < playerCount; i++) {
            View playerItemView = playerListContainer.getChildAt(i);
            TextView playerLabel = playerItemView.findViewById(R.id.playerLabel);
            playerLabel.setText("P" + (i + 1));
        }

        if (playerCount < 4) {
            addPlayerButton.setVisibility(View.VISIBLE);
        } else {
            addPlayerButton.setVisibility(View.INVISIBLE);
        }
    }
}