package at.aau.se2.singletest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Observable;

public class MatriculationNumberService {

    static String host = "se2-isys.aau.at";
    static int port = 53212;

    String sendSync(String request) {
        try (Socket s = new Socket(host, port);
             OutputStream out = s.getOutputStream()) {
            out.write(request.getBytes());
            out.write('\n');
            out.flush();

            String result;
            try (InputStream in = s.getInputStream()) {
                byte[] buf = new byte[256];
                int count = 0;
                for (int b = in.read(); b != -1; b = in.read()) {
                    if (count >= buf.length) {
                        buf = Arrays.copyOf(buf, buf.length * 2);
                    }
                    buf[count++] = (byte) b;
                }
                result = new String(buf, 0, count);
            }
            return result;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Observable<String> send(String request) {
        return Observable.create(s -> {
            try {
                s.onNext(sendSync(request));
                s.onComplete();
            } catch (Exception e) {
                s.onError(e);
            }
        });
    }
}
