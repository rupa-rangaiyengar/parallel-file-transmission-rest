package com.rupa.parallel.download.rest.utils;

import java.net.URL;

/*
 * @author rupashree com.rupa.utils.Utils needed for REST adapter for the api.
 */
public class Utils {

    public static boolean checkUrl(String fileName){
        if (!fileName.toLowerCase().startsWith("http://") && !fileName.toLowerCase().startsWith("https://"))
            return false;

        URL verifiedUrl = null;
        try {
            verifiedUrl = new URL(fileName);
        } catch (Exception e) {
            return false;
        }

        if (verifiedUrl.getFile().length() < 2)
            return false;

        return true;
    }

    public static boolean checkContentType(String contentType){
        if (contentType.equalsIgnoreCase("text/plain") || contentType.equalsIgnoreCase("text/xml")
                || contentType.equalsIgnoreCase("application/octet-stream") ||
                (contentType.equalsIgnoreCase("text/html") ||
                        contentType.equalsIgnoreCase("multipart/form-data") ||
                        contentType.equalsIgnoreCase("application/json") ||
                        contentType.equalsIgnoreCase("application/xml") ||
                        contentType.equalsIgnoreCase("application/x-www-form-urlencoded")))
            return true;
        else
            return false;
    }

    public static String getFileNameFromURL(String url){
        return  url.substring(url.lastIndexOf('/') + 1);
    }
}
