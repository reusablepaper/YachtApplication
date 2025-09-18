// Dice.java
package com.example.yacht;

import java.util.Random;

public class Dice {
    private int value; // 주사위 값 (1~6)
    private boolean held; // 고정 여부

    public Dice() {
        this.held = false; // 초기에는 고정 해제
        roll(); // 처음 생성 시 한 번 굴림
    }

    public void roll() {
        if (!held) { // 고정되지 않은 경우에만 굴림
            Random random = new Random();
            this.value = random.nextInt(6) + 1;
        }
    }

    public void toggleHold() {
        this.held = !this.held;
    }

    public void reset() {
        this.held = false;
        this.value = 0; // 또는 초기값 설정
    }

    // --- Getter 메서드 ---
    public int getValue() {
        return value;
    }

    public boolean isHeld() {
        return held;
    }
}