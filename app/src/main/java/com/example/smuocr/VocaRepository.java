package com.example.smuocr;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class VocaRepository {

    private VocaDao vocaDao;
    private LiveData<List<Voca>> allVocas;

    public VocaRepository(Application application) {
        VocaDatabase database = VocaDatabase.getInstance(application);
        vocaDao = database.vocaDao();
        allVocas = vocaDao.getAllvocas();
    }

    public void insert(Voca voca) {
        new InsertVocaAsyncTask(vocaDao).execute(voca);
    }

    public void update(Voca voca) {
        new UpdateVocaAsyncTask(vocaDao).execute(voca);
    }

    public void delete(Voca voca) {
        new DeleteVocaAsyncTask(vocaDao).execute(voca);
    }

    public LiveData<List<Voca>> getAllVocas() {
        return allVocas;
    }

    private static class InsertVocaAsyncTask extends AsyncTask<Voca, Void, Void> {
        private VocaDao vocaDao;

        private InsertVocaAsyncTask(VocaDao vocaDao) {
            this.vocaDao = vocaDao;
        }

        @Override
        protected Void doInBackground(Voca... vocas) {
            vocaDao.insert(vocas[0]);
            return null;
        }
    }

    private static class UpdateVocaAsyncTask extends AsyncTask<Voca, Void, Void> {
        private VocaDao vocaDao;

        private UpdateVocaAsyncTask(VocaDao vocaDao) {
            this.vocaDao = vocaDao;
        }

        @Override
        protected Void doInBackground(Voca... vocas) {
            vocaDao.update(vocas[0]);
            return null;
        }
    }

    private static class DeleteVocaAsyncTask extends AsyncTask<Voca, Void, Void> {
        private VocaDao vocaDao;

        private DeleteVocaAsyncTask(VocaDao vocaDao) {
            this.vocaDao = vocaDao;
        }

        @Override
        protected Void doInBackground(Voca... vocas) {
            vocaDao.delete(vocas[0]);
            return null;
        }
    }

}
