package me.saidur.bdonlineradio;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
//import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzs.TAG;

public class FmRadioDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String ARGUMENT_IMAGE_RES_ID = "imageResId";
    private static final String ARGUMENT_NAME = "name";
    private static final String ARGUMENT_DESCRIPTION = "description";
    private static final String ARGUMENT_URL = "url";

    /*Radio Player Operaiton Code Start*/
    private ProgressBar playSeekBar;
    private Button buttonPlay;
    private Button buttonStopPlay;
    private MediaPlayer player;
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;
    private Button call;
    String number;
    /*Radio Player Operaiton Code End*/

    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public static FmRadioDetailsFragment newInstance(int imageResId, String name, String description, String url) {

        final Bundle args = new Bundle();
        args.putInt(ARGUMENT_IMAGE_RES_ID, imageResId);
        args.putString(ARGUMENT_NAME, name);
        args.putString(ARGUMENT_DESCRIPTION, description);
        args.putString(ARGUMENT_URL, url);
        final FmRadioDetailsFragment fragment = new FmRadioDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fm_radio_details, container, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.comic_image);
        final TextView nameTextView = (TextView) view.findViewById(R.id.name);
        //final TextView descriptionTextView = (TextView) view.findViewById(R.id.call);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Bundle args = getArguments();
        imageView.setImageResource(args.getInt(ARGUMENT_IMAGE_RES_ID));
        nameTextView.setText(args.getString(ARGUMENT_NAME));
        //final String text = String.format(getString(R.string.description_format), args.getString(ARGUMENT_DESCRIPTION), args.getString(ARGUMENT_URL));
        //descriptionTextView.setText(text);
        String url = String.format(args.getString(ARGUMENT_URL));
        //saidur
        number = String.format(args.getString(ARGUMENT_DESCRIPTION));

        /*Radio Player Operaiton Code Start*/
        initializeUIElements();
        initializeMediaPlayer(url);
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();
         /*Radio Player Operaiton Code End*/


        return view;
    }

    /*Radio Player Operaiton Code Start*/
    private void initControls() {
        try {
            volumeSeekbar = (SeekBar) view.findViewById(R.id.seekBar);
            audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUIElements() {

        playSeekBar = (ProgressBar) view.findViewById(R.id.progressBar1);
        playSeekBar.setMax(100);
        playSeekBar.setVisibility(View.INVISIBLE);

        buttonPlay = (Button) view.findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);

        buttonStopPlay = (Button) view.findViewById(R.id.buttonStopPlay);
        buttonStopPlay.setEnabled(false);
        buttonStopPlay.setOnClickListener(this);

        call = (Button) view.findViewById(R.id.call);
        call.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == buttonPlay) {
            if (isOnline()) {
                startPlaying();
            } else {
                String message = "No Internet Connection";
                Toast.makeText(this.getActivity(), message, Toast.LENGTH_LONG).show();
            }
        } else if (v == buttonStopPlay) {
            stopPlaying();
        } else if (v == call) { //saidur
            callStudent();
        }
    }


    //saidur
    private void callStudent() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getActivity().startActivity(callIntent);
        Log.d(TAG, "Hello");
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    private void startPlaying() {

        buttonStopPlay.setEnabled(true);
        buttonPlay.setEnabled(false);
        playSeekBar.setVisibility(View.VISIBLE);

        final TextView validTextView = (TextView) view.findViewById(R.id.validationtext);
        validTextView.setText("Receiving data please wait for 10-20 seconds...");
        validTextView.setVisibility(View.VISIBLE);

        Timer t = new Timer(false);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    (getActivity()).runOnUiThread(new Runnable() {
                        public void run() {
                            validTextView.setVisibility(View.INVISIBLE);
                        }

                    });
                }
            }
        }, 10000);


        /*try{
            player.prepareAsync();
        }catch (Exception e){
            e.printStackTrace();
        }*/
        //player.prepareAsync();

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });

        /*if(true){
            Toast.makeText(this.getActivity(), "Receiving data please wait...", Toast.LENGTH_LONG).show();
        }*/

    }

    private void stopPlaying() {

        final Bundle args = getArguments();
        String url = String.format(args.getString(ARGUMENT_URL));

        try {
            if (player != null && player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
            initializeMediaPlayer(url);
        } catch (Exception e) {
            e.printStackTrace();
        }


        buttonPlay.setEnabled(true);
        buttonStopPlay.setEnabled(false);
        playSeekBar.setVisibility(View.INVISIBLE);
    }

    private void initializeMediaPlayer(String url) {
        player = new MediaPlayer();
        try {
            //player.setDataSource("http://162.254.149.187:9302");//Radio today
            player.setDataSource(url);
            player.prepareAsync();// might take long! (for buffering, etc)

        } catch (IllegalArgumentException e) {
            Toast.makeText(this.getActivity(), "The Radio may be offline.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Toast.makeText(this.getActivity(), "The Radio may be offline.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this.getActivity(), "The Radio may be offline.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this.getActivity(), "The Radio may be offline.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playSeekBar.setSecondaryProgress(percent);
                //Log.i("Buffering", "" + percent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (player != null) {
                player.stop();
            }
            if (player.isPlaying()) {
                player.stop();
                player.release();
                player = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonPlay.setEnabled(true);
        buttonStopPlay.setEnabled(false);
        playSeekBar.setVisibility(View.INVISIBLE);
    }
/*Radio Player Operaiton Code End*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home: {
                //Log.i(TAG, "Save from fragment");
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
