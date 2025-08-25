package com.example.yacht;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScoreFragment extends Fragment {

    private static final String ARG_PLAYER_NAME = "playerName";
    private static final String ARG_TOTAL_PLAYERS = "totalPlayers";

    private String playerNameStr;
    private int totalPlayers;

    private EditText upperEditText_1; // Ace
    private EditText upperEditText_2; // Two
    private EditText upperEditText_3; // Three
    private EditText upperEditText_4; // Four
    private EditText upperEditText_5; // Five
    private EditText upperEditText_6; // Six

    private TextView upperTotal;
    private TextView bonus;

    private EditText choiceEditText;
    private EditText fourKindEditText;
    private EditText fullHouseEditText;
    private EditText smallStraightEditText;
    private EditText largeStraightEditText;
    private EditText yachtEditText;
    private TextView totalScore;

    private TextView playerName;

    private enum ScoreValidationType {
        MULTIPLIER,
        CHOICE,
        FOUR_OF_KIND,
        FULL_HOUSE,
        SMALL_STRAIGHT,
        LARGE_STRAIGHT,
        YACHT
    }

    public ScoreFragment() {
        // Required empty public constructor
    }

    public static ScoreFragment newInstance(String playerName, int totalPlayers) {
        ScoreFragment fragment = new ScoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLAYER_NAME, playerName);
        args.putInt(ARG_TOTAL_PLAYERS, totalPlayers);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerNameStr = getArguments().getString(ARG_PLAYER_NAME);
            totalPlayers = getArguments().getInt(ARG_TOTAL_PLAYERS, 1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, container, false);

        initializeViews(view);
        setupScoreCalculation();
        setupInputValidation();

        if (playerNameStr != null) {
            playerName.setText(playerNameStr);
        }

        return view;
    }

    private void initializeViews(View view) {
        upperEditText_1 = view.findViewById(R.id.upperEditText_1);
        upperEditText_2 = view.findViewById(R.id.upperEditText_2);
        upperEditText_3 = view.findViewById(R.id.upperEditText_3);
        upperEditText_4 = view.findViewById(R.id.upperEditText_4);
        upperEditText_5 = view.findViewById(R.id.upperEditText_5);
        upperEditText_6 = view.findViewById(R.id.upperEditText_6);
        upperTotal = view.findViewById(R.id.upperTotal);
        bonus = view.findViewById(R.id.bunus);
        choiceEditText = view.findViewById(R.id.choiceEditText);
        fourKindEditText = view.findViewById(R.id.fourKindEditText);
        fullHouseEditText = view.findViewById(R.id.fullHouseEditText);
        smallStraightEditText = view.findViewById(R.id.smallStraightEditText);
        largeStraightEditText = view.findViewById(R.id.largeStraightEditText);
        yachtEditText = view.findViewById(R.id.yachtEditText);
        totalScore = view.findViewById(R.id.textView4);
        playerName = view.findViewById(R.id.playerName);
    }

    private void setupScoreCalculation() {
        TextWatcher scoreWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                calculateUpperTotal();
                calculateTotalScore();
            }
        };

        TextView.OnEditorActionListener doneActionListener = (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 키보드 숨기기
                InputMethodManager imm = (InputMethodManager) requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // 포커스 해제
                v.clearFocus();
                return true;
            }
            return false;
        };

        // 대상 EditText를 배열로 관리
        EditText[] editTexts = {
                upperEditText_1, upperEditText_2, upperEditText_3,
                upperEditText_4, upperEditText_5, upperEditText_6,
                choiceEditText, fourKindEditText, fullHouseEditText,
                smallStraightEditText, largeStraightEditText, yachtEditText
        };

        // 공통 리스너 등록
        for (EditText editText : editTexts) {
            editText.addTextChangedListener(scoreWatcher);
            editText.setOnEditorActionListener(doneActionListener);
        }
    }


    private void setupInputValidation() {
        setupValidationForScoreField(upperEditText_1, ScoreValidationType.MULTIPLIER, 1);
        setupValidationForScoreField(upperEditText_2, ScoreValidationType.MULTIPLIER, 2);
        setupValidationForScoreField(upperEditText_3, ScoreValidationType.MULTIPLIER, 3);
        setupValidationForScoreField(upperEditText_4, ScoreValidationType.MULTIPLIER, 4);
        setupValidationForScoreField(upperEditText_5, ScoreValidationType.MULTIPLIER, 5);
        setupValidationForScoreField(upperEditText_6, ScoreValidationType.MULTIPLIER, 6);

        setupValidationForScoreField(choiceEditText, ScoreValidationType.CHOICE);
        setupValidationForScoreField(fourKindEditText, ScoreValidationType.FOUR_OF_KIND);
        setupValidationForScoreField(fullHouseEditText, ScoreValidationType.FULL_HOUSE);
        setupValidationForScoreField(smallStraightEditText, ScoreValidationType.SMALL_STRAIGHT);
        setupValidationForScoreField(largeStraightEditText, ScoreValidationType.LARGE_STRAIGHT);
        setupValidationForScoreField(yachtEditText, ScoreValidationType.YACHT);
    }

    private void setupValidationForScoreField(final EditText editText, final ScoreValidationType validationType) {
        setupValidationForScoreField(editText, validationType, 0);
    }

    private void setupValidationForScoreField(final EditText editText, final ScoreValidationType validationType, final int multiplier) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    performValidationOnBlur(editText, validationType, multiplier);
                }
            }
        });
    }

    private void performValidationOnBlur(EditText editText, ScoreValidationType validationType, int multiplier) {
        String inputText = editText.getText().toString();

        if (inputText.isEmpty()) {
            return;
        }

        int score;
        try {
            score = Integer.parseInt(inputText);
        } catch (NumberFormatException e) {
            showErrorAndClear(editText, "유효하지 않은 숫자 형식입니다.");
            return;
        }

        boolean isValid = false;
        String errorMessage = "유효하지 않은 점수입니다.";

        switch (validationType) {
            case MULTIPLIER:
                if (score % multiplier == 0 && score >= 0 && score <= multiplier * 5) {
                    isValid = true;
                }
                errorMessage = multiplier + "의 배수(0~" + (multiplier * 5) + ")만 입력 가능합니다.";
                break;
            case CHOICE:
            case FOUR_OF_KIND:
                if (score >= 0 && score <= 30) {
                    isValid = true;
                }
                errorMessage = "0에서 30 사이의 값만 입력 가능합니다.";
                break;
            case FULL_HOUSE:
                if (score >= 0 && score <= 28) { // 수정된 Full House 최대 점수
                    isValid = true;
                }
                errorMessage = "0에서 28 사이의 값만 입력 가능합니다.";
                break;
            case SMALL_STRAIGHT:
                if (score == 0 || score == 15) {
                    isValid = true;
                }
                errorMessage = "0 또는 15만 입력 가능합니다.";
                break;
            case LARGE_STRAIGHT:
                if (score == 0 || score == 30) {
                    isValid = true;
                }
                errorMessage = "0 또는 30만 입력 가능합니다.";
                break;
            case YACHT:
                if (score == 0 || score == 50) {
                    isValid = true;
                }
                errorMessage = "0 또는 50만 입력 가능합니다.";
                break;
        }

        if (!isValid) {
            showErrorAndClear(editText, errorMessage);
        }
    }

    private void showErrorAndClear(EditText editText, String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        editText.setText("");
    }

    private void calculateUpperTotal() {
        int sum = 0;
        sum += parseScore(upperEditText_1);
        sum += parseScore(upperEditText_2);
        sum += parseScore(upperEditText_3);
        sum += parseScore(upperEditText_4);
        sum += parseScore(upperEditText_5);
        sum += parseScore(upperEditText_6);
        upperTotal.setText(String.valueOf(sum));

        // 보너스 점수 자동 부여 기능
        if (sum >= 63) {
            bonus.setText("35");
        } else {
            bonus.setText("0");
        }
    }

    private void calculateTotalScore() {
        int totalSum = 0;
        totalSum += parseScore(upperTotal);
        totalSum += parseScore(bonus);
        totalSum += parseScore(choiceEditText);
        totalSum += parseScore(fourKindEditText);
        totalSum += parseScore(fullHouseEditText);
        totalSum += parseScore(smallStraightEditText);
        totalSum += parseScore(largeStraightEditText);
        totalSum += parseScore(yachtEditText);
        totalScore.setText(String.valueOf(totalSum));
    }

    private int parseScore(View view) {
        String text = "";
        if (view instanceof EditText) {
            text = ((EditText) view).getText().toString();
        } else if (view instanceof TextView) {
            text = ((TextView) view).getText().toString();
        }
        if (text.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}