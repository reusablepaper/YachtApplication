package com.example.yacht;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialog extends DialogFragment {

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_SCORE = "score";

    private String category;
    private int score;
    private ConfirmListener listener;

    public interface ConfirmListener {
        void onConfirmed(String category, int score);
    }

    public ConfirmDialog() {
        // Required empty public constructor
    }

    public static ConfirmDialog newInstance(String category, int score) {
        ConfirmDialog fragment = new ConfirmDialog();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        args.putInt(ARG_SCORE, score);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ConfirmListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement ConfirmListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
            score = getArguments().getInt(ARG_SCORE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm, container, false);

        TextView textView = view.findViewById(R.id.textView6);
        Button confirmButton = view.findViewById(R.id.confirmButton);
        Button cancleButton = view.findViewById(R.id.cancleButton);

        String confirmMessage = String.format("[%s]를 [%d]점으로\n확정하시겠습니까?", category, score);
        textView.setText(confirmMessage);

        confirmButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmed(category, score);
            }
            dismiss();
        });

        cancleButton.setOnClickListener(v -> dismiss());

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            Window window = getDialog().getWindow();
            if (window != null) {

                window.setLayout(900, 500);
            }
        }
    }
}