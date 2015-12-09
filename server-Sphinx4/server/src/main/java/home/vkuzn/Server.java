package home.vkuzn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private static String resultText;
    private Recognition recognition;
    private DataInputStream dataReader;
    private PrintWriter out;

    public Server() {
        port = 8080;
        try {
            recognition = new Recognition();
        } catch (IOException e) {
            log.info(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * В потоке постоянно проверяем соединение с клиентом и принимаем речь(dataReader), сказанную пользователем, в виде потока байт.
     * Вызываем функцию распознования и передаем полученный результат пользователю.
     */
    //@Override
    public void run() {
        do {
            try {
                System.out.println("Welcome to server side.");
                log.info("-------------------------------WELCOME-------------------------------");
                log.info("Welcome to server side.");
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String keyWord = in.readLine();
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Speak");
                log.info("Speak");

                while (!socket.isClosed()) {
                    dataReader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    resultText = recognition.recognize(dataReader, keyWord);
                    System.out.println(resultText);

                    out.println(resultText);
                }

                dataReader.close();
                out.close();
                serverSocket.close();
                socket.close();
            } catch (IOException e) {
                log.info(e.getMessage());
                if (out != null)
                    out.close();
                try {
                    if (dataReader != null)
                        dataReader.close();
                    if (serverSocket != null)
                        serverSocket.close();
                    if (socket != null)
                        socket.close();
                } catch (IOException e1) {
                    log.info(e1.getMessage());
                    e1.printStackTrace();
                }
            }
        } while (true);
    }
}
