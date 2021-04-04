package com.mirae.shimpyo.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mirae.shimpyo.R;
import com.mirae.shimpyo.helper.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Fragment01 extends Fragment {
    private View view;
    private String no;
    private int dayOfYear;
    private String question;
    private String answer;
    private byte[] photo;

    private ImageView imageViewPhoto;

    private static Fragment01 instance = null;

    private Fragment01() {
        this.photo = new byte[]{};
    }

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
        editTextAnswer.setText(this.answer);
        TextView textViewQuestion = view.findViewById(R.id.textViewQuestion);
        textViewQuestion.setText(this.question);
        Button buttonAnswer = view.findViewById(R.id.buttonAnswer);
        imageViewPhoto = view.findViewById(R.id.imageViewPhoto);

        setPhoto(photo);
        imageViewPhoto.setOnTouchListener((v, e) -> {
            Util.hideSoftKeyboard(getActivity());
            return false;
        });

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        String formattedDate = df.format(c);
        TextView textViewDayOfYear = view.findViewById(R.id.textViewDayOfYear);
        textViewDayOfYear.setText(formattedDate);

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        imageViewPhoto.setMinimumHeight((int)(height*0.35));
        editTextAnswer.setMinimumHeight((int)(height*0.3));

        buttonAnswer.setOnClickListener((v) -> {
            FragmentDialogForSave fragmentDialogForSave = new FragmentDialogForSave();
            fragmentDialogForSave.show(getFragmentManager(), "save");
        });

        imageViewPhoto.setOnClickListener(v -> {
            FragmentDialogForPhoto dialog = new FragmentDialogForPhoto();
            dialog.show(getFragmentManager(), "photo");
        });

        editTextAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setAnswer(editTextAnswer.getText().toString());
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupUI(getActivity(), view.findViewById(R.id.scrollView));
    }

    public String getNo() {
        return no;
    }
    public byte[] getPhoto() { return photo; }
    public String getAnswer() { return answer; }
    public String getQuestion() { return question; }
    public int getDayOfYear() { return dayOfYear; }

    public void setQuestion(String question) {
        this.question = question;
        if (view != null) {
            TextView textViewQuestion = view.findViewById(R.id.textViewQuestion);
            textViewQuestion.setText(question);
        }
    }

    public void setNo(String no) {
        this.no = no;
    }

    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
        if (view != null) {
            TextView textViewDayOfYear = view.findViewById(R.id.textViewDayOfYear);
            textViewDayOfYear.setText(Util.convertDayOfYearToyyyyMMdd(dayOfYear));
        }
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setAnswerView() {
        if (view != null) {
            EditText editTextAnswer = view.findViewById(R.id.editTextAnswer);
            editTextAnswer.setText(this.answer);
        }
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
        if (view != null && photo.length > 0) {
            Log.d("debug", "photo length : " + photo.length);
            ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
            TextView textViewHint = view.findViewById(R.id.textViewHint);
            textViewHint.setTextColor(getResources().getColor(R.color.transparent));
            imageViewPhoto.setImageBitmap(Util.byteArrayToBitmap(photo));
        }
    }

    public void setPhoto(Uri selectedImage) {
        if (view != null) {
            ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
            TextView textViewHint = view.findViewById(R.id.textViewHint);
            textViewHint.setTextColor(getResources().getColor(R.color.transparent));
        }

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
        photo = new byte[]{};
        if (view != null) {
            ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
            TextView textViewHint = view.findViewById(R.id.textViewHint);
            textViewHint.setTextColor(getResources().getColor(R.color.hint));
            imageViewPhoto.setImageBitmap(null);
        }
    }

    public void saveImageToGallery(){
        ImageView imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        imageViewPhoto.setDrawingCacheEnabled(true);
        Bitmap b = imageViewPhoto.getDrawingCache();
        if (b == null) return;
        MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), b, "shimpyo", "from shimpyo");
    }

    public void setupUI(Activity activity, View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    Util.hideSoftKeyboard(activity);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                // 여기서 에러가 난다.
//                setupUI(activity, innerView);
            }
        }
    }
}
