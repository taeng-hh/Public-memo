package myPackage;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocket server = new ServerSocket(6789);
        List<ClientHandler> clients = new ArrayList<>();

        System.out.println("Server running...");

        while (true) {
            Socket socket = server.accept();

            ClientHandler client = new ClientHandler(socket, clients);
            clients.add(client);
            client.start();
        }
    }
}