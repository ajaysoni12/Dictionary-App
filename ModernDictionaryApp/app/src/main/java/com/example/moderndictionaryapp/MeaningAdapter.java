package com.example.moderndictionaryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MeaningAdapter extends RecyclerView.Adapter<MeaningAdapter.ViewHolder> {

    List<Meanings> meaningsList;
    Context context;

    public MeaningAdapter(Context context, List<Meanings> meaningsList) {
        this.context = context;
        this.meaningsList = meaningsList;
    }

    @NonNull
    @Override
    public MeaningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meaning_recyler_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeaningAdapter.ViewHolder holder, int position) {
        holder.txtPartOfSpeechWord.setText(meaningsList.get(position).getPartOfSpeech());
        List<String> definitions = meaningsList.get(position).getDefinitions();
        List<String> synonyms = meaningsList.get(position).getSynonyms();
        List<String> antonyms = meaningsList.get(position).getAntonyms();

        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        StringBuilder stringBuilder3 = new StringBuilder();

        int currInd = 1;
        for (String definition : definitions) {
            stringBuilder1.append(currInd).append("  ").append(definition).append("\n\n");
            currInd++;
        }
        holder.txtDefinitions.setText(stringBuilder1.toString());

        for (String synonym : synonyms) {
            stringBuilder2.append(synonym).append(",  ");
        }
        holder.txtSynonyms.setText(stringBuilder2.toString());

        for (String antonym : antonyms) {
            stringBuilder3.append(antonym).append(",  ");
        }
        holder.txtAntonyms.setText(stringBuilder3.toString());

    }

    @Override
    public int getItemCount() {
        return meaningsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPartOfSpeechWord, txtDefinitions, txtSynonyms, txtAntonyms;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtPartOfSpeechWord = itemView.findViewById(R.id.txtPartOfSpeechWord);
            txtDefinitions = itemView.findViewById(R.id.txtDefinitions);
            txtSynonyms = itemView.findViewById(R.id.txtSynonyms);
            txtAntonyms = itemView.findViewById(R.id.txtAntonyms);
        }
    }

    public void updateData(List<Meanings> meaningsList) {
        this.meaningsList = meaningsList;
        notifyDataSetChanged();
    }
}
