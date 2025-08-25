package com.example.yacht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ScoreTypeFragment extends Fragment {

    public ScoreTypeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // fragment_score_type.xml 레이아웃을 로드합니다.
        return inflater.inflate(R.layout.fragment_score_type, container, false);
    }
}