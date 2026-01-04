package com.distributedfileshare.Models;

public class Manifest {
    private String filename;
    private long fileSize;
    private String fileHash;
    private String fileType;
    private String fileExtension;
    private String fileDescription;
    private String[] IPAddresses;

    public Manifest(String filename, long fileSize, String fileHash, String fileType, String fileExtension, String fileDescription, String[] IPAddresses) {
        this.filename = filename;
        this.fileSize = fileSize;
        this.fileHash = fileHash;
        this.fileType = fileType;
        this.fileExtension = fileExtension;
        this.fileDescription = fileDescription;
        this.IPAddresses = IPAddresses;
    }

    public String getFilename() {
        return filename;
    }

    public long getFileSize() {
        return fileSize;
    }

    public String getFileHash() {
        return fileHash;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getFileDescription() {
        return fileDescription;
    }

    public String[] getIPAddresses() {
        return IPAddresses;
    }
}
