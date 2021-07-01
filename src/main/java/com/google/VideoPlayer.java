package com.google;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class VideoPlayer {

  private boolean isPlaying = false;
  private Video currentlyPlaying;
  private final VideoLibrary videoLibrary;
  private final TreeMap<String, VideoPlaylist> videoPlaylists;

  public VideoPlayer(VideoLibrary videoLibrary) {
    this.videoLibrary = videoLibrary;
    this.videoPlaylists = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library\n", videoLibrary.getVideos().size());
  }

  public void showAllVideos() {
    System.out.println("Here's a list of all available videos:");
    videoLibrary
        .getVideos()
        .stream()
        .sorted(Comparator.comparing(Video::getTitle))
        .forEach(System.out::println);
  }

  public void playVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);
    if (video == null) {
      System.out.println("Cannot play video: Video does not exist");
    } else if (video.isFlagged()) {
      System.out.printf("Cannot play video: Video is currently flagged (reason: %s)\n",
          video.getReason());
    } else {
      if (currentlyPlaying != null) {
        stopPlaying();
      }
      startPlaying(video);
      currentlyPlaying = video;
    }
  }

  public void stopVideo() {
    if (currentlyPlaying == null) {
      System.out.println("Cannot stop video: No video is currently playing");
    } else {
      stopPlaying();
      currentlyPlaying = null;
    }
  }

  public void playRandomVideo() {
    // Filter out all unplayable videos
    List<Video> playableVideos = getPlayableVideos(videoLibrary.getVideos());
    if (playableVideos.isEmpty()) {
      System.out.println("No videos available");
    } else {
      int randomIndex = new Random().nextInt(playableVideos.size());
      playVideo(playableVideos.get(randomIndex).getVideoId());
    }
  }

  public void pauseVideo() {
    if (currentlyPlaying == null) {
      System.out.println("Cannot pause video: No video is currently playing");
    } else if (isPlaying) {
      System.out.printf("Pausing video: %s\n", currentlyPlaying.getTitle());
      isPlaying = false;
    } else {
      System.out.printf("Video already paused: %s\n", currentlyPlaying.getTitle());
    }
  }

  public void continueVideo() {
    if (currentlyPlaying == null) {
      System.out.println("Cannot continue video: No video is currently playing");
    } else if (isPlaying) {
      System.out.println("Cannot continue video: Video is not paused");
    } else {
      System.out.printf("Continuing video: %s\n", currentlyPlaying.getTitle());
    }
  }

  public void showPlaying() {
    if (currentlyPlaying == null) {
      System.out.println("No video is currently playing");
    } else {
      System.out.printf("Currently playing: %s %s\n",
          currentlyPlaying,
          isPlaying ? "" : "- PAUSED");
    }
  }

  public void createPlaylist(String playlistName) {
    if (playlistName.contains(" ")) {
      System.out.println("Playlist name cannot contain any whitespace");
    } else if (videoPlaylists.containsKey(playlistName)) {
      System.out.println("Cannot create playlist: A playlist with the same name already exists");
    } else {
      videoPlaylists.put(playlistName, new VideoPlaylist(playlistName));
      System.out.printf("Successfully created new playlist: %s\n", playlistName);
    }
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    if (!videoPlaylists.containsKey(playlistName)) {
      System.out.printf("Cannot add video to %s: Playlist does not exist\n", playlistName);
    } else if (videoLibrary.getVideo(videoId) == null) {
      System.out.printf("Cannot add video to %s: Video does not exist\n", playlistName);
    } else if (videoPlaylists.get(playlistName).has(videoId)) {
      System.out.printf("Cannot add video to %s: Video already added\n", playlistName);
    } else {
      Video video = videoLibrary.getVideo(videoId);
      if (video.isFlagged()) {
        System.out.printf("Cannot add video to %s: Video is currently flagged (reason: %s)\n",
            playlistName,
            video.getReason());
      } else {
        VideoPlaylist playlist = videoPlaylists.get(playlistName);
        playlist.add(video);
        System.out.printf("Added video to %s: %s\n", playlistName, video.getTitle());
      }
    }
  }

  public void showAllPlaylists() {
    if (videoPlaylists.isEmpty()) {
      System.out.println("No playlists exist yet");
    } else {
      System.out.println("Showing all playlists:");
      videoPlaylists.values().forEach(System.out::println);
    }
  }

  public void showPlaylist(String playlistName) {
    if (!videoPlaylists.containsKey(playlistName)) {
      System.out.printf("Cannot show playlist %s: Playlist does not exist", playlistName);
    } else {
      VideoPlaylist playlist = videoPlaylists.get(playlistName);
      List<Video> videos = playlist.getVideos();
      System.out.printf("Showing playlist: %s\n", playlistName);
      if (videos.isEmpty()) {
        System.out.println("No videos here yet");
      } else {
        videos.forEach(System.out::println);
      }
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    if (!videoPlaylists.containsKey(playlistName)) {
      System.out.printf("Cannot remove video from %s: Playlist does not exist", playlistName);
    } else if (videoLibrary.getVideo(videoId) == null) {
      System.out.printf("Cannot remove video from %s: Video does not exist", playlistName);
    } else if (!videoPlaylists.get(playlistName).has(videoId)) {
      System.out.printf("Cannot remove video from %s: Video is not in playlist\n", playlistName);
    } else {
      Video removedVideo = videoPlaylists.get(playlistName).remove(videoId);
      System.out.printf("Removed video from %s: %s\n", playlistName, removedVideo.getTitle());
    }
  }

  public void clearPlaylist(String playlistName) {
    if (!videoPlaylists.containsKey(playlistName)) {
      System.out.printf("Cannot clear playlist %s: Playlist does not exist\n", playlistName);
    } else {
      videoPlaylists.get(playlistName).clear();
      System.out.printf("Successfully removed all videos from %s\n", playlistName);
    }
  }

  public void deletePlaylist(String playlistName) {
    if (!videoPlaylists.containsKey(playlistName)) {
      System.out.printf("Cannot delete playlist %s: Playlist does not exist\n", playlistName);
    } else {
      videoPlaylists.remove(playlistName);
      System.out.printf("Deleted playlist: %s\n", playlistName);
    }
  }

  public void searchVideos(String searchTerm) {
    assert (!hasWhitespace(searchTerm) && !hasSpecialCharacters(searchTerm));

    /*
     * Get videos that have a title containing searchTerm
     */
    List<Video> searchResults = videoLibrary.getVideos().stream()
        .filter(video -> video
            .getTitle()
            .toLowerCase()
            .contains(searchTerm.toLowerCase()))
        .sorted(Comparator.comparing(Video::getTitle))
        .collect(Collectors.toList());

    // only display playable videos
    displayResultsAndGetInput(searchTerm, getPlayableVideos(searchResults));
  }


  public void searchVideosWithTag(String videoTag) {
    assert (!hasWhitespace(videoTag) && !hasSpecialCharacters(videoTag));

    /*
     * Get videos with a tag matching videoTag
     */
    List<Video> searchResults = videoLibrary.getVideos().stream()
        .filter(video -> video
            .getTags()
            .stream()
            .anyMatch(tag -> tag.equalsIgnoreCase(videoTag)))
        .sorted(Comparator.comparing(Video::getTitle))
        .collect(Collectors.toList());

    // only display playable videos
    displayResultsAndGetInput(videoTag, getPlayableVideos(searchResults));
  }

  public void flagVideo(String videoId, String reason) {
    assert (!hasWhitespace(reason));
    if (videoLibrary.getVideo(videoId) == null) {
      System.out.println("Cannot flag video: Video does not exist");
    } else {
      Video video = videoLibrary.getVideo(videoId);
      if (video.isFlagged()) {
        System.out.println("Cannot flag video: Video is already flagged");
      } else {
        video.flag(reason);
        // if flagged video is currently playing, stop it
        if(currentlyPlaying == video){
          stopVideo();
        }
        System.out.printf("Successfully flagged video: %s (reason: %s)\n", video.getTitle(),
            video.getReason());
      }
    }
  }

  public void flagVideo(String videoId) {
    flagVideo(videoId, "Not supplied");
  }

  public void allowVideo(String videoId) {
    if (videoLibrary.getVideo(videoId) == null) {
      System.out.println("Cannot remove flag from video: Video does not exist");
    } else {
      Video video = videoLibrary.getVideo(videoId);
      if(!video.isFlagged()){
        System.out.println("Cannot remove flag from video: Video is not flagged\n");
      }else{
        video.allow();
        System.out.printf("Successfully removed flag from video: %s\n", video.getTitle());
      }
    }
  }

  /* HELPERS */
  private void stopPlaying() {
    System.out.printf("Stopping video: %s\n", currentlyPlaying.getTitle());
    isPlaying = true;
  }

  private void startPlaying(Video video) {
    System.out.printf("Playing video: %s\n", video.getTitle());
    isPlaying = true;
  }

  private boolean hasWhitespace(String line) {
    return line.matches("//s");
  }

  private boolean hasSpecialCharacters(String line) {
    return line.matches("[^a-zA-Z0-9]");
  }

  private void displayResultsAndGetInput(String searchTerm, List<Video> searchResults) {
    if (searchResults.isEmpty()) {
      // On empty results
      System.out.printf("No search results for %s\n", searchTerm);
    } else {
      // display list of results
      System.out.printf("Here are the results for %s:\n", searchTerm);
      for (int i = 0; i < searchResults.size(); i++) {
        System.out.printf("%d) %s\n", i + 1, searchResults.get(i));
      }
      getSearchInput(searchResults);
    }
  }

  private void getSearchInput(List<Video> searchResults) {
    System.out.println(
        "Would you like to play any of the above? If yes, specify the number of the video.\n"
            + "If your answer is not a valid number, we will assume it's a no.");

    var scanner = new Scanner(System.in);
    if (scanner.hasNextInt()) {
      int number = scanner.nextInt();
      // ensure input is in valid range
      if (number >= 1 && number <= searchResults.size()) {
        playVideo(searchResults.get(number - 1).getVideoId());
      }
    }
  }

  // filters a list of videos to get only playable ones (non-flagged ones)
  private List<Video> getPlayableVideos(List<Video> videos) {
    return videos.stream().filter(video -> !video.isFlagged())
        .collect(Collectors.toList());
  }

}