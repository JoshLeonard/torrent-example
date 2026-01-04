package com.distributedfileshare;

import java.io.IOException;
import java.util.Scanner;
import com.distributedfileshare.Models.Manifest;

/**
 * Main application class for DistributedFileShare console application.
 */
public class App {
    
    public static void main(String[] args) {
        System.out.println("Welcome to DistributedFileShare!");
        System.out.println("=================================");
        
        Scanner scanner = new Scanner(System.in);
        
        boolean running = true;
        while (running) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Load Manifest File");
            System.out.println("2. Start File Server");
            System.out.println("3. Connect to Server (Client)");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    loadAndDisplayManifest(scanner);
                    break;
                case "2":
                    startFileServer(scanner);
                    break;
                case "3":
                    connectToServer(scanner);
                    break;
                case "4":
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private static void loadAndDisplayManifest(Scanner scanner) {
        System.out.println("\n--- Load Manifest File ---");
        System.out.println("Available test files:");
        System.out.println("  - test_manifest.txt (Video file)");
        System.out.println("  - test_manifest2.txt (PDF file)");
        System.out.println("  - test_manifest3.txt (ZIP archive)");
        System.out.print("\nEnter manifest filename: ");
        
        String filename = scanner.nextLine().trim();
        
        try {
            Manifest manifest = ManifestLoader.loadManifest(filename);
            displayManifest(manifest);
        } catch (Exception e) {
            System.err.println("Error loading manifest: " + e.getMessage());
        }
    }
    
    private static void startFileServer(Scanner scanner) {
        System.out.println("\n--- Start File Server ---");
        System.out.print("Enter port number (default: 3000): ");
        
        String portStr = scanner.nextLine().trim();
        
        try {
            int port;
            if (portStr.isEmpty()) {
                port = 3000;
                System.out.println("Using default port: 3000");
            } else {
                port = Integer.parseInt(portStr);
            }
            
            if (port < 1024 || port > 65535) {
                System.err.println("Please use a port between 1024 and 65535");
                return;
            }
            
            Thread serverThread = new Thread(() -> {
                FileServer server = new FileServer();
                System.out.println("Starting server on port " + port + "...");
                System.out.println("Waiting for client connections...");
                
                try {
                    server.start(port);
                } catch (IOException e) {
                    System.err.println("Error starting server: " + e.getMessage());
                } finally {
                    server.stop();
                }
            });

            serverThread.start();

            System.out.println("Server started! Press Enter to return to menu.");
            scanner.nextLine();
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please enter a valid integer.");
        }
    }
    
    private static void connectToServer(Scanner scanner) {
        System.out.println("\n--- Connect to Server ---");
        System.out.print("Enter server IP address (e.g., 127.0.0.1 for localhost): ");
        String ipAddress = scanner.nextLine().trim();
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = "127.0.0.1";
        }
        
        System.out.print("Enter server port number (default: 3000): ");
        String portStr = scanner.nextLine().trim();
        
        try {
            int port;
            if (portStr.isEmpty()) {
                port = 3000;
                System.out.println("Using default port: 3000");
            } else {
                port = Integer.parseInt(portStr);
            }
            
            if (port < 1024 || port > 65535) {
                System.err.println("Please use a port between 1024 and 65535");
                return;
            }
            
            FileRequestClient client = new FileRequestClient();
            System.out.println("Connecting to " + ipAddress + ":" + port + "...");
            client.connect(ipAddress, port);
            System.out.println("Connected successfully!");
            
            // Ask if user wants to request a file
            System.out.print("\nDo you want to request a file? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (response.equals("y") || response.equals("yes")) {
                System.out.print("Enter file hash to request: ");
                String fileHash = scanner.nextLine().trim();
                
                System.out.println("Requesting file with hash: " + fileHash);
                client.requestFile(fileHash);
                
                System.out.println("Receiving data...");
                client.receiveData();

            }
            
            System.out.println("\nClosing connection...");
            client.stop();
            System.out.println("Disconnected from server.");
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please enter a valid integer.");
        }
    }
    
    private static void displayManifest(Manifest manifest) {
        System.out.println("\n=== Manifest Contents ===");
        System.out.println("Filename:     " + manifest.getFilename());
        System.out.println("File Size:    " + formatFileSize(manifest.getFileSize()));
        System.out.println("File Hash:    " + manifest.getFileHash());
        System.out.println("File Type:    " + manifest.getFileType());
        System.out.println("Extension:    " + manifest.getFileExtension());
        System.out.println("Description:  " + manifest.getFileDescription());
        System.out.println("IP Addresses: " + String.join(", ", manifest.getIPAddresses()));
        System.out.println("========================\n");
    }
    
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.2f %sB (%,d bytes)", bytes / Math.pow(1024, exp), pre, bytes);
    }
}


