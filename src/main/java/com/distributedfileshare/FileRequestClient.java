package com.distributedfileshare;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class FileRequestClient {
    private Socket clientSocket;
    private BufferedInputStream bufferedInputStream;
    private OutputStream outputStream;

    public void connect(String ipAddress, int port) {
        try {
            clientSocket = new Socket(ipAddress, port);
            bufferedInputStream = new BufferedInputStream(clientSocket.getInputStream());
            outputStream = clientSocket.getOutputStream();
        } catch (IOException e) {
            System.err.println("Error connecting to file server: " + e.getMessage());
        }
    }

    public void requestFile(String fileHash) {
        try {
            outputStream.write(fileHash.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error requesting file: " + e.getMessage());
        }
    }

    public void receiveData() {
        try {
            byte[] data = new byte[1024];
            int bytesRead = bufferedInputStream.read(data);
            String dataString = new String(data, 0, bytesRead);
            System.out.println("Received " + data.length + " bytes");

            System.out.println("Data: " + dataString);
        } catch (IOException e) {
            System.err.println("Error receiving data: " + e.getMessage());
        }
    }

    public void stop() {
        try {
            bufferedInputStream.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error stopping file request client: " + e.getMessage());
        }
    }
}
