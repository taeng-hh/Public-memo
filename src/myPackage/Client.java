package myPackage;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

    public static void main(String[] args) {
        try {
            // 서버 접속
            Socket socket = new Socket("localhost", 6789);

            // 각 클라이언트마다 독립적인 메시지 큐 생성
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();

            // Receiver 스레드 시작
            ClientReceiver receiver = new ClientReceiver(socket, queue);
            receiver.start();

            // GUI 실행
            GUIFrame gui = new GUIFrame(socket, queue);
            gui.setVisible(true);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
