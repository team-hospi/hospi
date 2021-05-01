package com.gradproject.hospi.home.search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.gradproject.hospi.R;

import java.util.ArrayList;

public class SpeechRecognitionPopUp extends AppCompatActivity {
    Intent recogIntent;
    SpeechRecognizer mRecognizer;
    String result;
    TextView recordStatusTxt, failureTxt, successTxt;
    MaterialButton retryBtn;
    FrameLayout voiceBkg;
    ImageView voiceImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speech_recognition_pop_up);

        voiceBkg = findViewById(R.id.voiceBkg);
        voiceImg = findViewById(R.id.voiceImg);
        recordStatusTxt = findViewById(R.id.recordStatusTxt);
        failureTxt = findViewById(R.id.failureTxt);
        successTxt = findViewById(R.id.successTxt);
        retryBtn = findViewById(R.id.retryBtn);

        recogIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recogIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        retryBtn.setOnClickListener(v -> startRecognition());

        startRecognition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecognizer.stopListening();
    }

    private void startRecognition(){
        failureTxt.setVisibility(View.INVISIBLE);
        recordStatusTxt.setVisibility(View.VISIBLE);

        retryBtn.setVisibility(View.INVISIBLE);
        voiceBkg.setBackground(getResources().getDrawable(R.drawable.record_on, null));
        voiceImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

        mRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(recogIntent);
    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {}

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {
            failureTxt.setVisibility(View.VISIBLE);
            recordStatusTxt.setVisibility(View.INVISIBLE);

            voiceBkg.setBackground(getResources().getDrawable(R.drawable.record_off, null));
            voiceImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#4646FF")));
            retryBtn.setVisibility(View.VISIBLE);
        }

        @Override
        public void onResults(Bundle results) {
            failureTxt.setVisibility(View.INVISIBLE);
            recordStatusTxt.setVisibility(View.INVISIBLE);

            voiceBkg.setBackground(getResources().getDrawable(R.drawable.record_success, null));
            voiceImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#4646FF")));

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i=0; i<matches.size(); i++){
                result = matches.get(i);
            }

            successTxt.setText(result);

            Intent intent = new Intent();
            intent.putExtra("result", result);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };
}