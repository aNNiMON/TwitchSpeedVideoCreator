package com.annimon.tsvc.tasks;

import com.annimon.tsvc.Util;
import com.annimon.tsvc.model.TwitchVideo;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author aNNiMON
 */
public final class PastBroadcastsTask implements Task<String, List<TwitchVideo>> {
    
    private static final String VIDEOS_URL = "https://api.twitch.tv/kraken/channels/%s/videos?broadcasts=true";

    @Override
    public List<TwitchVideo> process(String channel) throws Exception {
        final String jsonRaw = Util.getContent(String.format(VIDEOS_URL, channel));
        final JSONObject jsonBroadcasts = new JSONObject(jsonRaw);
        final JSONArray jsonVideos = jsonBroadcasts.getJSONArray("videos");
        for (int i = 0; i < jsonVideos.length(); i++) {
            TwitchVideo video = new TwitchVideo(jsonVideos.getJSONObject(i));
            System.out.println(video);
        }
        
        return null;
    }

}
