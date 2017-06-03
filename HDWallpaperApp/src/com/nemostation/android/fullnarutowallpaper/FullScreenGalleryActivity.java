package com.nemostation.android.fullnarutowallpaper;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.view.WindowManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.nemostation.android.fullnarutowallpaper.adapters.FullScreenGalleryAdapter;
import com.nemostation.android.fullnarutowallpaper.fragments.FullScreenGalleryFragment;
import com.nemostation.android.fullnarutowallpaper.models.Category;
import com.nemostation.android.fullnarutowallpaper.models.Recent;
import com.nemostation.android.fullnarutowallpaper.util.DataHolder;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class FullScreenGalleryActivity extends BaseActivity {

	private InterstitialAd interstitial;
	//private AdView mAdView;
	private ArrayList<String> mFavouritesList;

	private Recent mRecent;
	private List<String> mFavourites;
	private Category mCategory;
	
	private DataHolder mDataHolder;
	
	private ViewPager mViewPager;
	int position;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //display full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen_gallery);
		interstitial = new InterstitialAd(FullScreenGalleryActivity.this);
        interstitial.setAdUnitId("ca-app-pub-5836818937934168/3443886733");
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitial.loadAd(adRequest);
        interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
            	if(Math.random() < 0.3)
            		displayInterstitial();
            }
        });

		mRecent = getIntent().getExtras().getParcelable(MainActivity.PARC_RECENT);
		mFavourites = getIntent().getExtras().getStringArrayList(MainActivity.PARC_FAVOURITES);
		mCategory = getIntent().getExtras().getParcelable(MainActivity.PARC_CATEGORIES);
		position = getIntent().getExtras().getInt(MainActivity.PARC_POSITION);
		
		mDataHolder = getIntent().getExtras().getParcelable(MainActivity.PARC_DATA_HOLDER);
		mFavouritesList = mDataHolder.getFavourites();

		
		mViewPager = (ViewPager) findViewById(R.id.image_pager);
		
		setFragmentAdapter();
		
		mViewPager.setAdapter(new FullScreenGalleryAdapter(getSupportFragmentManager(), getImageFragments()));
		mViewPager.setCurrentItem(position);

        //mAdView = (AdView) findViewById(R.id.full_screen_gallery_ad_view);
        //mAdView.loadAd(new AdRequest.Builder().build());

        //mLinearLayout_ActionBar = (LinearLayout) findViewById(R.id.full_screen_gallery_action_bar);

        //mLinearLayout_ActionBar.setVisibility(View.GONE);
        //mAdView.setVisibility(View.GONE);

        //mLinearLayout_ActionBar.setVisibility(View.VISIBLE);
        //mAdView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onBackPressed() {
		mDataHolder.setFavourites(mFavouritesList);
		super.onBackPressed();
	}
	
	public void setFragmentAdapter() {
		mViewPager.setAdapter(new FullScreenGalleryAdapter(getSupportFragmentManager(), getImageFragments()));
		mViewPager.setCurrentItem(position);
	}
	
	public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
	
	private List<Fragment> getImageFragments() {
		List<Fragment> fragments = new ArrayList<Fragment>(0);
		if (mRecent != null) {
			for (String image : mRecent.getImages()) {
				fragments.add(FullScreenGalleryFragment.newInstance(image.split("#")[0], image.split("#")[1]));
			}
		} else if (mFavourites != null) {
			for (String image : mFavourites) {
				String catandimage = image.split("/")[image.split("/").length - 1];
				String[] spl = catandimage.split("--");
				fragments.add(FullScreenGalleryFragment.newInstance(spl[spl.length - 2], spl[spl.length - 1]));
			}
		} else if (mCategory != null) {
			for (String image : mCategory.getImages()) {
				fragments.add(FullScreenGalleryFragment.newInstance(mCategory.getName(), image));
			}
		}
		return fragments;
	}
	
	public int getPosition() {
		return position;
	}
	
	public ViewPager getMPager() {
		return mViewPager;
	}
	
	public ArrayList<String> getFavourites() {
		return mFavouritesList;
	}
	
	public void fillFavourites(String cat, String img) {
		String dir = Environment.getExternalStorageDirectory() + "/" + this.getString(R.string.app_name) + "/favourites/";
		mFavouritesList.add(dir + cat + "--" + img);
		mDataHolder.setFavourites(mFavouritesList);
	}
	
	public void removeFavourites(String cat, String img) {
		String dir = Environment.getExternalStorageDirectory() + "/" + this.getString(R.string.app_name) + "/favourites/";
		mFavouritesList.remove(dir + cat + "--" + img);
		mDataHolder.setFavourites(mFavouritesList);
	}
}
