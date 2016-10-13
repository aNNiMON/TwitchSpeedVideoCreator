package com.annimon.tsvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author aNNiMON
 */
public final class Util {
    
    public static final String NEW_LINE = System.lineSeparator();

    public static String getContent(String link) {
        try {
            final URL url = new URL(link);
            final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestProperty(decodeString(372577544, 2400345),
                    decodeString(89481936, 88629715, 185627395, 257786946, 242029274, 358672002, 26));
            urlConn.setUseCaches(false);
            urlConn.setDoOutput(true);
            return readResponse(urlConn.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger("Util").log(Level.WARNING, "Util.getContent", ex);
            return "";
        }
    }

    public static boolean download(String link, File destFile) {
        return download(link, destFile, false);
    }
    
    public static boolean download(String link, File destFile, boolean append) {
        try {
            final URL url = new URL(link);
            try ( ReadableByteChannel channel = Channels.newChannel(url.openStream());
                  FileChannel fileChannel = new FileOutputStream(destFile, append).getChannel() ) {
                fileChannel.transferFrom(channel, append ? (fileChannel.size()) : 0, Long.MAX_VALUE);
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger("Util").log(Level.WARNING, "Util.download", ex);
            return false;
        }
    }

    public static void joinFiles(File destFile, File additionFile) {
        try ( FileChannel addChannel = new FileInputStream(additionFile).getChannel();
              FileChannel destChannel =  new FileOutputStream(destFile, true).getChannel();
            ) {
            destChannel.transferFrom(addChannel, destChannel.size(), addChannel.size());
        } catch (IOException ex) {
            Logger.getLogger("Util").log(Level.WARNING, "Util.joinFiles", ex);
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
    
    public static boolean isFFmpegExists() {
        try {
            Process ffmpeg = Runtime.getRuntime().exec("ffmpeg -version");
            return (ffmpeg.waitFor() == 0);
        } catch (InterruptedException | IOException ex) {
            return false;
        }
    }

    private static String decodeString(int... data) {
        final int[] s = { 0, 1, 4, 6, 7, 8, 9, 11, 23, 24, 29, 54, 55,
            57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 69, 70, 72, 75, 77 };
        return IntStream.of(data)
                .boxed()
                .map(code -> {
                    final int[] result = new int[5];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = (code >> (6 * i)) & 0x3f;
                    }
                    return result;
                })
                .flatMapToInt(arr -> IntStream.of(arr)
                        .map(pos -> s[pos])
                        .map(i -> i == 0 ? ' ' : (i + 44))
                )
                .mapToObj(ch -> String.valueOf((char)ch))
                .collect(Collectors.joining()).trim();
    }
}