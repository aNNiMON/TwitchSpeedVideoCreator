package com.annimon.tsvc.tasks;

import com.annimon.tsvc.ExceptionHandler;
import com.annimon.tsvc.Util;
import com.annimon.tsvc.model.TwitchVideo;
import java.util.ArrayList;
import java.util.List;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author aNNiMON
 */
public final class PastBroadcastsTask extends Task<List<TwitchVideo>> {
    
    private static final String VIDEOS_URL = "https://api.twitch.tv/kraken/channels/%s/videos?broadcasts=true";
    
    private final String channel;

    public PastBroadcastsTask(String channel) {
        this.channel = channel;
    }
    
    @Override
    public List<TwitchVideo> call() throws Exception {
        final String jsonRaw = Util.getContent(String.format(VIDEOS_URL, channel));
        final JSONObject jsonBroadcasts = new JSONObject(jsonRaw);
        final JSONArray jsonVideos = jsonBroadcasts.getJSONArray("videos");
        final int length = jsonVideos.length();
        final List<TwitchVideo> videos = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            try {
                videos.add( new TwitchVideo(jsonVideos.getJSONObject(i)) );
            } catch (Exception ex) {
                ExceptionHandler.log("PastBroadcastsTask", ex);
            }
        }
        return videos;
    }

}
