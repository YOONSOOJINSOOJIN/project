package com.example.smuocr;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VocaAdapter extends RecyclerView.Adapter <VocaAdapter.VocaHolder> {

    public static List<Voca> vocas = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public VocaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_voca, parent, false);
        return new VocaHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VocaHolder holder, int position) {
        Voca currentVoca = vocas.get(position);
        holder.textViewEnglish.setText(currentVoca.getEnglish());
        holder.textViewMean.setText(currentVoca.getMean());
    }

    @Override
    public int getItemCount() {
        return vocas.size();
    }

    public void setVocas(List<Voca> vocas) {
        this.vocas = vocas;
        notifyDataSetChanged();
    }

    public Voca getVocaAt(int position) {
        return vocas.get(position);
    }


    class VocaHolder extends RecyclerView.ViewHolder {
        private TextView textViewEnglish;
        private TextView textViewMean;


        public VocaHolder(@NonNull View itemView) {
            super(itemView);
            textViewEnglish = itemView.findViewById(R.id.text_english);
            textViewMean = itemView.findViewById(R.id.text_means);

            itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(vocas.get(position));
                    }
                }
            });

        }

    }

    public interface OnItemClickListener {
        void onItemClick(Voca voca);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
