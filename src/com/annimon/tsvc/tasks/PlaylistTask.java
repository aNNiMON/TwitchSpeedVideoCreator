package com.annimon.tsvc.tasks;

import com.annimon.tsvc.Util;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.concurrent.Task;
import org.json.JSONObject;

/**
 * Downloads video playlist.
 * 
 * @author aNNiMON
 */
public final class PlaylistTask extends Task<Void> {
    
    private final String vodId;
    private final Path destFile;

    public PlaylistTask(String vodId, Path destFile) {
        this.vodId = vodId;
        this.destFile = destFile;
    }
    
    @Override
    public Void call() throws Exception {
        updateMessage("Retrieving token");
        final JSONObject token = getToken(vodId);
        
        updateMessage("Retrieving playlist");
        final String playlist = getPlaylist(vodId, token.getString("token"), token.getString("sig"));
        final String m3u8url = getM3U8Url(playlist);
        if (!m3u8url.contains("/")) throw new RuntimeException("Invalid url path");
        final String urlPart = m3u8url.substring(0, m3u8url.lastIndexOf('/') + 1);
        
        updateMessage("Creating full m3u8 playlist");
        final String content = Util.getContent(m3u8url);
        final List<String> lines = Stream.of(content.split(Util.NEW_LINE))
                .map(line -> {
                    if (line.trim().isEmpty()) return line;
                    if (line.startsWith("#")) return line;
                    return urlPart + line.trim();
                })
                .collect(Collectors.toList());
        
        updateMessage("Writing playlist to " + destFile);
        Files.write(destFile, lines);
        return null;
    }
    
    private static JSONObject getToken(String vodId) {
        final String url = "https://api.twitch.tv/api/vods/" + vodId + "/access_token";
        final String jsonRaw = Util.getContent(url);
        return new JSONObject(jsonRaw);
    }
    
    private static String getPlaylist(String vodId, String token, String sig) throws UnsupportedEncodingException {
        final String url = "http://usher.twitch.tv/vod/" + vodId + 
                "?player=twitchweb&allow_source=true&nauthsig=" + sig + 
                "&nauth=" + URLEncoder.encode(token, "UTF-8");
        return Util.getContent(url);
    }
    
    private static String getM3U8Url(String playlist) {
        for (String line : playlist.split("\n")) {
            if (line.startsWith("http")) return line.trim();
        }
        throw new RuntimeException("Unable to get m3u8 playlist");
    }
}