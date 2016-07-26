package com.annimon.tsvc.model;

import com.annimon.tsvc.Util;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONObject;

/**
 *
 * @author aNNiMON
 */
public final class TwitchVideo {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    private String channel;
    private String title;
    private String description;
    private int broadcastId;
    private String id;
    private String recordedAt;
    private String game;
    private int length;
    private String previewUrl;
    private String url;
    
    private Date date;
    private String duraton;

    public TwitchVideo() {
    }
    
    public TwitchVideo(JSONObject json) {
        channel = json.getJSONObject("channel").getString("name");
        title = json.optString("title", "Untitled broadcast");
        description = json.optString("description");
        broadcastId = json.getInt("broadcast_id");
        id = json.getString("_id");
        recordedAt = json.getString("recorded_at");
        date = date(recordedAt);
        game = json.optString("game", "");
        length = json.getInt("length");
        duraton = Util.duration(length);
        previewUrl = json.getString("preview");
        url = json.getString("url");
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(int broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
        date = date(recordedAt);
    }
    
    public Date getDate() {
        return date;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public int getLength() {
        return length;
    }
    
    public void setLength(int length) {
        this.length = length;
        duraton = Util.duration(length);
    }
    
    public String getDuraton() {
        return duraton;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    private Date date(String recordedAt) {
        try {
            return DATE_FORMAT.parse(recordedAt);
        } catch (ParseException ex) {
            return new Date();
        }
    }
    
    @Override
    public String toString() {
        return "TwitchVideos{" + "title=" + title + ", description=" + description + ", broadcastId=" + broadcastId + ", id=" + id + ", recordedAt=" + recordedAt + ", game=" + game + ", length=" + length + ", previewUrl=" + previewUrl + ", url=" + url + '}';
    }
}
