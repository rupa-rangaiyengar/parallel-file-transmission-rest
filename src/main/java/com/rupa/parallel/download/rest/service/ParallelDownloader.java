package com.rupa.parallel.download.rest.service;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParallelDownloader extends AbstractDownloader {

    final static Logger logger = Logger.getLogger(ParallelDownloader.class);


    public ParallelDownloader(URL url, String outputFolder, int numConnections) {
        super(url, outputFolder, numConnections);
        Thread t = new Thread(this);
        t.start();
    }

    private void error(String errorMessage) {
        logger.error("ERROR due to: "+ errorMessage);
        setState(AbstractDownloader.ERROR);
    }

    @Override
    public void run() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(10000);

            conn.connect();

            if (conn.getResponseCode() / 100 != 2) {
                error("Http URL Connection could not be opened correctly");
            }

            int contentLength = conn.getContentLength();
            if (contentLength < 1) {
                error("Connection opened but content length is invalid");
            }

            if (fileSize == -1) {
                fileSize = contentLength;
                stateChanged();
                logger.info("File size to be downloaded: " + fileSize);
            }

            if (state == AbstractDownloader.DOWNLOADING) {
                if (downloadThreads.size() == 0)
                {
                    if (fileSize > AbstractDownloader.MIN_DOWNLOAD_SIZE) {
                        int partSize = Math.round(((float)fileSize / noOfConnections) / AbstractDownloader.BLOCK_SIZE) * AbstractDownloader.BLOCK_SIZE;
                        logger.info("Part size for each Thread to be downloaded in bytes : " + partSize);

                        int startByte = 0;
                        int endByte = partSize - 1;
                        HttpDownloadThread aThread = new HttpDownloadThread(1, url, outputFolder + fileName, startByte, endByte);
                        downloadThreads.add(aThread);
                        int i = 2;
                        while (endByte < fileSize) {
                            startByte = endByte + 1;
                            endByte += partSize;
                            aThread = new HttpDownloadThread(i, url, outputFolder + fileSize, startByte, endByte);
                            downloadThreads.add(aThread);
                            ++i;
                        }
                    } else
                    {
                        HttpDownloadThread aThread = new HttpDownloadThread(1, url, outputFolder + fileName, 0, fileSize);
                        downloadThreads.add(aThread);
                    }
                } else {
                    Thread t = new Thread();
                    for (int i = 0; i< downloadThreads.size(); ++i) {
                        if (!downloadThreads.get(i).isFinished())
                            t = new Thread(downloadThreads.get(i));
                        t.start();
                    }
                }

                for (int i = 0; i< downloadThreads.size(); ++i) {
                    downloadThreads.get(i).waitFinish();
                }

                if (state == AbstractDownloader.DOWNLOADING) {
                    setState(AbstractDownloader.COMPLETED);
                }
            }
        } catch (Exception e) {
            error("Exception while trying to download chunks due to : "+e.getMessage());
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }

    private class HttpDownloadThread extends AbstractDownloader.DownloadThread {

        public HttpDownloadThread(int threadID, URL url, String outputFile, int startByte, int endByte) {
            super(threadID, url, outputFile, startByte, endByte);
        }

        @Override
        public void run() {
            BufferedInputStream in = null;
            RandomAccessFile raf = null;

            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                String byteRange = startByteIndex + "-" + endByteIndex;
                conn.setRequestProperty("Range", "bytes=" + byteRange);
                logger.info("Thread Number:  "+ Thread.currentThread().getName() + " Byte Range to download : " + byteRange);
                conn.connect();
                if (conn.getResponseCode() / 100 != 2) {
                    error("Connection has failed!");
                }
                in = new BufferedInputStream(conn.getInputStream());

                raf = new RandomAccessFile(outputFile, "rw");
                raf.seek(startByteIndex);

                byte data[] = new byte[AbstractDownloader.BUFFER_SIZE];
                int numRead;
                while((state == AbstractDownloader.DOWNLOADING) && ((numRead = in.read(data,0, AbstractDownloader.BUFFER_SIZE)) != -1))
                {
                    raf.write(data,0,numRead);
                    startByteIndex += numRead;
                    downloaded(numRead);
                }

                if (state == AbstractDownloader.DOWNLOADING) {
                    isFinished = true;
                }
            } catch (IOException e) {
                error("IOException encountered due to parallel download due to : "+e.getMessage());
            } finally {
                if (raf != null) {
                    try {
                        raf.close();
                    } catch (IOException e) {}
                }

                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        error("IOException due to : "+e.getMessage());
                    }
                }
            }

            logger.info("Thread " + threadID +" completed successfully!" );
        }
    }
}

