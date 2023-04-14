package org.Project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        while (!this.serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread th = new Thread(clientHandler);
                th.start();
            } catch (IOException e) {

            }
        }
    }

    public void closeServerSocket() {
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSoc = new ServerSocket(4000);
            Server server = new Server(serverSoc);
            server.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
