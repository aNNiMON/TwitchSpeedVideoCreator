package com.annimon.tsvc.model;

import org.json.JSONObject;

/**
 *
 * @author aNNiMON
 */
public final class TwitchVideo {

    private String title;
    private String description;
    private int broadcastId;
    private int id;
    private String recordedAt;
    private String game;
    private int length;
    private String previewUrl;
    private String url;

    public TwitchVideo() {
    }
    
    public TwitchVideo(JSONObject json) {
        title = json.getString("title");
        description = json.optString("description");
        broadcastId = json.getInt("broadcast_id");
        String _id = json.getString("_id");
        if (_id.startsWith("v")) _id = _id.substring(1);
        id = Integer.parseInt(_id);
        recordedAt = json.getString("recorded_at");
        game = json.getString("game");
        length = json.getInt("length");
        previewUrl = json.getString("preview");
        url = json.getString("url");
    }

    public TwitchVideo(String title, String description, int broadcastId, int id, String recordedAt, String game, int length, String previewUrl, String url) {
        this.title = title;
        this.description = description;
        this.broadcastId = broadcastId;
        this.id = id;
        this.recordedAt = recordedAt;
        this.game = game;
        this.length = length;
        this.previewUrl = previewUrl;
        this.url = url;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(String recordedAt) {
        this.recordedAt = recordedAt;
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

    @Override
    public String toString() {
        return "TwitchVideos{" + "title=" + title + ", description=" + description + ", broadcastId=" + broadcastId + ", id=" + id + ", recordedAt=" + recordedAt + ", game=" + game + ", length=" + length + ", previewUrl=" + previewUrl + ", url=" + url + '}';
    }
}
