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


    private static final String CLIENT_ID = "53310b0060a3473393b4a425b08aa90f";

    private static final String REDIRECT_URI = "carcompanion://callback";

    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
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

        // Check if result comes from the correct activity
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

    View play = findViewById(R.id.playButton);
    View pause = findViewById(R.id.pauseButton);

    public void clickNext (View view){
        mPlayer.skipToNext();
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);
    }
    public void clickPlay (View view){
        mPlayer.resume();
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);

    }

    public void clickBack (View view){
        mPlayer.skipToPrevious();
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);

    }

    public void clickPause (View view){
        mPlayer.pause();
        pause.setVisibility(View.INVISIBLE);
        play.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
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

        mPlayer.playUri("spotify:user:spotifycharts:playlist:37i9dQZEVXbMDoHDwVN2tF", 0, 0);
        //mPlayer.playUri("spotify:track:6OlsZzPiKA0i2BpqMkbPie", 0, 0);
        //mPlayer.queue("spotify:track:47VRtROAdociHgtuVZNoBL");
        //mPlayer.queue("spotify:track:0SsQNbkgmhYjR6zS5dOGEF");

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
