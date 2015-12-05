package home.vkuzn;

public class SpeechRecognition
{
    private static Server server;
    public static void main(String[] args) {
        server = new Server();
        Thread thread = new Thread(server);
        thread.start();
    }
}
