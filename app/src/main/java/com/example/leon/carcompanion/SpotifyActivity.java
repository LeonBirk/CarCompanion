package com.example.leon.carcompanion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class SpotifyActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    //wird für die Authentifizierung mit Spotify benötigt und erhält man beim Anlegen eines Spotify-Projektes
    private static final String CLIENT_ID = "9f5c963a850a4094b7448c6fc0730d14";
    private static final String REDIRECT_URI = "carcompanion://callback";

    // Request code that will be used to verify if the result comes from correct activity
    private static final int REQUEST_CODE = 1337;

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Prüfen, ob Ergebnis von der richtigen Aktivität kommt
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(SpotifyActivity.this);
                        mPlayer.addNotificationCallback(SpotifyActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("SpotifyActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }


    public void clickNext (View view){
        mPlayer.skipToNext();
        View play = findViewById(R.id.playButton);
        View pause = findViewById(R.id.pauseButton);
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
    }

    public void clickPlay (View view){
        mPlayer.resume();
        View play = findViewById(R.id.playButton);
        View pause = findViewById(R.id.pauseButton);
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
    }

    public void clickBack (View view){
        mPlayer.skipToPrevious();
        View play = findViewById(R.id.playButton);
        View pause = findViewById(R.id.pauseButton);
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
    }

    public void clickPause (View view){
        mPlayer.pause();
        View play = findViewById(R.id.playButton);
        View pause = findViewById(R.id.pauseButton);
        pause.setVisibility(View.INVISIBLE);
        play.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        // Beim schließen der App wird der Player gelöscht
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("SpotifyActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("SpotifyActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("SpotifyActivity", "User logged in");
        // Playlist, welche abgespielt wird
        mPlayer.playUri("spotify:user:spotifycharts:playlist:37i9dQZEVXbMDoHDwVN2tF", 0, 0);
    }

    @Override
    public void onLoggedOut() {
        Log.d("SpotifyActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(int i) {
        Log.d("SpotifyActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("SpotifyActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("SpotifyActivity", "Received connection message: " + message);
    }
}
