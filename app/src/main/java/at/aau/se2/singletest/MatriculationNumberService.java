package at.aau.se2.singletest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MatriculationNumberService {

    static final String HOST = "se2-isys.aau.at";
    static final int PORT = 53212;
    static final int MAX_NUMBER = 9999_9999;

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

    final SortedSet<Integer> repository = new TreeSet<>(Comparator
            .comparingInt(x -> (int)x % 2)
            .thenComparingInt(x -> (int)x));

    List<String> sortedNumbersAsStrings() {
        return repository.stream()
                .map(x -> String.format(Locale.ROOT, "%08d", x))
                .collect(Collectors.toList());
    }

    public List<String> insertSorted(@NonNull String number) {
        int parsed = Integer.parseInt(number);
        if(parsed <= 0 || parsed > MAX_NUMBER)
            throw new IllegalArgumentException("Invalid number: " + number);

        repository.add(parsed);

        return sortedNumbersAsStrings();
    }
}
