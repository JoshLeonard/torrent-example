package com.distributedfileshare;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

import com.distributedfileshare.Models.ServerState;

public class FileServer implements AutoCloseable {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private static final int TIMEOUT = 10000;
    private volatile ServerState serverState = ServerState.Stopped;

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
        int requestSizeSize = 4;
        
        int versionSize = 4;
        byte[] versionBytes = new byte[versionSize];
        readFully(inputStream, versionBytes);
        int version = ByteBuffer.wrap(versionBytes).getInt();
        if (version != 1) {
            throw new IOException("Unsupported version: " + version);
        }

        byte[] requestSizeBytes = new byte[requestSizeSize];
        readFully(inputStream, requestSizeBytes);
        int requestSize = ByteBuffer.wrap(requestSizeBytes).getInt();
        if (requestSize <= -1) {
            throw new IOException("Request size is -1, indicating no request");
        }
        if (requestSize > 1024 * 1024) {
            throw new IOException("Request size is too large: " + requestSize);
        }

        byte[] request = new byte[requestSize];
        readFully(inputStream, request);
        String fileHash = new String(request, 0, request.length, StandardCharsets.UTF_8);

        System.out.println("Received request for file with hash: " + fileHash);
    }

    private void readFully(BufferedInputStream inputStream, byte[] buffer) throws IOException {
        int totalRead = 0;
        while (totalRead < buffer.length) {
            int bytesRead = inputStream.read(buffer, totalRead, buffer.length - totalRead);
            if (bytesRead == -1) {
                throw new IOException("EOF reached");
            }
            totalRead += bytesRead;
        }
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
