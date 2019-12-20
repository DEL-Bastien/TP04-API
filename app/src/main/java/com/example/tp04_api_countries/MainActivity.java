package com.example.tp04_api_countries;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;

import android.content.Context;

import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private EditText name;
    private TextView result_text;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Permissions*/
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        /** Droits d'accès au stockage*/
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.nomCapital);
        button = findViewById(R.id.capitalButton);
        result_text = findViewById(R.id.result);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://restcountries.eu/rest/v2/capital/" + name.getText();

                        HttpURLConnection maConnection = null;
                        BufferedReader reponse;
                        String ligneReponse;
                        StringBuffer reponseJson = new StringBuffer();
                        try {
                            maConnection = (HttpURLConnection) new URL(url).openConnection();
                            reponse = new BufferedReader(new InputStreamReader(maConnection.getInputStream()));
                            while ((ligneReponse = reponse.readLine()) != null)
                                reponseJson.append(ligneReponse);
                            reponse.close();

                            /** 3. Traitement de la reponse */
                            traitementReponse(reponseJson.toString());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    private void traitementReponse(String unJson) {
        try {
            /** Traitement des données */
            JSONObject jsonObject = new JSONObject(unJson);

            if (jsonObject.length() > 0) {
                final StringBuilder affichage = new StringBuilder();
                affichage.append("Nom du pays : ").append(jsonObject.getString("name")).append("\n");
                /** Thread d'accès à un élément de l'interface */
                result_text.post(new Runnable() {
                    @Override
                    public void run() {
                        result_text.setText(affichage);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
    }
}
