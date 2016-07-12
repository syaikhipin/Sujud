package com.arifin.sujud.kalma;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.arifin.sujud.R;
import com.arifin.sujud.universal.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * Created by Shahzad on 03-July-15.
 */
public class KalmasOfIslam extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kalmas);
        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);
        context = this;
        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(Utils.Interstitial);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        AdView mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice(Utils.Banner)
                .build();
        mAdView.loadAd(adRequest2);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private int[] mImages = new int[] {
                R.drawable.kalmafst,
                R.drawable.kalmascnd,
                R.drawable.kalmathd,
                R.drawable.kalmafour,
                R.drawable.kalmafiv,
                R.drawable.kalmasi
        };

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            Context context = Fragment6NamesOfAllah.this;
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(
                    R.dimen.padding_small);
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource(mImages[position]);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }
}
