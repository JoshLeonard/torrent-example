package com.distributedfileshare;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import com.distributedfileshare.Models.ServerState;

public class FileServer implements AutoCloseable {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private static final int TIMEOUT = 10000;
    private static final int BUFFER_SIZE = 1024;
    private ServerState serverState = ServerState.Stopped;

    public FileServer() {
        executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void start(int port) throws IOException {
        if (serverState == ServerState.Started) {
            throw new IllegalStateException("Server is already started");
        }
        serverState = ServerState.Started;
        serverSocket = new ServerSocket(port);

        while (true) {
            Socket clientSocket = handleConnection();
            clientSocket.setSoTimeout(TIMEOUT);
            executorService.execute(() -> {
                try {
                    handleClient(clientSocket);
                    handleDisconnection(clientSocket);
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            });

        }
    }

    private Socket handleConnection() throws IOException {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
        return clientSocket;
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(clientSocket.getInputStream());
        waitForRequest(inputStream);
        sendData("Hello, client!".getBytes(), clientSocket);
    }

    private void handleDisconnection(Socket clientSocket) throws IOException {
        clientSocket.close();
        System.out.println("Client disconnected");
    }

    private void waitForRequest(BufferedInputStream inputStream) throws IOException {
        byte[] request = new byte[BUFFER_SIZE];
        int bytesRead = inputStream.read(request);
        String fileHash = new String(request, 0, bytesRead);

        System.out.println("Received request for file with hash: " + fileHash);
    }

    private void sendData(byte[] data, Socket clientSocket) throws IOException {
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write(data, 0, data.length);
        outputStream.flush();
    }

    public void stop() {
        if (serverState == ServerState.Stopped) {
            throw new IllegalStateException("Server is already stopped");
        }
        serverState = ServerState.Stopped;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error stopping file server: " + e.getMessage());
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
