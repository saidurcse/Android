package me.saidur.bdonlineradio;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/*https://www.raywenderlich.com/149112/android-fragments-tutorial-introduction*/

public class MainActivity extends AppCompatActivity
        implements FmRadioListFragment.OnFmRadioSelected {

    //private Toolbar mToolbar;
    protected ImageButton toHome;
    protected TextView title;
    protected ImageView icon;

    // variable for showing ads
    InterstitialAd mInterstitialAd;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#20a780"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        //mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);

        // For showing ads
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        /* Code to get Test Ads
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("83599FFCF8BD99650E8645DDC23BDFA4")  // My Galaxy Nexus test phone
                .build();*/
        mAdView.loadAd(adRequest);

        /* Code to get device id:
        String android_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
        Log.i("Android id: ", android_id);*/


        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        // Load ads into Interstitial Ads
        mInterstitialAd.loadAd(adRequest);
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_layout, FmRadioListFragment.newInstance(), "FmRadioList")
                    .commit();
        }
    }

    @Override
    public void OnFmRadioSelected(int imageResId, String name, String description, String url) {
        final FmRadioDetailsFragment detailsFragment =
                FmRadioDetailsFragment.newInstance(imageResId, name, description, url);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.root_layout, detailsFragment, "FmRadioDetails")
                .addToBackStack(null)
                .commit();
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    /*// Code for handling exit application back key operation
    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        //Log.v(DEBUG, "onBackPressed function finished");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Called when leaving the activity : responsible code for ads
     */
    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity : responsible code for ads
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed : responsible code for ads
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_share) {
            createShareIntent();
            return true;
        }

        if (id == R.id.action_rate) {
            // When Rate and Review item in list clicked, go to Google Play.
            Intent iRate = new Intent(Intent.ACTION_VIEW);
            iRate.setData(Uri.parse(getString(R.string.google_play_url)));
            startActivity(iRate);
        }

        if (id == R.id.action_moreapps) {
            // When Rate and Review item in list clicked, go to Google Play.
            Intent iRate = new Intent(Intent.ACTION_VIEW);
            iRate.setData(Uri.parse(getString(R.string.google_play_url_product_list)));
            startActivity(iRate);
        }
        return super.onOptionsItemSelected(item);
    }

    // Create share intent when share item click.
    Intent createShareIntent() {
        String intro = getResources().getString(R.string.intro_message);
        String extra = getResources().getString(R.string.extra_message);
        String gPlayURL = getResources().getString(R.string.google_play_url);
        String appName = getResources().getString(R.string.app_name);
        String here = getResources().getString(R.string.here);
        String message = intro + "" + extra + " " + appName + " " + here + " " + gPlayURL;
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(i, getResources().getString(R.string.share_to)));
        return i;
    }

}
