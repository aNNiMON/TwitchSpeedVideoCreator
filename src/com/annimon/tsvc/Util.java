package com.annimon.tsvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aNNiMON
 */
public final class Util {
    
    public static final String NEW_LINE = System.lineSeparator();

    public static String getContent(String link) {
        try {
            final URL url = new URL(link);
            return readResponse(url.openStream());
        } catch (IOException ex) {
            Logger.getLogger("Util").log(Level.WARNING, "Util.getContent", ex);
            return "";
        }
    }
    
    public static boolean download(String link, File destFile) {
        try {
            final URL url = new URL(link);
            try ( ReadableByteChannel channel = Channels.newChannel(url.openStream());
                  FileChannel fileChannel = new FileOutputStream(destFile).getChannel() ) {
                fileChannel.transferFrom(channel, 0, Long.MAX_VALUE);
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger("Util").log(Level.WARNING, "Util.download", ex);
            return false;
        }
    }
    
    public static String readResponse(InputStream is) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append(Util.NEW_LINE);
            }
        }
        return response.toString();
    }
    
    public static String duration(int len) {
        final int hours = len / 3600;
        final int minutes = len / 60 % 60;
        final int seconds = len % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}