package com.google;

import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;

/**
 * A class used to represent a video.
 */
class Video {

  private final String title;
  private final String videoId;
  private final List<String> tags;
  private String reason;
  private boolean isFlagged;

  Video(String title, String videoId, List<String> tags) {
    this.title = title;
    this.videoId = videoId;
    this.tags = Collections.unmodifiableList(tags);
    isFlagged = false;
  }

  /**
   * Returns the title of the video.
   */
  String getTitle() {
    return title;
  }

  /**
   * Returns the video id of the video.
   */
  String getVideoId() {
    return videoId;
  }

  /**
   * Returns a readonly collection of the tags of the video.
   */
  List<String> getTags() {
    return tags;
  }

  /**
   * Sets the video as flagged with a specific reason.
   */
  public void flag(String reason) {
    isFlagged = true;
    this.reason = reason;
  }

  /**
   * Removes flag from video.
   */
  public void allow() {
    isFlagged = false;
    reason = null;
  }

  /**
   * If video is flagged this returns the reason for it being flagged otherwise this returns null
   */
  public String getReason() {
    return reason;
  }

  /**
   * Returns true if the video has been flagged
   */
  public boolean isFlagged() {
    return isFlagged;
  }

  /**
   * Returns a string representation of the video fields in the format of  "title (video_id)
   * [tags]".
   */
  @Override
  public String toString() {
    Formatter formatter = new Formatter();
    formatter.format("%s (%s) [%s]", title, videoId, String.join(" ", tags));
    if (isFlagged) {
      formatter.format(" - FLAGGED (reason: %s)", reason);
    }
    return formatter.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Video video = (Video) o;
    return Objects.equals(videoId, video.videoId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(videoId);
  }

}
