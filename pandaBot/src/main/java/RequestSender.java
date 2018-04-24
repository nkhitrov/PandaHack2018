import java.util.Random;

public class RequestSender extends Thread implements Runnable {
    TabletClient client;
    int delay;
    String currentState;
//    delay - Задержка между опросами в секундах
    public RequestSender(TabletClient client, int delay) {
        super();
        this.client = client;
        this.delay = delay;
    }

    @Override
    public void run() {
        while (true){
            try {
//                currentState = client.get();
                currentState = String.valueOf(new Random().nextInt(100));
                this.sleep(delay * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public String getCurrentState(){
        return currentState;
    }
}
