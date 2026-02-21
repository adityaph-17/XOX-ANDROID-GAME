package com.example.xoxgame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;

public class HomeActivity extends AppCompatActivity {

    RadioButton radioX, radioO;
    RadioButton radioPvP, radioPvC;
    Button btnStart, btnClose, btnSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        radioX = findViewById(R.id.radioX);
        radioO = findViewById(R.id.radioO);
        radioPvP = findViewById(R.id.radioPvP);
        radioPvC = findViewById(R.id.radioPvC);
        btnStart = findViewById(R.id.btnStart);
        btnClose = findViewById(R.id.btnClose);
        btnSound = findViewById(R.id.btnSound);

        updateSoundButton();

        // 🔊 Sound Toggle
        btnSound.setOnClickListener(v -> {
            SoundManager.isSoundOn = !SoundManager.isSoundOn;
            updateSoundButton();
        });

        // ▶ Start Game
        btnStart.setOnClickListener(v -> {

            String chosenSymbol = radioX.isChecked() ? "X" : "O";
            String gameMode = radioPvC.isChecked() ? "COMPUTER" : "PLAYER";

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("PLAYER_SYMBOL", chosenSymbol);
            intent.putExtra("MODE", gameMode);
            startActivity(intent);
        });

        // ❌ Exit
        btnClose.setOnClickListener(v -> finishAffinity());
    }

    private void updateSoundButton() {
        if (SoundManager.isSoundOn) {
            btnSound.setText("Sound: ON 🔊");
        } else {
            btnSound.setText("Sound: OFF 🔇");
        }
    }
}