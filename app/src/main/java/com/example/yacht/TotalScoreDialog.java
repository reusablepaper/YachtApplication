package com.example.yacht;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class TotalScoreDialog extends DialogFragment {

    private GameManager gameManager;
    private LinearLayout playerColumnsContainer;
    private Button closeButton;
    public static final String TAG = "TotalScoreDialogTag";

    public TotalScoreDialog() {
        // Required empty public constructor
    }

    public static TotalScoreDialog newInstance() {
        return new TotalScoreDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameManager = GameManager.getInstance();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        // setRetainInstance(true); // <--- 수명 주기 문제 방지를 위해 제거합니다.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_total_score, container, false);

        playerColumnsContainer = view.findViewById(R.id.playerColumnsContainer);
        closeButton = view.findViewById(R.id.closeButton);

        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setCancelable(false);
        }

        closeButton.setOnClickListener(v -> {
            if (isAdded()) {
                // PlayGameActivity에서 hide/show 로직을 사용하므로 hide를 유지합니다.
                requireFragmentManager().beginTransaction().hide(this).commit();
                if (getDialog() != null) {
                    getDialog().hide();
                }
            }
        });

        // --- 핵심 수정: savedInstanceState == null 체크 ---
        // 프래그먼트가 처음 생성될 때만 자식 프래그먼트들을 추가합니다.
        // (화면 회전 등 재생성 시 기존의 자식 프래그먼트들을 재사용하도록 합니다.)
        if (savedInstanceState == null) {
            displayScoreboardLayout();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // setRetainInstance(true)를 제거했으므로, 이 부분을 간단히 정리합니다.
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();

        // 다이얼로그 크기 조정
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
            window.setLayout(width, height);
            // hide() 후 show() 시 다이얼로그 윈도우가 다시 보이도록 설정
            window.getDecorView().setVisibility(View.VISIBLE);
        }

        // 데이터 갱신 (다이얼로그가 보일 때마다 최신 데이터로 갱신)
        updateAllColumnsData();
    }

    /**
     * 각 플레이어별 FrameLayout을 동적으로 생성하고 ScoreColumnFragment를 추가.
     */
    private void displayScoreboardLayout() {
        if (gameManager == null || !gameManager.isInitialized()) {
            return;
        }

        List<String> playerNames = gameManager.getPlayerNames();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        // 기존 프래그먼트 제거 (안전한 초기화를 위해)
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof ScoreColumnFragment) {
                transaction.remove(fragment);
            }
        }
        transaction.commitNow();

        transaction = getChildFragmentManager().beginTransaction();
        playerColumnsContainer.removeAllViews();

        // 플레이어별 동적 추가
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);

            // (1) 각 플레이어용 FrameLayout 생성
            FrameLayout frameLayout = new FrameLayout(requireContext());
            frameLayout.setId(View.generateViewId());

            // (2) 균등한 width 비율 설정 (layout_weight = 1)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            frameLayout.setLayoutParams(params);

            // (3) LinearLayout에 추가
            playerColumnsContainer.addView(frameLayout);

            // (4) FrameLayout 안에 프래그먼트 추가
            ScoreColumnFragment scoreColumnFragment = ScoreColumnFragment.newInstance(playerName);
            transaction.add(frameLayout.getId(), scoreColumnFragment, "ScoreColumn_" + playerName);
        }

        transaction.commit();
    }

    /**
     * 다이얼로그가 다시 보일 때 각 ScoreColumnFragment의 점수 데이터 갱신
     */
    public void updateAllColumnsData() {
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment instanceof ScoreColumnFragment) {
                ((ScoreColumnFragment) fragment).updateScoreboard();
            }
        }
    }
}