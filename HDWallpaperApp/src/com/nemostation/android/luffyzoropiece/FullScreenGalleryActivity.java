package com.nemostation.android.luffyzoropiece;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.WindowManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.DialogFragment;
//import android.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.nemostation.android.luffyzoropiece.adapters.FullScreenGalleryAdapter;
import com.nemostation.android.luffyzoropiece.fragments.FullScreenGalleryFragment;
import com.nemostation.android.luffyzoropiece.models.Category;
import com.nemostation.android.luffyzoropiece.models.Recent;
import com.nemostation.android.luffyzoropiece.util.DataHolder;
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
	boolean mRateUsCheck = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //display full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.full_screen_gallery);
		interstitial = new InterstitialAd(FullScreenGalleryActivity.this);
        interstitial.setAdUnitId("ca-app-pub-5836818937934168/8610725537");
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
		mRateUsCheck = getIntent().getExtras().getBoolean(MainActivity.PARC_RATE_US_CHECK);
		//mDataHolder = getIntent().getExtras().getParcelable(MainActivity.PARC_DATA_HOLDER);
		mFavouritesList = getIntent().getExtras().getStringArrayList(MainActivity.PARC_DATA_FAVOURITES);

		mViewPager = (ViewPager) findViewById(R.id.image_pager);
		
		setFragmentAdapter();
		
		mViewPager.setAdapter(new FullScreenGalleryAdapter(getSupportFragmentManager(), getImageFragments()));
		mViewPager.setCurrentItem(position);
		if ( mRateUsCheck == false ) {
			RateItDialogFragment.show(this, this.getSupportFragmentManager());
		}

        //mAdView = (AdView) findViewById(R.id.full_screen_gallery_ad_view);
        //mAdView.loadAd(new AdRequest.Builder().build());

        //mLinearLayout_ActionBar = (LinearLayout) findViewById(R.id.full_screen_gallery_action_bar);

        //mLinearLayout_ActionBar.setVisibility(View.GONE);
        //mAdView.setVisibility(View.GONE);

        //mLinearLayout_ActionBar.setVisibility(View.VISIBLE);
        //mAdView.setVisibility(View.VISIBLE);
	}

	@Override
	public void finish() {
		// Prepare data intent
		Intent data = new Intent();
		data.putExtra(MainActivity.PARC_POSITION, position);
		// Activity finished ok, return the data
		setResult(MainActivity.RESPONSE_POSITION, data);
		super.finish();
	}
	
	@Override
	public void onBackPressed() {
		//mDataHolder.setFavourites(mFavouritesList);
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
		//mDataHolder.setFavourites(mFavouritesList);
	}
	
	public void removeFavourites(String cat, String img) {
		String dir = Environment.getExternalStorageDirectory() + "/" + this.getString(R.string.app_name) + "/favourites/";
		mFavouritesList.remove(dir + cat + "--" + img);
		//mDataHolder.setFavourites(mFavouritesList);
	}

	public static class RateItDialogFragment extends DialogFragment {
		private static final int LAUNCHES_UNTIL_PROMPT = 5; // Number of app launch then prompt user
		private static final int DAYS_UNTIL_PROMPT = 1; //number of day using. 0 to just check number of app launch
		private static final int MILLIS_UNTIL_PROMPT = DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000;
		private static final String PREF_NAME = "APP_RATER";
		private static final String LAST_PROMPT = "LAST_PROMPT";
		private static final String LAUNCHES = "LAUNCHES";
		private static final String DISABLED = "DISABLED";

		public static void show(Context context, FragmentManager fragmentManager) {
			boolean shouldShow = false;
			SharedPreferences sharedPreferences = getSharedPreferences(context);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			long currentTime = System.currentTimeMillis();
			long lastPromptTime = sharedPreferences.getLong(LAST_PROMPT, 0);
			if (lastPromptTime == 0) {
				lastPromptTime = currentTime;
				editor.putLong(LAST_PROMPT, lastPromptTime);
			}

			if (!sharedPreferences.getBoolean(DISABLED, false)) {
				int launches = sharedPreferences.getInt(LAUNCHES, 0) + 1;
				if (launches > LAUNCHES_UNTIL_PROMPT) {
					if (currentTime > lastPromptTime + MILLIS_UNTIL_PROMPT) {
						shouldShow = true;
					}
				}
				editor.putInt(LAUNCHES, launches);
			}

			if (shouldShow) {
				editor.putInt(LAUNCHES, 0).putLong(LAST_PROMPT, System.currentTimeMillis()).apply();
				new RateItDialogFragment().show(fragmentManager, null);
			} else {
				editor.commit();
			}
		}

		private static SharedPreferences getSharedPreferences(Context context) {
			return context.getSharedPreferences(PREF_NAME, 0);
		}

		@Override
		public AlertDialog onCreateDialog(Bundle savedInstanceState) {
			return new AlertDialog.Builder(getActivity())
					.setTitle(R.string.rate_title)
					.setMessage(R.string.rate_message)
					.setPositiveButton(R.string.rate_positive, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
							Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
							try {
								startActivity(goToMarket);
							} catch (ActivityNotFoundException e) {
								startActivity(new Intent(
										Intent.ACTION_VIEW,
										Uri.parse("http://play.google.com/store/apps/details?id="
												+ getActivity().getPackageName())));
							}
							getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).apply();
							dismiss();
						}
					})
					.setNeutralButton(R.string.rate_remind_later, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismiss();
						}
					})
/*					.setNegativeButton(R.string.rate_never, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							getSharedPreferences(getActivity()).edit().putBoolean(DISABLED, true).apply();
							dismiss();
						}
					})*/
					.create();
		}
	}
}
