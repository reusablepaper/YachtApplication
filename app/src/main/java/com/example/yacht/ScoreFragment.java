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

/**
 * ScoreFragment: 야추(Yacht) 게임의 점수판 기능을 담당하는 Fragment.
 * 사용자의 입력(스코어)을 받아 유효성을 검사하고 총점을 계산합니다.
 */
public class ScoreFragment extends Fragment {

    private static final String ARG_PLAYER_NAME = "playerName";
    private static final String ARG_TOTAL_PLAYERS = "totalPlayers";

    private String playerNameStr;
    private int totalPlayers;

    // 상단 섹션 EditText (Aces ~ Sixes)
    private EditText upperEditText_1;
    private EditText upperEditText_2;
    private EditText upperEditText_3;
    private EditText upperEditText_4;
    private EditText upperEditText_5;
    private EditText upperEditText_6;

    // 상단 섹션 합계 및 보너스 TextView
    private TextView upperTotal;
    private TextView bonus;

    // 하단 섹션 EditText (Choice ~ Yacht)
    private EditText choiceEditText;
    private EditText fourKindEditText;
    private EditText fullHouseEditText;
    private EditText smallStraightEditText;
    private EditText largeStraightEditText;
    private EditText yachtEditText;

    // 최종 총점 TextView
    private TextView totalScore;

    // 플레이어 이름 표시 TextView
    private TextView playerName;

    // 점수 유효성 검사 타입 정의
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

    /**
     * Fragment 인스턴스를 생성하고 초기 인자를 Bundle에 담아 반환합니다.
     */
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

    /**
     * XML 레이아웃의 모든 View 요소들을 초기화하고 참조합니다.
     */
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

    /**
     * 모든 EditText에 TextWatcher 및 Done 액션 리스너를 설정하여 실시간 점수 계산을 준비합니다.
     */
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
            // 키보드의 Done 버튼 클릭 시 키보드를 숨기고 포커스를 해제합니다.
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) requireActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                v.clearFocus();
                return true;
            }
            return false;
        };

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

    /**
     * 각 점수 입력 필드에 포커스 해제(OnFocusChangeListener) 시 유효성 검사 로직을 설정합니다.
     */
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

    /**
     * EditText에 포커스를 잃었을 때(onFocusChange) 유효성 검사를 수행하도록 리스너를 설정합니다.
     */
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

    /**
     * 포커스를 잃었을 때 입력된 값에 대해 유효성 검사를 수행합니다.
     * (선행 0 오류 및 점수 범위 검사 포함)
     */
    private void performValidationOnBlur(EditText editText, ScoreValidationType validationType, int multiplier) {
        String inputText = editText.getText().toString();

        if (inputText.isEmpty()) {
            return;
        }

        // [핵심 수정 부분] 유효하지 않은 숫자 형식 검사 (선행 0 오류 방지)
        // 입력이 "0" 하나가 아니고, 입력이 "0"으로 시작하는 경우 (예: "01", "000")는 잘못된 형식으로 처리
        if (inputText.length() > 1 && inputText.startsWith("0")) {
            showErrorAndClear(editText, "숫자는 '0'으로 시작하는 유효하지 않은 정수 형식입니다.");
            return;
        }

        int score;
        try {
            score = Integer.parseInt(inputText);
        } catch (NumberFormatException e) {
            // 이 오류는 이제 발생하지 않을 가능성이 높지만 안전을 위해 유지
            showErrorAndClear(editText, "유효하지 않은 숫자 형식입니다.");
            return;
        }

        boolean isValid = false;
        String errorMessage = "유효하지 않은 점수입니다.";

        // 점수 범위 및 규칙에 따른 유효성 검사 로직
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
                if (score >= 0 && score <= 28) { // Full House 최대 점수 (5,5,5,6,6 기준 27+5=32, 4,4,4,5,5 기준 22) -> (일반적으로 30 이하)
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

    /**
     * 오류 메시지를 Toast로 표시하고 해당 EditText의 내용을 지웁니다.
     */
    private void showErrorAndClear(EditText editText, String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
        editText.setText("");
    }

    /**
     * 상단 섹션(Aces~Sixes) 점수를 합산하고 보너스를 계산하여 TextView에 반영합니다.
     */
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

    /**
     * 모든 섹션의 점수와 보너스를 합산하여 최종 총점을 계산하고 반영합니다.
     */
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

    /**
     * View (EditText 또는 TextView)에서 텍스트를 가져와 정수(Integer)로 파싱합니다.
     * 값이 없거나 파싱 오류 시 0을 반환합니다.
     */
    private int parseScore(View view) {
        String text = "";
        if (view instanceof EditText) {
            text = ((EditText) view).getText().toString();
        } else if (view instanceof TextView) {
            text = ((TextView) view).getText().toString();
        }
        if (text.isEmpty() || text.equals("-")) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}