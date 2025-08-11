package com.example.yacht;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PlayerSettingActivity extends AppCompatActivity {

    private Button addPlayerButton;
    private Button nextButton;

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
        addPlayerButton = findViewById(R.id.addPlayerButton);
        nextButton = findViewById(R.id.nextButton);

        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //여기에 플레이어 목록이 추가되는 내용 추가
                //플레이어 정보는 저장 되어 다음 액티비티인 scoreboardactivity 로 전달되어야함
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayerSettingActivity.this, ScoreBoardActivity.class);
                //여기에 플레이어 정보를 넘기는 코드
                startActivity(intent);
            }
        });
    }
}