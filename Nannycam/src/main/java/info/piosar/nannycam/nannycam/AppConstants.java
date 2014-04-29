package info.piosar.nannycam.nannycam;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by klieth on 4/29/14.
 */
public class AppConstants {
    private static Socket sock = null;

    public static Socket initSocket(String dest, int port) throws UnknownHostException, IOException {
        sock = new Socket(dest, port);
        return sock;
    }
    public static Socket getSocket() {
        return sock;
    }
}
