package home.vkuzn;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
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
            e.printStackTrace();
        }

//        try {
        //recognition =  new Recognition();
//            serverSocket = new ServerSocket(port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * В потоке постоянно проверяем соединение с клиентом и принимаем речь(dataReader), сказанную пользователем, в виде потока байт.
     * Вызываем функцию распознования и передаем полученный результат пользователю.
     */
    //@Override
    public void run() {
        do {
            try {
//                recognition =  new Recognition();
                System.out.println("Welcome to server side.");
                serverSocket = new ServerSocket(port);
                socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String keyWord = in.readLine();
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Speak");

                while (!socket.isClosed()) {
                    dataReader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    resultText = recognition.Recognize(dataReader, keyWord);
                    System.out.println(resultText);

                    out.println(resultText);
                }

                dataReader.close();
                out.close();
                serverSocket.close();
                socket.close();
            } catch (IOException e) {
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
                    e1.printStackTrace();
                }
            }
        } while (true);
    }
}
