package com.example.leon.carcompanion;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Locale;

import static com.example.leon.carcompanion.R.string.speech_not_supported;

public class SpotifyActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String answer;
    private TextView txtSpeechInput;
    ImageButton btnSpeakInput;

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
        btnSpeakInput = (ImageButton) findViewById(R.id.btnSpeakInput);



        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        btnSpeakInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptSpeechInput();
            }
        });
    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        View view = findViewById(R.id.textView).getRootView();
        clickPause(view);
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            //txtSpeechInput.setText("Permission Granted");
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        View view = findViewById(R.id.textView).getRootView();

        // Prüfen, ob Ergebnis von der richtigen Aktivität kommt
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
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
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if(data == null){
                    //txtSpeechInput.setText("No Speech input recognized");
                }

                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //txtSpeechInput.setText(result.get(0));
                    //answer = result.get(0);

                    for (String s:result) {
                        Log.d("MAIN", s);
                    }

                    boolean found = false; //Prüfvariable

                    /*
                    Die ArrayList @result hat mehrere Antworten. Diese werden jeweils in die
                    einzelnen Worte aufgeteilt und dann auf definierte Schlagwörter geprüft
                     */
                    for (String s:result) { //Aufteilung in "Interpretationen"
                        String[] split = s.split(" ");
                        for (String wort:split) { //Aufteilung in die einzelnen Wörter

                            Log.d("MAIN", wort);
                            switch (wort.toUpperCase()){
                                case "WEITER":
                                    found = true;
                                    clickNext(view);
                                    break;
                                case "ZURÜCK":
                                    found = true;
                                    clickBack(view);
                                    break;
                                case "PAUSE":
                                    found = true;
                                    clickPause(view);
                                    break;
                                case "PLAY":
                                    found = true;
                                    clickPlay(view);
                                    break;
                            }
                            if (found)
                                break; //Aus der Schleife
                        }

                        if (found)
                            break;

                    }


                }
                    clickPlay(view);
                break;
            }

        }
    }


    public void clickNext(View view){
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
