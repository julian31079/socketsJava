package org.Project;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    Socket socket;
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    String userName;

    public Client(Socket socket, String userName) {
        this.socket = socket;
        this.userName = userName;
        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        } catch (IOException e) {
            closeEveryting(this.socket, this.bufferedReader, this.bufferedWriter);
        }

    }

    private void closeEveryting(Socket socket, BufferedReader buffReader, BufferedWriter buffWriter) {
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

    private void sendMessage() {
        try {
            this.bufferedWriter.write(this.userName);
            this.bufferedWriter.newLine();
            this.bufferedWriter.flush();
            Scanner scanner = new Scanner(System.in);
            while (this.socket.isConnected()) {
                String msg = scanner.nextLine();
                this.bufferedWriter.write(msg);
                this.bufferedWriter.newLine();
                this.bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEveryting(this.socket, this.bufferedReader, this.bufferedWriter);
        }
    }

    private void listenMsgs() {
        Thread th = new Thread(this);
        th.start();
    }

    @Override
    public void run() {
        while (this.socket.isConnected()) {
            try {
                String msg = this.bufferedReader.readLine();
                if (msg != null) {
                    System.out.println(msg);
                }else{
                    closeEveryting(this.socket, this.bufferedReader, this.bufferedWriter);
                }

            } catch (IOException e) {
                closeEveryting(this.socket, this.bufferedReader, this.bufferedWriter);
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        Socket sock = null;
        try {
            sock = new Socket("localhost", 4000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Client client = new Client(sock, username);
        client.listenMsgs();
        client.sendMessage();
    }

}
