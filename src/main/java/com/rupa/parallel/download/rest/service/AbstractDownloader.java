package com.rupa.parallel.download.rest.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;

import com.rupa.parallel.download.rest.utils.*;
import org.apache.log4j.Logger;

public abstract class AbstractDownloader extends Observable implements Runnable {

    final static Logger logger = Logger.getLogger(AbstractDownloader.class);

    protected URL url;

    protected String outputFolder;

    protected int noOfConnections;

    protected int fileSize;

    protected int state;

    protected int downloaded;

    protected String fileName;

    protected ArrayList<DownloadThread> downloadThreads;

    protected static final int BLOCK_SIZE = Constants.getBlockSize();
    protected static final int BUFFER_SIZE = Constants.getBufferSize();
    protected static final int MIN_DOWNLOAD_SIZE = BLOCK_SIZE * 100;

    public static final String STATUS[] = {"Downloading", "Completed", "Error"};

    public static final int DOWNLOADING = 0;
    public static final int COMPLETED = 1;
    public static final int ERROR = 2;


    protected AbstractDownloader(URL url, String outputFolder, int numConnections) {
        this.url = url;
        this.outputFolder = outputFolder;
        noOfConnections = numConnections;

        String fileURL = url.getFile();

        fileName = Utils.getFileNameFromURL(fileURL);

        logger.info("File name of the file to be downloaded: " + fileName);
        fileSize = -1;
        state = DOWNLOADING;
        downloaded = 0;

        downloadThreads = new ArrayList<DownloadThread>();
    }

    public String getURL() {
        return url.toString();
    }

    public int getFileSize() {
        return fileSize;
    }

    public float getProgress() {
        return ((float) downloaded / fileSize) * 100;
    }

    public int getState() {
        return state;
    }

    protected void setState(int value) {
        state = value;
        stateChanged();
    }

    protected synchronized void downloaded(int value) {
        downloaded += value;
        stateChanged();
    }

    protected void stateChanged() {
        setChanged();
        notifyObservers();
    }

    protected abstract class DownloadThread implements Runnable {
        protected int threadID;
        protected URL url;
        protected String outputFile;
        protected int startByteIndex;
        protected int endByteIndex;
        protected boolean isFinished;
        protected Thread thread;

        public DownloadThread(int threadID, URL url, String outputFile, int startByte, int endByte) {
            this.threadID = threadID;
            this.url = url;
            this.outputFile = outputFile;
            startByteIndex = startByte;
            endByteIndex = endByte;
            isFinished = false;
            thread = new Thread(this);
            thread.start();
        }

        public boolean isFinished() {
            return isFinished;
        }

        public void waitFinish() throws InterruptedException {
            thread.join();
        }

    }
}
