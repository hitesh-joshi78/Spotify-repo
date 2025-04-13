package com.example.spotify.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SpotifyService {
    private String accessToken;
    private String refreshToken;

    public void setTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public JsonNode getTopTracks() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.spotify.com/v1/me/top/tracks?limit=10"))
            .header("Authorization", "Bearer " + accessToken)
            .build();
        return sendRequest(request);
    }

    public JsonNode getCurrentlyPlaying() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.spotify.com/v1/me/player/currently-playing"))
            .header("Authorization", "Bearer " + accessToken)
            .build();
        return sendRequest(request);
    }

    public void playTrack(String trackId) throws IOException {
        String jsonBody = "{\"uris\": [\"spotify:track:" + trackId + "\"]}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.spotify.com/v1/me/player/play"))
            .header("Authorization", "Bearer " + accessToken)
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        sendRequest(request);
    }

    public void pausePlayback() throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.spotify.com/v1/me/player/pause"))
            .header("Authorization", "Bearer " + accessToken)
            .PUT(HttpRequest.BodyPublishers.noBody())
            .build();
        sendRequest(request);
    }

    private JsonNode sendRequest(HttpRequest request) throws IOException {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new ObjectMapper().readTree(response.body());
        } catch (Exception e) {
            throw new IOException("Spotify API request failed", e);
        }
    }
}
