package com.mirae.shimpyo.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.object.ObjectVolley;
import com.mirae.shimpyo.util.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Fragment01 extends Fragment {
    private View view;
    private String no;
    private int dayOfYear;
    private String answer;
    private byte[] photo;

    private ImageView imageViewPhoto;

    public static Fragment01 instance = null;

    private Fragment01() { }

    public static Fragment01 getInstance(){
        if (instance == null) {
            instance = new Fragment01();
        }
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment01, container, false);

        EditText editTextAnswer = view.findViewById(R.id.editTextAnswer);
        Button buttonAnswer = view.findViewById(R.id.buttonAnswer);

        buttonAnswer.setOnClickListener((v) -> {
            ObjectVolley.getInstance(v.getContext()).requestAnswer(
                    no,
                    dayOfYear,
                    editTextAnswer.getText().toString(),
                    photo,
                    new ObjectVolley.RequestAnswerListener() {
                        @Override
                        public void jobToDo() {
                            Log.e(getString(R.string.tag_server), "응답 성공");
                        }
                    },
                    new ObjectVolley.StandardErrorListener(){}
            );
        });

        imageViewPhoto = view.findViewById(R.id.imageViewPhoto);

        imageViewPhoto.setOnClickListener(v -> {
            FragmentDialogForPhoto dialog = new FragmentDialogForPhoto();
            dialog.show(getFragmentManager(), "photo");
        });

        return view;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
        TextView textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewQuestion.setText(this.no + "님 " + textViewQuestion.getText());
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
        TextView textViewDayOfYear = view.findViewById(R.id.textViewDayOfYear);
        textViewDayOfYear.setText(textViewDayOfYear.getText().toString() + "(" + String.valueOf(this.dayOfYear) + ")");
    }

    public void setAnswer(String answer) {
        this.answer = answer;
        EditText editTextAnswer = view.findViewById(R.id.editTextAnswer);
        editTextAnswer.setText(this.answer);
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
        Log.d("debug", "photo length : " + photo.length);
        ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        imageViewPhoto.setImageBitmap(Util.byteArrayToBitmap(photo));
    }

    public void setPhoto(Uri selectedImage) {
        ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        imageViewPhoto.setImageURI(selectedImage);

        InputStream iStream = null;
        try {
            iStream = getContext().getContentResolver().openInputStream(selectedImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            photo = Util.getBytes(iStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPhoto() {
        ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        photo = new byte[]{};
        imageViewPhoto.setImageBitmap(null);
    }

    public byte[] getPhoto() { return photo; }

    public void saveImageToGallery(){
        ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        imageViewPhoto.setDrawingCacheEnabled(true);
        Bitmap b = imageViewPhoto.getDrawingCache();
        if (b == null) return;
        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b, "shimpyo", "from shimpyo");
    }
}
