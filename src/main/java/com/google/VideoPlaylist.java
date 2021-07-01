package com.google;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A class used to represent a Playlist
 */
class VideoPlaylist {

  private final LinkedHashMap<String, Video> videos;
  private final String playlistName;

  public VideoPlaylist(String playlistName) {
    this.playlistName = playlistName;
    this.videos = new LinkedHashMap<>();
  }

  public boolean has(String videoId) {
    return videos.containsKey(videoId);
  }

  public void add(Video video) {
    videos.put(video.getVideoId(), video);
  }

  public List<Video> getVideos() {
    return new ArrayList<>(videos.values());
  }

  public Video remove(String videoId) {
    return videos.remove(videoId);
  }

  @Override
  public String toString() {
    return playlistName;
  }

  public void clear() {
    videos.clear();
  }
}
