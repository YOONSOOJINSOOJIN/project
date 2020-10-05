package com.example.smuocr;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.logging.Logger;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Test extends AppCompatActivity {

    private final static String TAG = "TESTACTIVITY";
    private final static int ANSWER_CNT_MAX = 4;
    private final static int TIME_OF_JUDGMENT_SHOWING = 1000;

    private VocaDatabase mDb;

    private Voca voca;
    private List<Voca> mTestData;
    private ArrayList<Integer> mWrongVocaIds;
    private Map<Integer, String> mAnswerIdMeanMap = new HashMap<>();
    private int mNowQuestions = -1;

    List<String> mAnswers = new ArrayList<>();

    private Handler mHandler = new Handler();

    private AlertDialog.Builder mDialogBuilder;

    private TextView englishText;

    @BindViews({R.id.button_means1, R.id.button_means2, R.id.button_means3, R.id.button_means4})
    List<Button> meansBtns;

    private VocaViewModel vocaViewModel;

    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button answer4;

    private int questionCounter = 0;
    private int questionTotalCount;

    Random r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);

        mTestData = new ArrayList<>(VocaAdapter.vocas);

        r = new Random();

        englishText = findViewById(R.id.text_english);
        answer1 = findViewById(R.id.button_means1);
        answer2 = findViewById(R.id.button_means2);
        answer3 = findViewById(R.id.button_means3);
        answer4 = findViewById(R.id.button_means4);

        int temp = 0;

        for (Voca voca : mTestData) {
            mAnswerIdMeanMap.put(temp++, voca.getMean());
        }
        Collections.shuffle(mTestData);
        startTest();
    }

    protected void startTest() {
        mNowQuestions = -1;
        mWrongVocaIds = new ArrayList<>();
        mHandler.post(mSettingNewQuestionRunnable);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWrongVocaIds = intent.getIntegerArrayListExtra(IntentExtra.VOCA_ID_LIST);
        //mTestData = new ArrayList<>();
        //mTestData.add(voca);

        if (mTestData.size() == mWrongVocaIds.size()) {
            startTest();
        }
    }

    protected boolean isCorrect(String clickedMeans) {
        if (mTestData.get(mNowQuestions).getMean().equals(clickedMeans)) {
            return true;
        }
        else {
            return false;
        }
    }

    protected boolean isEnd() {
        return (mNowQuestions == mTestData.size() - 1) ? true : false;
    }

    private Runnable mSettingNewQuestionRunnable = new Runnable() {
        List<Integer> mVocaIdSet;

        @Override
        public void run() {
            mNowQuestions++;
            englishText.setText(mTestData.get(mNowQuestions).getEnglish());

            setNewAnswers(mTestData.get(mNowQuestions).getMean());

            setMeanTextsOnBtns();
            changeButtonEnables(true);
        }

        private void setNewAnswers(String realAnswer) {
            mAnswers.clear();
            mAnswers.add(realAnswer);

            mVocaIdSet = new ArrayList<>(mAnswerIdMeanMap.keySet());
            Collections.shuffle(mVocaIdSet);

            Random random = new Random();
            random.setSeed(System.currentTimeMillis());
            int temp1 = mAnswerIdMeanMap.size();
            int id = random.nextInt(temp1);
            boolean FLAG = true;

            while (mAnswers.size() < 4) {
                for (int i = 0; i < mAnswers.size(); i++) {
                    if (mAnswers.get(i) == mAnswerIdMeanMap.get(id)) {
                        FLAG = false;
                    }
                }
                if(FLAG == true){
                    mAnswers.add(mAnswerIdMeanMap.get(id));
                    if(id + 1 == mAnswerIdMeanMap.size()){
                        id=(id+1)%temp1;
                    }
                    else{
                        id++;
                    }
                }
                else if(FLAG == false){
                    if(id + 1 == mAnswerIdMeanMap.size()){
                        id=(id+1)%temp1;
                    }
                    else{
                        id++;
                    }
                    FLAG=true;
                }
                Log.d(TAG, "List" + mAnswers.toString());
            }
            Collections.shuffle(mAnswers);
            Log.d(TAG,"Suffled List"+mAnswers.toString());
    }

        private void setMeanTextsOnBtns() {
            for (int i = 0 ; i < ANSWER_CNT_MAX ; i++) {
                meansBtns.get(i).setText(mAnswers.get(i));
            }
        }
    };


    protected void goToTestActivityWithWrongWord() {
        startActivity(new Intent(this, Test.class)
                .putIntegerArrayListExtra(IntentExtra.VOCA_ID_LIST, mWrongVocaIds));
    }

    private void popUpDialog() {
        mDialogBuilder.show();
    }

    protected void changeButtonEnables(boolean isEnable) {
        for (Button btn : meansBtns) {
            btn.setEnabled(isEnable);
        }
    }

    @OnClick({R.id.button_means1, R.id.button_means2, R.id.button_means3, R.id.button_means4})
    public void onMeansBtnsClicked(Button button) {
        changeButtonEnables(false);

        if(isCorrect(button.getText().toString())) {
            englishText.setText("맞았습니다.");
        } else {
            englishText.setText("틀렸습니다.");
            mWrongVocaIds.add(mTestData.get(mNowQuestions).getId());
        }

        if(isEnd()) {
            if (mWrongVocaIds.size() == 0) {
                Toast.makeText(Test.this, "모두 맞혔습니다.", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else {
                Toast.makeText(Test.this, "다시 테스트 하세요.", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            }
        } else {
            mHandler.postDelayed(mSettingNewQuestionRunnable, TIME_OF_JUDGMENT_SHOWING);
        }
    }

    protected void goToMainActivity() {
        finish();
    }

    private abstract class meansBtns implements List<Button> {
        public meansBtns(Button answer1, Button answer2, Button answer3, Button answer4) {
            return;
        }
    }
}