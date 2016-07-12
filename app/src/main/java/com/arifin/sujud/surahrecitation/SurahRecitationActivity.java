package com.arifin.sujud.surahrecitation;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.content.res.AssetManager;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.arifin.sujud.R;
import com.arifin.sujud.universal.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Shahzad Ahmad on 22-Jun-15.
 */
public class SurahRecitationActivity extends AppCompatActivity {
    public String AD_UNIT_ID= "";
    private AdView adView;
    private InterstitialAd interstitial;
    private MediaPlayer player;
    Context context;
    RelativeLayout readQuran;

    int fatiha,rehman,mulk,waqiya,yaseen=0;
    ImageView btnfatiha,btnrehman,btnmulk,btnwaqiya,btnyaseen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surah_recitation);

        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);

        context=this;

        AD_UNIT_ID= Utils.Banner;

//        AD_UNIT_ID= Utils.Banner;

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        // Create the interstitial.
        interstitial = new InterstitialAd(context);
        interstitial.setAdUnitId(Utils.Interstitial);

        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitial.loadAd(adRequest1);

        adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        LinearLayout layout = (LinearLayout)findViewById(R.id.adlayout);
        layout.addView(adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

       readQuran = (RelativeLayout)findViewById(R.id.rlReadQuran);
        readQuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CopyReadAssets();
            }
        });

        btnfatiha= (ImageView)findViewById(R.id.btn_surah_fatiha);
        btnfatiha.setOnClickListener(new View.OnClickListener()
        {   public void onClick(View v)
            {
                SurahFatiha();

            }

        });

        btnmulk= (ImageView)findViewById(R.id.btn_surah_mulk);
        btnmulk.setOnClickListener(new View.OnClickListener()
        {   public void onClick(View v)
            {
                SurahMulk();

            }

        });

        btnrehman= (ImageView)findViewById(R.id.btn_surah_rahman);
        btnrehman.setOnClickListener(new View.OnClickListener()
        {   public void onClick(View v)
            {
                SurahRehman();

            }

        });

        btnwaqiya= (ImageView)findViewById(R.id.btn_surah_waqia);
        btnwaqiya.setOnClickListener(new View.OnClickListener()
        {   public void onClick(View v)
            {
                SurahWaqiya();

            }

        });


        btnyaseen= (ImageView)findViewById(R.id.btn_surah_yaseen);
        btnyaseen.setOnClickListener(new View.OnClickListener()
        {   public void onClick(View v)
            {
                SurahYaseen();

            }

        });
    }
public  void playerChecker(){
    if(fatiha==1||rehman==1||waqiya==1||yaseen==1||mulk==1){
        player.pause();
    }
}
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
       playerChecker();
    }
    public void SurahYaseen(){
        if(fatiha==1||rehman==1||waqiya==1||mulk==1){
            player.pause();
        }
        if (yaseen == 0) {

            player = MediaPlayer.create(context, R.raw.syaseen);
            yaseen = 1;
            player.start();
            btnyaseen.setBackgroundResource(R.drawable.stop);
            btnmulk.setBackgroundResource(R.drawable.play);
            btnrehman.setBackgroundResource(R.drawable.play);
            btnwaqiya.setBackgroundResource(R.drawable.play);
            btnfatiha.setBackgroundResource(R.drawable.play);

        }else{
            player.pause();
            yaseen = 0;
            btnyaseen.setBackgroundResource(R.drawable.play);
        }
    }
    public void SurahMulk(){
        if(fatiha==1||rehman==1||waqiya==1||yaseen==1){
            player.pause();
        }
        if (mulk == 0) {

            player = MediaPlayer.create(context, R.raw.smulk);
            mulk = 1;
            player.start();
            btnmulk.setBackgroundResource(R.drawable.stop);
            btnyaseen.setBackgroundResource(R.drawable.play);
            btnrehman.setBackgroundResource(R.drawable.play);
            btnwaqiya.setBackgroundResource(R.drawable.play);
            btnfatiha.setBackgroundResource(R.drawable.play);

        }else{

            player.pause();
            mulk = 0;
            btnmulk.setBackgroundResource(R.drawable.play);
        }
    }
    public void SurahWaqiya(){
        if(fatiha==1||rehman==1||yaseen==1||mulk==1){
            player.pause();
        }
        if (waqiya == 0) {

            player = MediaPlayer.create(context, R.raw.swaqya);
            waqiya = 1;
            player.start();
            btnwaqiya.setBackgroundResource(R.drawable.stop);
            btnmulk.setBackgroundResource(R.drawable.play);
            btnrehman.setBackgroundResource(R.drawable.play);
            btnyaseen.setBackgroundResource(R.drawable.play);
            btnfatiha.setBackgroundResource(R.drawable.play);

        }else{
            player.pause();
            waqiya = 0;
            btnwaqiya.setBackgroundResource(R.drawable.play);
        }
    }
    public void SurahFatiha(){
        if(rehman==1||waqiya==1||yaseen==1||mulk==1){
            player.pause();
        }
        if (fatiha == 0) {

            player = MediaPlayer.create(context, R.raw.sfatiha);
            fatiha = 1;
            player.start();
            btnfatiha.setBackgroundResource(R.drawable.stop);
            btnmulk.setBackgroundResource(R.drawable.play);
            btnrehman.setBackgroundResource(R.drawable.play);
            btnwaqiya.setBackgroundResource(R.drawable.play);
            btnyaseen.setBackgroundResource(R.drawable.play);

        }else{
            player.pause();
            fatiha = 0;
            btnfatiha.setBackgroundResource(R.drawable.play);
        }
    }
    public void SurahRehman(){
        if(fatiha==1||waqiya==1||yaseen==1||mulk==1){
            player.pause();
        }
        if (rehman == 0) {

            player = MediaPlayer.create(context, R.raw.srahman);
            rehman = 1;
            player.start();
            btnrehman.setBackgroundResource(R.drawable.stop);
            btnmulk.setBackgroundResource(R.drawable.play);
            btnfatiha.setBackgroundResource(R.drawable.play);
            btnwaqiya.setBackgroundResource(R.drawable.play);
            btnyaseen.setBackgroundResource(R.drawable.play);

        }else{
            player.pause();
            rehman = 0;
            btnrehman.setBackgroundResource(R.drawable.play);
        }
    }
    private void CopyReadAssets()
    {
        AssetManager assetManager = getAssets();

        InputStream in = null;
        OutputStream out = null;
        File file = new File(getFilesDir(), "quraan.pdf");
        try
        {
            in = assetManager.open("quraan.pdf");
            out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.parse("file://" + getFilesDir() + "/quraan.pdf"),
                "application/pdf");

        startActivity(intent);
//        finish();
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }
    }
    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
