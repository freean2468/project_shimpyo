package com.mirae.shimpyo.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mirae.shimpyo.R;

public class FragmentDialogForPhoto extends DialogFragment {
    private final int GET_GALLERY_IMAGE = 200;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_dialog_for_photo, null);
        Fragment01 fragment01 = Fragment01.getInstance();
        Button buttonDownload = view.findViewById(R.id.buttonDownload);
        Button buttonDelete = view.findViewById(R.id.buttonDelete);

        if (fragment01.getPhoto().length <= 0) {
            buttonDownload.setEnabled(false);
            buttonDelete.setEnabled(false);
            buttonDownload.setTextColor(getResources().getColor(R.color.text_disabled));
            buttonDelete.setTextColor(getResources().getColor(R.color.text_disabled));
        }

        view.findViewById(R.id.buttonUpload).setOnTouchListener((v, e) -> {
            boolean flag = false;
            if(e.getAction() == MotionEvent.ACTION_UP){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,GET_GALLERY_IMAGE);
                flag = true;
            }
            return flag;
        });

        buttonDownload.setOnTouchListener((v, e) -> {
            boolean flag = false;
            if(e.getAction() == MotionEvent.ACTION_UP) {
                fragment01.saveImageToGallery();
                FragmentDialogForPhoto.this.getDialog().cancel();
                flag = true;
            }
            return flag;
        });

        buttonDelete.setOnTouchListener((v, e) -> {
            boolean flag = false;
            if(e.getAction() == MotionEvent.ACTION_UP) {
                fragment01.setPhoto();
                FragmentDialogForPhoto.this.getDialog().cancel();
                flag = true;
            }
            return flag;
        });

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null)
        {
            Fragment01.getInstance().setPhoto(data.getData());
        }

        FragmentDialogForPhoto.this.getDialog().cancel();
    }
}