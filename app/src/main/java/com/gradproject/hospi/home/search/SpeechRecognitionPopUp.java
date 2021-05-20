package com.gradproject.hospi.home.search;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;

import com.gradproject.hospi.R;
import com.gradproject.hospi.databinding.SpeechRecognitionPopUpBinding;

import java.util.ArrayList;

public class SpeechRecognitionPopUp extends AppCompatActivity {
    private SpeechRecognitionPopUpBinding binding;

    Intent recogIntent;
    SpeechRecognizer mRecognizer;
    String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SpeechRecognitionPopUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recogIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recogIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        recogIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        binding.retryBtn.setOnClickListener(v -> startRecognition());

        startRecognition();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecognizer.stopListening();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void startRecognition(){
        binding.failureTxt.setVisibility(View.INVISIBLE);
        binding.recordStatusTxt.setVisibility(View.VISIBLE);

        binding.retryBtn.setVisibility(View.INVISIBLE);
        binding.voiceBkg.setBackground(getResources().getDrawable(R.drawable.record_on, null));
        binding.voiceImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

        mRecognizer=SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);
        mRecognizer.startListening(recogIntent);
    }

    private final RecognitionListener listener = new RecognitionListener() {
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

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onError(int error) {
            binding.failureTxt.setVisibility(View.VISIBLE);
            binding.recordStatusTxt.setVisibility(View.INVISIBLE);

            binding.voiceBkg.setBackground(getResources().getDrawable(R.drawable.record_off, null));
            binding.voiceImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#4646FF")));
            binding.retryBtn.setVisibility(View.VISIBLE);
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onResults(Bundle results) {
            binding.failureTxt.setVisibility(View.INVISIBLE);
            binding.recordStatusTxt.setVisibility(View.INVISIBLE);

            binding.voiceBkg.setBackground(getResources().getDrawable(R.drawable.record_success, null));
            binding.voiceImg.setImageTintList(ColorStateList.valueOf(Color.parseColor("#4646FF")));

            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for(int i=0; i<matches.size(); i++){
                result = matches.get(i);
            }

            binding.successTxt.setText(result);

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