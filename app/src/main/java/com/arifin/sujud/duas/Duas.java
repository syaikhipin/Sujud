package com.arifin.sujud.duas;

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
public class Duas extends AppCompatActivity {
    private InterstitialAd mInterstitialAd;

    Context context;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duas);
        getSupportActionBar ().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar ().setCustomView(R.layout.customactionbar);
        context = this;

        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager_ramadan_dua);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(Utils.Interstitial);

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);

        AdView mAdView = (AdView) findViewById(R.id.adView);
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
        private int[] mImages = new int[]{

//                R.drawable.allah1,
//                R.drawable.allah2,
//                R.drawable.allah3,
//                R.drawable.allah4,
//                R.drawable.allah5,
//                R.drawable.allah6,
//                R.drawable.allah7,
//                R.drawable.allah8,
//                R.drawable.allah9,
//                R.drawable.allah10,
//                R.drawable.allah11,
//                R.drawable.allah12,
//                R.drawable.allah13,
//                R.drawable.screenshot5,
//                R.drawable.screenshot6,
//                R.drawable.screenshot7,
//                R.drawable.screenshot8, R.drawable.screenshot9, R.drawable.screenshot16, R.drawable.screenshot17,
//                R.drawable.screenshot18,
//                R.drawable.screenshot19, R.drawable.screenshot20, R.drawable.screenshot21, R.drawable.screenshot22, R.drawable.screenshot23,
//                R.drawable.screenshot24, R.drawable.screenshot25, R.drawable.screenshot26, R.drawable.screenshot27, R.drawable.screenshot28,
//                R.drawable.screenshot29, R.drawable.screenshot30, R.drawable.screenshot31, R.drawable.screenshot32, R.drawable.screenshot33,
//                R.drawable.screenshot34, R.drawable.screenshot35, R.drawable.screenshot36, R.drawable.screenshot37, R.drawable.screenshot38,
//                R.drawable.screenshot39, R.drawable.screenshot40, R.drawable.screenshot41, R.drawable.screenshot42, R.drawable.screenshot43,
//                R.drawable.screenshot44, R.drawable.screenshot45, R.drawable.screenshot46, R.drawable.screenshot47, R.drawable.screenshot48,
//                R.drawable.screenshot49, R.drawable.screenshot50, R.drawable.screenshot51, R.drawable.screenshot52, R.drawable.screenshot53,
//                R.drawable.screenshot54, R.drawable.screenshot55, R.drawable.screenshot56, R.drawable.screenshot57, R.drawable.screenshot58,
//                R.drawable.screenshot59, R.drawable.screenshot60, R.drawable.screenshot61, R.drawable.screenshot62, R.drawable.screenshot63,
//                R.drawable.screenshot64, R.drawable.screenshot65, R.drawable.screenshot66, R.drawable.screenshot67, R.drawable.screenshot68,
//                R.drawable.screenshot69, R.drawable.screenshot70, R.drawable.screenshot71, R.drawable.screenshot72, R.drawable.screenshot74,
//                R.drawable.screenshot76, R.drawable.screenshot77, R.drawable.screenshot78, R.drawable.screenshot79, R.drawable.screenshot80,
//                R.drawable.screenshot81, R.drawable.screenshot82, R.drawable.screenshot83, R.drawable.screenshot85, R.drawable.screenshot86,
//                R.drawable.screenshot87, R.drawable.screenshot88, R.drawable.screenshot89, R.drawable.screenshot90, R.drawable.screenshot91,
//                R.drawable.screenshot92, R.drawable.screenshot93, R.drawable.screenshot94, R.drawable.screenshot95, R.drawable.screenshot96,
//                R.drawable.screenshot97, R.drawable.screenshot98, R.drawable.screenshot99, R.drawable.screenshot100,

                R.drawable.ayat,
                R.drawable.duaallahslovethumb,
                R.drawable.duachildren,
                R.drawable.duagooddeedsthumb,
                R.drawable.duahealsickthumb,
                R.drawable.duaheartslovethumb,
                R.drawable.duaislamthumb,
                R.drawable.dualaylatulqadrthumb,
//                R.drawable.duaneedythumb,
//                R.drawable.duaquranthumb,
//                R.drawable.duarighteouschildrenthumb,
//                R.drawable.ramadanduathumb
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
