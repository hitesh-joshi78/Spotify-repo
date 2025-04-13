package com.example.spotify.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spotify.Service.SpotifyService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/spotify")
public class SpotifyController {

     @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    @Autowired
    private SpotifyService spotifyService;

    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String url = "https://accounts.spotify.com/authorize?client_id=" + 
        clientId + "&response_type=code&redirect_uri=" + 
        redirectUri + "&scope=user-read-currently-playing user-top-read user-modify-playback-state";
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", url).build();
    }

    @GetMapping("/callback")
    public String callback(@RequestParam("code") String code) throws IOException, InterruptedException {
        String body = "grant_type=authorization_code&code=" + code + 
                      "&redirect_uri=" + redirectUri + 
                      "&client_id=" + clientId + 
                      "&client_secret=" + clientSecret;

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://accounts.spotify.com/api/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(response.body());

        spotifyService.setTokens(node.get("access_token").asText(), node.get("refresh_token").asText());
        return "Authentication successful!";
    }

    @GetMapping("/top-tracks")
    public ResponseEntity<JsonNode> getTopTracks() throws IOException {
        return ResponseEntity.ok(spotifyService.getTopTracks());
    }

    @GetMapping("/now-playing")
    public ResponseEntity<JsonNode> nowPlaying() throws IOException {
        return ResponseEntity.ok(spotifyService.getCurrentlyPlaying());
    }

    @GetMapping("/play/{id}")
    public ResponseEntity<String> play(@PathVariable String id) throws IOException {
        spotifyService.playTrack(id);
        return ResponseEntity.ok("Now playing track: " + id);
    }

    @GetMapping("/pause")
    public ResponseEntity<String> pause() throws IOException {
        spotifyService.pausePlayback();
        return ResponseEntity.ok("Playback paused.");
    }
}
