package com.rupa.parallel.download.rest.utils;

public class Constants {
    private static final int DEFAULT_DEGREE_OF_PARALLELLISM = 9;
    private static final String DEFAULT_OUTPUT_FOLDER = System.getProperty("user.home") + "/"; // User's home diretory
    private static final int BLOCK_SIZE= 4096;
    private static final int BUFFER_SIZE= 4096;
    public static final int DOWNLOADING = 0;
    public static final int COMPLETED = 1;
    public static final int ERROR = 2;

    public static int getDefaultDegreeOfParallellism() {
        return DEFAULT_DEGREE_OF_PARALLELLISM;
    }

    public static String getDefaultOutputFolder() {
        return DEFAULT_OUTPUT_FOLDER;
    }

    public static int getBlockSize() {
        return BLOCK_SIZE;
    }

    public static int getBufferSize() {
        return BUFFER_SIZE;
    }
}
