package myPackage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private List<ClientHandler> clients;
    private String nickname;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws Exception {
        this.socket = socket;
        this.clients = clients;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    // 한 클라이언트에게 메시지 보내기
    public void send(String msg) {
        out.println(msg);
    }

    // 모든 클라이언트에게 메시지 보내기
    public synchronized void broadcast(String msg) {
        for (ClientHandler c : clients) {
            c.send(msg);
        }
    }

    @Override
    public void run() {
        try {
            String msg;

            while ((msg = in.readLine()) != null) {

                if (msg.startsWith("NICK|")) {
                    nickname = msg.substring(5);
                    broadcast("JOIN|" + nickname);
                }
                else if (msg.startsWith("TEXT|")) {
                    String content = msg.substring(5);
                    broadcast("MSG|" + nickname + "|" + content);
                }
            }
        } catch (Exception e) {
            // 연결 종료 시 도착
        } finally {
            clients.remove(this);
            broadcast("LEAVE|" + nickname);
        }
    }
}