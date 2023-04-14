package org.Project;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String userName;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.userName = this.bufferedReader.readLine();
            this.clients.add(this);
            if (this.userName != null) {
                broadCastMessage("SERVER: " + this.userName + " has entered the chat");
            } else {
                System.out.println("Someone sent a null userName and will be removed");
                closeEveryting(this.clientSocket, this.bufferedReader, this.bufferedWriter);
                System.out.println();
            }

        } catch (IOException e) {
            closeEveryting(this.clientSocket, this.bufferedReader, this.bufferedWriter);
        }

    }

    private void broadCastMessage(String msg) {
        for (ClientHandler client : this.clients) {
            if (!client.userName.equals(this.userName)) {
                try {
                    client.bufferedWriter.write(msg);
                    client.bufferedWriter.newLine();
                    client.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEveryting(this.clientSocket, this.bufferedReader, this.bufferedWriter);
                }
            }

        }
    }

    private void removeClientHandler() {
        this.clients.remove(this);
        broadCastMessage("SERVER: " + this.userName + "has left the chat");
    }

    private void closeEveryting(Socket socket, BufferedReader buffReader, BufferedWriter buffWriter) {
        removeClientHandler();
        try {
            if (buffReader != null) {
                buffReader.close();
            }
            if (buffWriter != null) {
                buffWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        String msgClient;
        while (this.clientSocket.isConnected()) {
            try {
                msgClient = this.bufferedReader.readLine();
                if (msgClient != null) {
                    broadCastMessage(this.userName + ": " + msgClient);
                } else {
                    closeEveryting(this.clientSocket, this.bufferedReader, this.bufferedWriter);
                    break;
                }
            } catch (IOException e) {
                closeEveryting(this.clientSocket, this.bufferedReader, this.bufferedWriter);
                break;
            }
        }
    }
}
