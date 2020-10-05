package com.example.smuocr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditVoca extends AppCompatActivity {

    public static final String EXTRA_ENGLISH =
            "com.example.roomwordbook.EXTRA_ENGLISH";
    public static final String EXTRA_MEAN =
            "com.example.roomwordbook.EXTRA_MEAN";
    private static final String EXTRA_ID =
            "com.example.roomwordbook.EXTRA_ID";

    private EditText editTextEnglish;
    private EditText editTextMean;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_voca);


        editTextEnglish = findViewById(R.id.edit_english);
        editTextMean = findViewById(R.id.edit_means);


        findViewById(R.id.button_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveVoca();
            }
        });


    }

    private void saveVoca() {
        String english = editTextEnglish.getText().toString();
        String mean = editTextMean.getText().toString();
        int id = getIntent().getIntExtra(IntentExtra.VOCA_ID, IntentExtra.VOCA_NULL);

        if (english.trim().isEmpty() || mean.trim().isEmpty()) {
            Toast.makeText(this, "단어와 뜻을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_ID, id);
        data.putExtra(EXTRA_ENGLISH, english);
        data.putExtra(EXTRA_MEAN, mean);

        setResult(RESULT_OK, data);
        finish();


    }

}