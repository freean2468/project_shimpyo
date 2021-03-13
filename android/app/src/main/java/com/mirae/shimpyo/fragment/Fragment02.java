package com.mirae.shimpyo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.shimpyo.R;

public class Fragment02 extends Fragment {

    private View view;
    private ImageView imageView;
    private final int GET_GALLERY_IMAGE = 200;

    public static Fragment02 instance = null;

    private Fragment02() { }

    public static Fragment02 getInstance(){
        if (instance == null) {
            instance = new Fragment02();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment02, container, false);
        imageView = view.findViewById(R.id.imageViewAnswer);

        view.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
            startActivityForResult(intent,GET_GALLERY_IMAGE);
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==GET_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data!=null && data.getData()!=null)
        {
            Uri selectImage = data.getData();
            imageView.setImageURI(selectImage);

        }
    }
}
