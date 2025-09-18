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
    private String mode; // Declare a variable to hold the mode

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

        // Retrieve the mode from the intent
        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

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

                // Check the mode and start the appropriate activity
                if ("createScoreBoard".equals(mode)) {
                    Intent nextIntent = new Intent(PlayerSettingActivity.this, ScoreBoardActivity.class);
                    nextIntent.putStringArrayListExtra("playerNames", playerNames);
                    startActivity(nextIntent);
                } else if ("playGame".equals(mode)) {
                    // Assuming you have a PlayGameActivity class
                    Intent nextIntent = new Intent(PlayerSettingActivity.this, PlayGameActivity.class);
                    nextIntent.putStringArrayListExtra("playerNames", playerNames);
                    startActivity(nextIntent);
                } else {
                    // Handle an unexpected mode, perhaps by showing an error or defaulting
                    Toast.makeText(PlayerSettingActivity.this, "Error: Unknown mode", Toast.LENGTH_SHORT).show();
                }
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
                if (playerCount > 1) {
                    playerListContainer.removeView(playerItemView);
                    playerCount--;
                    updatePlayerUi();
                }
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