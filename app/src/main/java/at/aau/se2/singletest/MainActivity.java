package at.aau.se2.singletest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Button sendButton;
    EditText editText;
    TextView resultText;
    MatriculationNumberService matriculationNumberService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matriculationNumberService = new MatriculationNumberService();

        sendButton = findViewById(R.id.buttonSendRequest);
        editText = findViewById(R.id.editTextStudentId);
        resultText = findViewById(R.id.labelResult);

        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onNext(@NonNull String s) {
                resultText.append(s);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                editText.setEnabled(true);
                sendButton.setEnabled(true);
            }
            @Override
            public void onComplete() {
                editText.setEnabled(true);
                sendButton.setEnabled(true);
            }
        };
        sendButton.setOnClickListener(v -> {
            Editable text = editText.getText();
            String value = text.toString();
            sendButton.setEnabled(false);
            editText.setEnabled(false);

            matriculationNumberService.send(value)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(matriculationNumberService != null) {
            //matriculationNumberService.cancel();
        }

    }
}