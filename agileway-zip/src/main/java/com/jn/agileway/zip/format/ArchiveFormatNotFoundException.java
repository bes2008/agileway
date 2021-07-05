package com.jn.agileway.zip.format;

public class ArchiveFormatNotFoundException extends RuntimeException{
    public ArchiveFormatNotFoundException() {
        super();
    }

    public ArchiveFormatNotFoundException(String message) {
        super(message);
    }

    public ArchiveFormatNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchiveFormatNotFoundException(Throwable cause) {
        super(cause);
    }
}
