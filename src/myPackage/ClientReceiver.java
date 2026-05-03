package myPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ClientReceiver extends Thread {

    private BufferedReader in;
    private BlockingQueue<String> queue;

    public ClientReceiver(Socket socket, BlockingQueue<String> queue) throws Exception {
        this.queue = queue;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            String msg;

            while ((msg = in.readLine()) != null) {
                queue.offer(msg);  // GUI 로 전달
            }

        } catch (Exception e) {
            // 연결 종료 시 도착 가능
        }
    }
}