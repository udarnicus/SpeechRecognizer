package com.example.speechrecognizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView);


        AudioManager audio = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0,  AudioManager.FLAG_SHOW_UI);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        Log.i("VoiceRecognitionActivit", "isRecognitionAvailable: " + SpeechRecognizer.isRecognitionAvailable(this));
        speech.setRecognitionListener(MainActivity.this);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        speech.startListening(recognizerIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Started Recognition!", Toast.LENGTH_LONG).show();
                    speech.startListening(recognizerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast
                            .LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onReadyForSpeech(Bundle params) {

    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i("VoiceRecognitionActivit", "onBeginningOfSpeech");
        progressBar.setIndeterminate(false);
        progressBar.setMax(10);

    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.i("VoiceRecognitionActivit", "onRmsChanged: " + rmsdB);
        progressBar.setProgress((int) rmsdB);


    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i("VoiceRecognitionActivit", "onBufferReceived: " + buffer);

    }

    @Override
    public void onEndOfSpeech() {
        speech.startListening(recognizerIntent);
        Log.i("VoiceRecognitionActivit", "Restarting Voice Recognition");

    }

    @Override
    public void onError(int error) {
        Log.d("VoiceRecognitionActivit", "FAILED Recognition" + "ERROR ERROR");
        speech.stopListening();
        speech.startListening(recognizerIntent);

    }

    @Override
    public void onResults(Bundle results) {
        Log.i("VoiceRecognitionActivit", "onResults");
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String text = "";
        for (String result : matches)
            if(result.equals("help") || result.equals("Help")){
                textView.setText("Calling");
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "017647220372"));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);

            }


    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i("VoiceRecognitionActivit", "onPartialResults");
        speech.stopListening();
        speech.startListening(recognizerIntent);
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i("VoiceRecognitionActivit", "onEvent");
    }
}
