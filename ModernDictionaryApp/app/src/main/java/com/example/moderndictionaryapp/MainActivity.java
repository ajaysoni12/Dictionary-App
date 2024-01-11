package com.example.moderndictionaryapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edtInputWord;
    Button btnSearchWord;
    TextView txtInputWord, txtPhoneticWord;
    RecyclerView recyclerView;
    MeaningAdapter meaningAdapter;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtInputWord = findViewById(R.id.edtInputWord);
        btnSearchWord = findViewById(R.id.btnSearchWord);
        txtInputWord = findViewById(R.id.txtInputWord);
        txtPhoneticWord = findViewById(R.id.txtPhoneticWord);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);


        meaningAdapter = new MeaningAdapter(MainActivity.this, new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(meaningAdapter);

        btnSearchWord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = edtInputWord.getText().toString();
                if (word.equals("")) {
                    edtInputWord.setError("Please enter word");
                } else {
                    getWordMeaning(word);
                }
            }
        });

    }

    private void getWordMeaning(String word) {
        progressBar.setVisibility(View.VISIBLE);
        btnSearchWord.setClickable(false);
        String url = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject result = response.getJSONObject(0);
                    WordResult wordResult = parseResult(result);

                    txtInputWord.setText(wordResult.getWord());
                    txtPhoneticWord.setText(wordResult.getPhonetic());
                    meaningAdapter.updateData(wordResult.getMeaningsList());

                    progressBar.setVisibility(View.GONE);
                    btnSearchWord.setClickable(true);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(MainActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                txtInputWord.setText("no word exist!");
                txtPhoneticWord.setText("");
                meaningAdapter.updateData(new ArrayList<>());
                progressBar.setVisibility(View.GONE);
                btnSearchWord.setClickable(true);
            }
        });

        requestQueue.add(jsonArrayRequest);
    }

    /* parse all meanings of word */
    private WordResult parseResult(JSONObject result) {
        WordResult wordResult = new WordResult();

        try {
            // get word and phonetic
            wordResult.setWord(result.getString("word"));
            wordResult.setPhonetic(result.getString("phonetic"));

            // parse meaning of word here
            List<Meanings> meaningsList = new ArrayList<>();
            JSONArray meaningArray = result.getJSONArray("meanings");
            for (int i = 0; i < meaningArray.length(); i++) {
                JSONObject meaningObject = meaningArray.getJSONObject(i);
                Meanings meanings = new Meanings();

                // get part of speech
                meanings.setPartOfSpeech(meaningObject.getString("partOfSpeech"));

                // parse definitions of word
                JSONArray definitionsArray = meaningObject.getJSONArray("definitions");
                List<String> definitionsList = new ArrayList<>();
                for (int j = 0; j < definitionsArray.length(); j++) {
                    JSONObject definitionsObject = definitionsArray.getJSONObject(j);

                    // now add all definitions of word
                    definitionsList.add(definitionsObject.getString("definition"));
                }
                meanings.setDefinitions(definitionsList);

                // add synonyms and antonyms of word
                meanings.setSynonyms(parseStringArray(meaningObject.getJSONArray("synonyms")));
                meanings.setAntonyms(parseStringArray(meaningObject.getJSONArray("antonyms")));
                meaningsList.add(meanings);
            }
            wordResult.setMeaningsList(meaningsList);
        } catch (JSONException e) {
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        }

        return wordResult;
    }

    /* this method parse synonyms and antonyms fromm jsonArray */
    private List<String> parseStringArray(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                list.add(jsonArray.getString(i));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    }

}