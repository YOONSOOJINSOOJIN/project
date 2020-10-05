package com.example.smuocr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class study extends AppCompatActivity {

    public static final String EXTRA_ENGLISH =
            "com.example.roomwordbook.EXTRA_ENGLISH";
    public static final String EXTRA_MEAN =
            "com.example.roomwordbook.EXTRA_MEAN";
    public static final String EXTRA_ID =
            "com.example.roomwordbook.EXTRA_ID";
    private final static String[] STUDY_TYPE = {"모두", "영어만", "뜻만"};

    private TextView english;
    private TextView mean;
    private Button visibilityBtn;

    private List<Voca> vocas;

    private int mNowStudyTypeIdx = 0;
    private int mNowStudyIdx = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        english = findViewById(R.id.text_english);
        mean = findViewById(R.id.text_means);
        visibilityBtn = findViewById(R.id.button_visibility);

        vocas = VocaAdapter.vocas;

        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)) {
            english.setText(intent.getStringExtra(EXTRA_ENGLISH));
            mean.setText(intent.getStringExtra(EXTRA_MEAN));
        }

        Button visibilityBtn = findViewById(R.id.button_visibility);
        visibilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onVisibilityBtnClicked();
            }
        });

        Button previousBtn = findViewById(R.id.button_previous);
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousBtnClicked();
            }
        });

        Button nextBtn = findViewById(R.id.button_next);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextBtnClicked();
            }
        });
    }

    public void onPreviousBtnClicked() {
        if (mNowStudyIdx == 0) {
            Toast.makeText(this, "첫 단어입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            mNowStudyIdx--;
            setQuestion();
        }
    }

    public void onNextBtnClicked() {
        if (mNowStudyIdx == vocas.size() - 1) {
            Toast.makeText(this, "마지막 단어입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            mNowStudyIdx++;
            setQuestion();
        }
    }

    public void onVisibilityBtnClicked() {
        mNowStudyTypeIdx = (mNowStudyTypeIdx + 1) % STUDY_TYPE.length;

        setNowStudyTypeText();
        setTextsVisibility();
    }

    protected void setNowStudyTypeText() {
        visibilityBtn.setText(STUDY_TYPE[mNowStudyTypeIdx]);
    }

    protected void setQuestion() {
        if (vocas == null) {
            return;
        }


        english.setText(vocas.get(mNowStudyIdx).getEnglish());
        mean.setText(vocas.get(mNowStudyIdx).getMean());
    }



    protected void setTextsVisibility() {
        english.setVisibility(View.VISIBLE);
        mean.setVisibility(View.VISIBLE);

        switch (STUDY_TYPE[mNowStudyTypeIdx]) {
            case "모두":
                break;
            case "영어만":
                mean.setVisibility(View.INVISIBLE);
                break;
            case "뜻만":
                english.setVisibility(View.INVISIBLE);
                break;
        }
    }



    public void setVocas(List<Voca> vocas) {
        this.vocas = vocas;
    }
}