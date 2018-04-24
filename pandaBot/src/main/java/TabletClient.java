import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by toshiba on 19.02.2018.
 */

public class TabletClient {
    private Socket client;

    private String IP;
    private int port;

    public TabletClient(String ip, int port) {
        this.IP = ip;
        this.port = port;

    }

    public String get() {
        try {
            client = new Socket(IP, port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(new DataInputStream(client.getInputStream())));

            String response = reader.readLine();
            client.close();

            return response;
        } catch (Exception e) {
            return "";
        }
    }
}
