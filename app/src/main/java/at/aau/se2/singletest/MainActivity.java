package at.aau.se2.singletest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    static final String LOG_TAG = MainActivity.class.getSimpleName();

    Button sendButton;
    EditText editText;
    TextView resultText;
    MatriculationNumberService matriculationNumberService;
    Disposable currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        matriculationNumberService = new MatriculationNumberService();

        sendButton = findViewById(R.id.buttonSendRequest);
        editText = findViewById(R.id.editTextStudentId);
        resultText = findViewById(R.id.labelResult);

        sendButton.setOnClickListener(v -> {
            Editable text = editText.getText();
            String value = text.toString();
            sendButton.setEnabled(false);
            editText.setEnabled(false);

//            currentCall = matriculationNumberService.send(value,
//                    this::onResponseReceived, this::onSendError);
            try {
                // the call is too short to make it async
                onSortNumber(matriculationNumberService.insertSorted(value));
            } catch (Exception ex) {
                onSendError(ex);
            }
        });
    }

    void onSortNumber(List<String> numbers) {
        editText.getText().clear();
        editText.setEnabled(true);
        sendButton.setEnabled(true);
        resultText.setText(String.join("\n", numbers));
    }

    void onResponseReceived(String response) {
        Log.i(LOG_TAG, "received response: " + response);
        resultText.append("\n" + response);

        editText.setEnabled(true);
        sendButton.setEnabled(true);
    }

    void onSendError(Throwable error) {
        Log.e(LOG_TAG, "Error: " + error.getMessage(), error);

        new AlertDialog.Builder(this)
                .setMessage("Error: " + error.getMessage())
                .setTitle("Error")
                .setNeutralButton("OK", (d, w) -> d.dismiss())
                .create()
                .show();

        editText.setEnabled(true);
        sendButton.setEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentCall != null) {
            currentCall.dispose();
        }
    }
}