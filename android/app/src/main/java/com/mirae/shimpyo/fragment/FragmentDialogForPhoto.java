package com.mirae.shimpyo.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mirae.shimpyo.R;

public class FragmentDialogForPhoto extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_for_photo, null);

        view.findViewById(R.id.buttonUpload).setOnClickListener(v -> {
            Log.d("debug", "upload");
            FragmentDialogForPhoto.this.getDialog().cancel();
        });

        view.findViewById(R.id.buttonDownload).setOnClickListener(v -> {
            Log.d("debug", "download");
            FragmentDialogForPhoto.this.getDialog().cancel();
        });

        view.findViewById(R.id.buttonDelete).setOnClickListener(v -> {
            Log.d("debug", "delete");
            FragmentDialogForPhoto.this.getDialog().cancel();
        });

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }
}