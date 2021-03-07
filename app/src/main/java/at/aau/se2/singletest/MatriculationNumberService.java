package at.aau.se2.singletest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MatriculationNumberService {

    static final String HOST = "se2-isys.aau.at";
    static final int PORT = 53212;

    String sendSync(String request) {
        try (Socket s = new Socket(HOST, PORT);
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            out.writeBytes(request + '\n');
            out.flush();

            try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                return in.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Disposable send(String request,
                           @NonNull Consumer<String> onSuccess,
                           @NonNull Consumer<? super Throwable> onError) {
        Single<String> single = Single.create(s -> {
            try {
                s.onSuccess(sendSync(request));
            } catch (Exception e) {
                s.onError(e);
            }
        });
        return single.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onError);
    }
}
