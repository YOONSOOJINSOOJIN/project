package com.example.smuocr;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class VocaViewModel extends AndroidViewModel {
    private VocaRepository vocaRepository;
    private LiveData<List<Voca>> allVocas;


    public VocaViewModel(Application application) {
        super(application);

        vocaRepository = new VocaRepository(application);
        allVocas = vocaRepository.getAllVocas();

    }

    public void insert(Voca voca) {
        vocaRepository.insert(voca);
    }

    public void update(Voca voca) {
        vocaRepository.update(voca);
    }

    public void delete(Voca voca) {
        vocaRepository.delete(voca);
    }

    public LiveData<List<Voca>> getAllVocas() {
        return allVocas;
    }
}
