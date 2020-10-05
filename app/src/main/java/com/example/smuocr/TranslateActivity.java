package com.example.smuocr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Semaphore;


public class TranslateActivity extends AppCompatActivity {

    /*
        Description : this activity translates the OCR String through papago api
        and shows the result to the user.
    */

    String _ocr;
    String _translate;
    String _replace_string;
    Handler handler;

    EditText ocr_result;
    TextView translate_result;

    Vector<String> word_token;
    Vector<WordSet> word_set;

    WordSet word_temp;

    String OCR_PAGO = "";
    String PAGO_RETURN = "";
    String temp_str = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);



        ocr_result = findViewById(R.id.ocr_result);
        translate_result = findViewById(R.id.translate_result);
        Button translate_btn = findViewById(R.id.translate_btn);
        Button go_word_btn = findViewById(R.id.go_word_btn);

        word_temp = new WordSet();

        Intent intent = getIntent();
        _ocr = intent.getStringExtra("OCR_STRING");
        ocr_result.setText(_ocr);



        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OCR_PAGO = ocr_result.getText().toString();
                TranslateTask translateTask = new TranslateTask();
                translateTask.execute();
                handler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (msg.what == 337) {
                            translate_result.setText(PAGO_RETURN);

                        }
                    }
                };
            }
        });

        go_word_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
                String __word_ocr ="";
                __word_ocr = ocr_result.getText().toString();
                translate_result.setText(" ");

                _replace_string =__word_ocr.replaceAll(match, " ");


                word_token = new Vector<String>();

                StringTokenizer Token = new StringTokenizer(_replace_string);
                while(Token.hasMoreTokens()) {
                    String to = Token.nextToken();
                    if(to.length() > 3) {
                        word_token.add(to);
                    }
                }



                word_temp = new WordSet();

                String test_text = "";


                for(int i=0;i<word_token.size();++i){
                    test_text += word_token.elementAt(i) + "\n";
                }
                OCR_PAGO = test_text;
                TranslateTask translateTask = new TranslateTask();
                translateTask.execute();

                handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg){
                        if (msg.what == 337) {

                                _translate = PAGO_RETURN;
                                word_temp.Korean = _translate;


                            Vector<String> kor_word_token = new Vector<>();
                            StringTokenizer kor_Token = new StringTokenizer(word_temp.Korean,"\n");
                            while(kor_Token.hasMoreTokens()) {
                                String to = kor_Token.nextToken();
                                kor_word_token.add(to);
                            }


                            word_set = new Vector<WordSet>();

                            for(int i=0;i<word_token.size();++i){
                                WordSet test_temp = new WordSet();
                                test_temp.English = word_token.elementAt(i);
                                test_temp.Korean = kor_word_token.elementAt(i);
                                word_set.add(test_temp);
                            }

                            /* debug */

                            for(int i=0; i < word_set.size(); ++i){
                                temp_str += word_set.elementAt(i).English + "#" + word_set.elementAt(i).Korean + "#";
                            }
                            /*
                            translate_result.setText(temp_str);
                            temp_str = "";
                            */
                            /* debug */

                            Intent intent = new Intent(TranslateActivity.this, VocaList.class);
                            intent.putExtra("WORD_SET_STRING",temp_str);
                            startActivityForResult(intent,Constant.AUTOMATIC_ADD);





                            /*
                            When you get to this point,
                            the English,Korean words are stored in the WordSet,
                            so move to WordBook activity.
                            */

                            /* Vector<WordSet> word_set */
                            /* example */

                            /*
                            Intent intent = new Intent(TranslateActivity.this, ...Activity.class);
                            intent.putExtra("WORD_SET_STRING",word_set);
                            */

                            word_set = null;
                            }
                    }
                };





            }
        });

    }

    /*
        Description : Papago API
                      This code is taken from Naver
                      it used as an example of using Papago API.
                      1. parse the result received as JSON
                      2. using the event handler, Send Translation result to Activity
    */
    public class TranslateTask extends AsyncTask<String, Void, String> {


        private String trans_string = "";
        private String result = "";

        public void set_text(String str) {
            this.trans_string = str;
        }

        public void set_trans_text(String str) {
            this.result = str;
        }


        public String get_text() {
            return this.result;
        }

        @Override
        protected String doInBackground(String... strings) {


            String clientId = "PG76WWiLnDbpoeyA7f4M";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "0frGLmVFkp";//애플리케이션 클라이언트 시크릿값";

            String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
            String text;
            try {

                text = URLEncoder.encode(OCR_PAGO, "UTF-8");

            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("인코딩 실패", e);
            }

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("X-Naver-Client-Id", clientId);
            requestHeaders.put("X-Naver-Client-Secret", clientSecret);

            String responseBody = post(apiURL, requestHeaders, text);




            return responseBody;

        }

        @Override
        protected void onPostExecute(String result) {

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONObject object = jsonObject.getJSONObject("message");
                JSONObject subObject = object.getJSONObject("result");
                String attr1 = subObject.getString("translatedText");
                PAGO_RETURN = attr1;
                handler.sendEmptyMessage(337);

            } catch (Exception e) {
            }

            // doInBackground 에서 받아온 total 값 사용 장소
        }


        private String post(String apiUrl, Map<String, String> requestHeaders, String text) {
            HttpURLConnection con = connect(apiUrl);
            String postParams = "source=en&target=ko&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
            try {
                con.setRequestMethod("POST");
                for (Map.Entry<String, String> header : requestHeaders.entrySet()) {
                    con.setRequestProperty(header.getKey(), header.getValue());
                }

                con.setDoOutput(true);
                try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                    wr.write(postParams.getBytes());
                    wr.flush();
                }

                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                    return readBody(con.getInputStream());
                } else {  // 에러 응답
                    return readBody(con.getErrorStream());
                }
            } catch (IOException e) {
                throw new RuntimeException("API 요청과 응답 실패", e);
            } finally {
                con.disconnect();
            }
        }

        private HttpURLConnection connect(String apiUrl) {
            try {
                URL url = new URL(apiUrl);
                return (HttpURLConnection) url.openConnection();
            } catch (MalformedURLException e) {
                throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
            } catch (IOException e) {
                throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
            }
        }

        private String readBody(InputStream body) {
            InputStreamReader streamReader = new InputStreamReader(body);

            try (BufferedReader lineReader = new BufferedReader(streamReader)) {
                StringBuilder responseBody = new StringBuilder();

                String line;
                while ((line = lineReader.readLine()) != null) {
                    responseBody.append(line);
                }

                return responseBody.toString();
            } catch (IOException e) {
                throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
            }
        }
    }

}
