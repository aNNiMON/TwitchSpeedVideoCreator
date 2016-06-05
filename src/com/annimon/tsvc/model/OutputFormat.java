package com.annimon.tsvc.model;

public enum OutputFormat {
    MP4("mp4"),
    AVI("avi"),
    TS("ts"),
    TS_QUICK("ts", "ts (quick)");

    private final String extension, label;

    OutputFormat(String extension) {
        this(extension, extension);
    }

    OutputFormat(String extension, String label) {
        this.extension = extension;
        this.label = label;
    }

    public String getExtension() {
        return extension;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
