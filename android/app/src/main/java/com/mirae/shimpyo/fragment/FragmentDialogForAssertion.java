package com.mirae.shimpyo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.activity.ActivityLogin;

public class FragmentDialogForAssertion extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_for_assertion, null);

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setNegativeButton("확인", (d, id) -> {
                    Intent intent = new Intent(getContext(), ActivityLogin.class);
                    this.getDialog().cancel();
                    startActivity(intent);
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }
}