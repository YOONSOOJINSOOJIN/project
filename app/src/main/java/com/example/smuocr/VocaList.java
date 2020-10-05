package com.example.smuocr;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

public class VocaList extends AppCompatActivity {
    public static final int ADD_VOCA_REQUEST = 1;
    public static final int STUDY_VOCA_REQUEST = 2;
    public static final int TEST_VOCA_REQUEST = 3;

    private VocaViewModel vocaViewModel;

    Vector<WordSet> word_set;
    WordSet words;
    private String english;
    private String korean;
    String word_set_string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voca_list);

        FloatingActionButton addButton = findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VocaList.this, EditVoca.class);
                startActivityForResult(intent, ADD_VOCA_REQUEST);
            }
        });

        Button testButton = findViewById(R.id.test_btn);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(VocaList.this, Test.class);
                startActivityForResult(intent2, TEST_VOCA_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.list_voca);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final VocaAdapter vocaAdapter = new VocaAdapter();
        recyclerView.setAdapter(vocaAdapter);

        vocaViewModel = ViewModelProviders.of(this).get(VocaViewModel.class);
        vocaViewModel.getAllVocas().observe(this, new Observer<List<Voca>>() {
            @Override
            public void onChanged(List<Voca> vocas) {
                // update recyclerview
                vocaAdapter.setVocas(vocas);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                vocaViewModel.delete(vocaAdapter.getVocaAt(viewHolder.getAdapterPosition()));
                Toast.makeText(VocaList.this, "단어가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        vocaAdapter.setOnItemClickListener(new VocaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Voca voca) {
                Intent intent = new Intent(VocaList.this, study.class);
                intent.putExtra(study.EXTRA_ID, voca.getId());
                intent.putExtra(study.EXTRA_ENGLISH, voca.getEnglish());
                intent.putExtra(study.EXTRA_MEAN, voca.getMean());
                startActivityForResult(intent, STUDY_VOCA_REQUEST);
            }
        });

        if (getIntent().hasExtra("WORD_SET_STRING")) {
            word_set_string = getIntent().getStringExtra("WORD_SET_STRING");

            Vector<String> kor_word_token = new Vector<>();
            Vector<String> eng_word_token = new Vector<>();

            StringTokenizer word_Token = new StringTokenizer(word_set_string, "#");
            while (word_Token.hasMoreTokens()) {
                String en = word_Token.nextToken();
                eng_word_token.add(en);
                String ko = word_Token.nextToken();
                kor_word_token.add(ko);
            }

            word_set = new Vector<WordSet>();

            for (int i = 0; i < eng_word_token.size(); ++i) {
                WordSet test_temp = new WordSet();
                test_temp.English = eng_word_token.elementAt(i);
                test_temp.Korean = kor_word_token.elementAt(i);
                word_set.add(test_temp);
            }

            for (int i = 0; i < word_set.size(); i++) {
                words = word_set.get(i);
                english = words.English;
                korean = words.Korean;
                Voca voca = new Voca(english, korean);
                vocaViewModel.insert(voca);
            }
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_VOCA_REQUEST && resultCode == RESULT_OK) {

            String english = data.getStringExtra(EditVoca.EXTRA_ENGLISH);
            String mean = data.getStringExtra(EditVoca.EXTRA_MEAN);

            Voca voca = new Voca(english, mean);
            vocaViewModel.insert(voca);

            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
        }
    }
}