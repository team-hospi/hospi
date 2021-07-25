package com.gradproject.hospi.home.search;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.gradproject.hospi.databinding.FragmentSearchWindowBinding;
import com.gradproject.hospi.home.hospital.HospitalActivity;
import com.gradproject.hospi.utils.SoundSearcher;

import java.util.ArrayList;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class SearchWindowFragment extends Fragment implements RecognitionListener {
    private static final String TAG = "SearchWindowFragment";
    private FragmentSearchWindowBinding binding;

    LinearLayoutManager layoutManager;
    HospitalAdapter hospitalAdapter = new HospitalAdapter();
    ActivityResultLauncher<Intent> mGetContent;
    SpeechRecognizer speech;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        speech = SpeechRecognizer.createSpeechRecognizer(getContext());
        speech.setRecognitionListener(this);
        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                if(result.getData()!=null){
                    ArrayList<String> text = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    for(int i = 0; i < text.size() ; i++){
                        Log.e("GoogleActivity", "onActivityResult text : " + text.get(i));
                        binding.searchEdt.setText(text.get(i));
                    }
                }
                searchProcess();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchWindowBinding.inflate(inflater, container, false);

        binding.hospitalList.setLayoutManager(layoutManager);

        binding.backBtn.setOnClickListener(v -> requireActivity().finish());
        binding.removeBtn.setOnClickListener(v -> binding.searchEdt.setText(""));

        binding.voiceInputBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR"); //언어지정입니다.
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireContext().getPackageName());
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);   //검색을 말한 결과를 보여주는 갯수
                mGetContent.launch(recognizerIntent);
            }else{
                micPermissionCheck();
            }
        });

        binding.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { /* empty */ }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(binding.searchEdt.getText().toString().equals("")){
                    binding.voiceInputBtn.setVisibility(View.VISIBLE);
                    binding.removeBtn.setVisibility(View.INVISIBLE);
                }else{
                    binding.voiceInputBtn.setVisibility(View.INVISIBLE);
                    binding.removeBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { /* empty */ }
        });

        binding.searchEdt.setOnEditorActionListener((v, actionId, event) -> {
            searchProcess();
            return true;
        });

        hospitalAdapter.setOnItemClickListener((holder, view, position) -> {
            Hospital hospital = hospitalAdapter.getItem(position);
            Intent intent = new Intent(getContext(), HospitalActivity.class);
            intent.putExtra("hospital", hospital);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void micPermissionCheck(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireContext());
        alertDialog.setTitle("앱 권한");
        alertDialog.setMessage("음성 인식 검색을 이용하기 위해서는 권한 허용이 필요합니다. 해당 기능을 이용하시려면 애플리케이션 [정보]>[권한] 에서 마이크 액세스 권한을 허용해 주십시오.");
        alertDialog.setPositiveButton("권한설정",
                (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + requireActivity().getPackageName()));
                    startActivity(intent);
                    dialog.cancel();
                });
        alertDialog.setNegativeButton("취소",
                (dialog, which) -> dialog.cancel());
        alertDialog.show();
    }

    private void searchProcess(){
        hospitalAdapter.items.clear(); // 기존 검색 결과 항목 모두 삭제
        hospitalAdapter.notifyDataSetChanged(); // 어댑터 갱신

        String searchStr = binding.searchEdt.getText().toString().trim();

        if(!searchStr.equals("")){
            searchHospital(searchStr); // 검색
            binding.noSearchTxt.setVisibility(View.INVISIBLE);
        }else{
            binding.noSearchTxt.setVisibility(View.VISIBLE);
        }
    }

    private void searchHospital(String searchStr){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(Hospital.DB_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<Hospital> tmpArrList = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Hospital hospital = document.toObject(Hospital.class);

                            String str = stateDistribution(searchStr);

                            if(hospital.getName().contains(str) || hospital.getAddress().contains(str)
                                || SoundSearcher.matchString(hospital.getName(), str)
                                || SoundSearcher.matchString(hospital.getAddress(), str)){
                                tmpArrList.add(hospital);
                            }
                        }

                        if(tmpArrList.size()==0){
                            binding.noSearchTxt.setVisibility(View.VISIBLE);
                        }else{
                            binding.noSearchTxt.setVisibility(View.INVISIBLE);
                            for(int i=0; i<tmpArrList.size(); i++){
                                hospitalAdapter.addItem(tmpArrList.get(i));
                            }
                        }

                        binding.hospitalList.setAdapter(hospitalAdapter);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private String stateDistribution(String searchStr){
        String str = searchStr;

        if(searchStr.endsWith("남도")){
            str = searchStr.charAt(0) + "남";
        }else if(searchStr.endsWith("북도")){
            str = searchStr.charAt(0) + "북";
        }else if(searchStr.endsWith("도") || searchStr.endsWith("특별시")
                || searchStr.endsWith("광역시") || searchStr.endsWith("시")){
            str = searchStr.substring(0, 2);
        }

        return str;
    }

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
        String message;

        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "오디오 에러가 발생하였습니다.";
                break;

            case SpeechRecognizer.ERROR_CLIENT:
                message = "클라이언트 에러가 발생하였습니다.";
                break;

            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "권한이 존재하지 않습니다.";
                break;

            case SpeechRecognizer.ERROR_NETWORK:
                message = "네트워크 에러가 발생하였습니다.";
                break;

            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "연결 시간이 초과되었습니다.";
                break;

            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "일치하는 문자를 찾을 수 없습니다.";
                break;

            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "다시 시도해주세요.";
                break;

            case SpeechRecognizer.ERROR_SERVER:
                message = "서버 에러가 발생하였습니다.";
                break;

            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "말하는 시간이 초과되었습니다.";
                break;

            default:
                message = "알 수 없는 에러가 발생하였습니다.";
                break;
        }

        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results
                .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

        for(int i = 0; i < matches.size() ; i++){
            Log.e("GoogleActivity", "onResults text : " + matches.get(i));
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }
}