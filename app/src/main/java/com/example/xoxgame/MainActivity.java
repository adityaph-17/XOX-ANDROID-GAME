package com.example.xoxgame;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Button[] buttons = new Button[9];
    boolean playerX = true;
    boolean isComputerMode = false;
    int moveCount = 0;

    String firstPlayerSymbol = "X";

    TextView tvStatus, tvScoreX, tvScoreO;
    Button btnReset, btnBack, btnClose;

    int scoreX = 0, scoreO = 0;

    // 🔊 Sound Players

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firstPlayerSymbol = getIntent().getStringExtra("PLAYER_SYMBOL");
        if (firstPlayerSymbol == null) firstPlayerSymbol = "X";

        String mode = getIntent().getStringExtra("MODE");
        if (mode != null && mode.equals("COMPUTER")) {
            isComputerMode = true;
        }

        tvStatus = findViewById(R.id.tvStatus);
        tvScoreX = findViewById(R.id.tvScoreX);
        tvScoreO = findViewById(R.id.tvScoreO);
        btnReset = findViewById(R.id.btnReset);
        btnBack = findViewById(R.id.btnBack);
        btnClose = findViewById(R.id.btnClose);

        tvStatus.setText("Player " + firstPlayerSymbol + " Turn");
        if (isComputerMode) {
            tvScoreX.setText("Player: " + scoreX);
            tvScoreO.setText("Computer: " + scoreO);
        } else {
            tvScoreX.setText("X: " + scoreX);
            tvScoreO.setText("O: " + scoreO);
        }

        for (int i = 0; i < 9; i++) {
            String buttonID = "b" + i;
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            buttons[i] = findViewById(resID);

            final int index = i;
            buttons[i].setOnClickListener(v -> handleClick(index));
        }

        btnReset.setOnClickListener(v -> resetGame());
        btnBack.setOnClickListener(v -> finish());
        btnClose.setOnClickListener(v -> finishAffinity());
    }

    void handleClick(int index) {

        if (!buttons[index].getText().toString().equals("")) return;

        SoundManager.playSound(this, R.raw.click);

        String currentSymbol = playerX ? firstPlayerSymbol :
                (firstPlayerSymbol.equals("X") ? "O" : "X");

        buttons[index].setText(currentSymbol);
        moveCount++;

        if (checkWinner()) {
            finishGame(currentSymbol);
            return;
        }

        if (moveCount == 9) {
            SoundManager.playSound(this, R.raw.loss);
            Toast.makeText(this, "Draw Game!", Toast.LENGTH_LONG).show();
            tvStatus.setText("Draw Game!");
            return;
        }

        playerX = !playerX;

        if (isComputerMode && !playerX) {
            computerMove();
        } else {
            String nextPlayer = playerX ? firstPlayerSymbol :
                    (firstPlayerSymbol.equals("X") ? "O" : "X");
            tvStatus.setText("Player " + nextPlayer + " Turn");
        }
    }

    void computerMove() {

        String computerSymbol = firstPlayerSymbol.equals("X") ? "O" : "X";
        String playerSymbol = firstPlayerSymbol;

        int move = findWinningMove(computerSymbol);

        if (move == -1) {
            move = findWinningMove(playerSymbol);
        }

        if (move == -1) {
            move = getRandomMove();
        }

        if (move != -1) {
            buttons[move].setText(computerSymbol);
            moveCount++;

            // 🔊 Click sound for computer move
            SoundManager.playSound(this, R.raw.click);
        }

        if (checkWinner()) {
            finishGame(computerSymbol);
            return;
        }

        if (moveCount == 9) {
            SoundManager.playSound(this, R.raw.loss);
            Toast.makeText(this, "Draw Game!", Toast.LENGTH_LONG).show();
            tvStatus.setText("Draw Game!");
            return;
        }

        playerX = true;
        tvStatus.setText("Player " + firstPlayerSymbol + " Turn");
    }

    void finishGame(String winnerSymbol) {

        highlightWinningRow();

        String computerSymbol = firstPlayerSymbol.equals("X") ? "O" : "X";
        String winnerText;

        if (isComputerMode) {

            if (winnerSymbol.equals(computerSymbol)) {
                winnerText = "Computer Wins!";
                scoreO++;
            } else {
                winnerText = "Player Wins!";
                scoreX++;
            }

        } else {
            winnerText = winnerSymbol + " Wins!";

            if (winnerSymbol.equals("X")) scoreX++;
            else scoreO++;
        }

        if (isComputerMode) {
            tvScoreX.setText("Player: " + scoreX);
            tvScoreO.setText("Computer: " + scoreO);
        } else {
            tvScoreX.setText("X: " + scoreX);
            tvScoreO.setText("O: " + scoreO);
        }

        // 🔊 Win Sound
        SoundManager.playSound(this, R.raw.win);
        Toast.makeText(this, winnerText, Toast.LENGTH_LONG).show();
        tvStatus.setText(winnerText);

        disableButtons();
    }

    int findWinningMove(String symbol) {

        int[][] winPositions = {
                {0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}
        };

        for (int[] pos : winPositions) {

            int count = 0;
            int emptyIndex = -1;

            for (int i : pos) {
                String text = buttons[i].getText().toString();

                if (text.equals(symbol)) count++;
                else if (text.equals("")) emptyIndex = i;
            }

            if (count == 2 && emptyIndex != -1) {
                return emptyIndex;
            }
        }

        return -1;
    }

    int getRandomMove() {

        ArrayList<Integer> empty = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            if (buttons[i].getText().toString().equals("")) {
                empty.add(i);
            }
        }

        if (!empty.isEmpty()) {
            return empty.get(new Random().nextInt(empty.size()));
        }

        return -1;
    }

    boolean checkWinner() {
        return check(0,1,2) || check(3,4,5) || check(6,7,8)
                || check(0,3,6) || check(1,4,7) || check(2,5,8)
                || check(0,4,8) || check(2,4,6);
    }

    boolean check(int a, int b, int c) {
        String textA = buttons[a].getText().toString();
        String textB = buttons[b].getText().toString();
        String textC = buttons[c].getText().toString();

        return !textA.equals("") && textA.equals(textB) && textB.equals(textC);
    }

    void highlightWinningRow() {

        int[][] winPositions = {
                {0,1,2},{3,4,5},{6,7,8},
                {0,3,6},{1,4,7},{2,5,8},
                {0,4,8},{2,4,6}
        };

        for (int[] pos : winPositions) {
            String a = buttons[pos[0]].getText().toString();
            String b = buttons[pos[1]].getText().toString();
            String c = buttons[pos[2]].getText().toString();

            if (!a.equals("") && a.equals(b) && b.equals(c)) {
                buttons[pos[0]].setBackgroundColor(
                        getResources().getColor(android.R.color.holo_green_light));
                buttons[pos[1]].setBackgroundColor(
                        getResources().getColor(android.R.color.holo_green_light));
                buttons[pos[2]].setBackgroundColor(
                        getResources().getColor(android.R.color.holo_green_light));
                break;
            }
        }
    }

    void disableButtons() {
        for (Button btn : buttons) btn.setEnabled(false);
    }

    void resetGame() {
        for (Button btn : buttons) {
            btn.setText("");
            btn.setEnabled(true);
            btn.setBackgroundColor(Color.parseColor("#ff6750a4"));
        }
        moveCount = 0;
        playerX = true;
        tvStatus.setText("Player " + firstPlayerSymbol + " Turn");
    }

}