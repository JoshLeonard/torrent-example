package com.distributedfileshare;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.distributedfileshare.Models.Manifest;

public class ManifestLoader {
    
    public static Manifest loadManifest(String filename) {
        List<String> manifestFile = loadManifestFile(filename);

        return parseManifest(manifestFile);
    }

    private static Manifest parseManifest(List<String> manifestFile) {
        if (manifestFile == null) {
            throw new IllegalArgumentException("Manifest file is null");
        }

        String fileName = manifestFile.get(0);
        long fileSize = Long.parseLong(manifestFile.get(1));
        String fileHash = manifestFile.get(2);
        String fileType = manifestFile.get(3);
        String fileExtension = manifestFile.get(4);
        String fileDescription = manifestFile.get(5);
        String[] IPAddresses = manifestFile.get(6).split(",");

        return new Manifest(fileName, fileSize, fileHash, fileType, fileExtension, fileDescription, IPAddresses);
    }

    private static List<String> loadManifestFile(String filename) {
        try {
            return Files.readAllLines(Paths.get(filename));
        } catch (IOException e) {
            System.err.println("Error loading manifest file: " + e.getMessage());
            return null;
        }
    }
}
