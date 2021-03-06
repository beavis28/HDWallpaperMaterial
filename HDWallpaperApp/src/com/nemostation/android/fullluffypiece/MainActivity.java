package com.nemostation.android.fullluffypiece;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.app.Activity;

import com.nemostation.android.fullluffypiece.adapters.GridImageAdapter;
import com.nemostation.android.fullluffypiece.models.Categories;
import com.nemostation.android.fullluffypiece.models.Category;
import com.nemostation.android.fullluffypiece.models.Recent;
import com.nemostation.android.fullluffypiece.util.Controller;
import com.nemostation.android.fullluffypiece.util.DataHolder;
import com.nemostation.android.fullluffypiece.util.DialogUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends NavigationDrawerActivity implements
		OnItemClickListener {

	public static final String PARC_RECENT = "com.nemostation.android.fullluffypiece.Recent";
	public static final String PARC_FAVOURITES = "com.nemostation.android.fullluffypiece.Favourites";
	public static final String PARC_CATEGORIES = "com.nemostation.android.fullluffypiece.Categories";
	public static final String PARC_POSITION = "com.nemostation.android.fullluffypiece.Position";
	public static final String PARC_DATA_HOLDER = "com.nemostation.android.fullluffypiece.DataHolder";
	public static final String PARC_DATA_FAVOURITES = "com.nemostation.android.fullluffypiece.DataHolder.getFavourites()";
	public static final String PARC_RATE_US_CHECK = "com.nemostation.android.fullluffypiece.RateUsCheck";
	public static final int REQUEST_POSITION = 2001;
	public static final int RESPONSE_POSITION = 2001;
	public static ArrayList<String> pFavouritesShare;
	private int mPosition = 0;
	private boolean mRateUsCheck = false;
	private AdView mAdView;

	private Dialog mSplashScreenDialog;
	private boolean mSplashScreenOnScreen = true;
	private SyncData mSyncData;
	private DialogUtils dialog;
	public void setFavourites(ArrayList<String> favourites) {
		this.mDataHolder.setFavourites(favourites);
	}

	public ArrayList<String> getFavourites() {
		return this.mDataHolder.getFavourites();
	}

	private static final int NUMBER_OF_FUNCTION_CAT = 5;
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	// Permission method.
	public static boolean verifyStoragePermissions(Activity activity) {
		// Check if we have read or write permission
		boolean checkstatus;
		int writePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int readPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

		if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
			// We don't have permission so prompt the user
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE
			);
			checkstatus = false;
		}
		else {
			checkstatus = true;
		}
		return checkstatus;
	}

	public static String getImgName(String img) {
		String temp = img.replaceAll("https","");
		temp = temp.replaceAll("http","");
		temp = temp.replaceAll(".com","");
		temp = temp.replaceAll("farm","");
		temp = temp.replaceAll("staticflickr","");
		temp = temp.replaceAll("[^a-zA-Z0-9]","");
		temp = temp.replaceAll("jpg",".jpg");
		temp = temp.replaceAll("png",".png");
		temp = temp.replaceAll("jpeg",".jpeg");
		return temp;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		mDataHolder = new DataHolder();
		mSyncData = new SyncData();
		mSyncData.execute();

		mAdView = (AdView) findViewById(R.id.main_activity_ad_view);
		mAdView.loadAd(new AdRequest.Builder().build());

		mGrid.setOnItemClickListener(this);

		dialog = new DialogUtils(this);

		showSplashScreen();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		mGrid.setNumColumns(getResources().getInteger(
				R.integer.grid_num_columns));
	}

	@Override
	protected void onDestroy() {

        if (dialog != null) {
            dialog.dismissDialog();
        }
        super.onDestroy();
		if (mSyncData != null) {
			mSyncData.cancel(true);
		}
		if (mAdView != null) {
			mAdView.destroy();
		}
	}

	private void showSplashScreen() {
		mSplashScreenDialog = new Dialog(this, R.style.SplashScreenStyle);
		mSplashScreenDialog.setContentView(R.layout.splash_screen);
		mSplashScreenDialog.show();
		mSplashScreenDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				finish();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mAdView != null) {
			mAdView.resume();
		}
		if (mDataHolder != null && !mSplashScreenOnScreen) {
			Dialog progressDialog = ProgressDialog.show(MainActivity.this, "",
					getString(R.string.please_wait));
			;
			SyncData data;
			if (currentSelectedItem == 0) {
				data = new SyncData(progressDialog, 2, "recent");
			} else if (currentSelectedItem == 1) {
				data = new SyncData(progressDialog, 2);
			} else {
				data = new SyncData(progressDialog, 2, "category");
			}
			data.execute();
		}
		mSplashScreenOnScreen = false;
	}

	@Override
	public void onPause() {
		if (mAdView != null) {
			mAdView.pause();
		}
		super.onPause();
	}

	public void hideSplashScreen() {
		if (mSplashScreenDialog != null) {
			mSplashScreenDialog.dismiss();
			mSplashScreenDialog = null;
		}
		mSyncData = null;
	}

	@Override
	public void setGridAdapter(int position) {
		ArrayList<String> random;
		GridImageAdapter adapter;
		getActionBar().show();
		if (position < NUMBER_OF_FUNCTION_CAT) {
			if (position == 0) {
				// Recent will be random picture
				// to make user think that everyday is new pic
				random = new ArrayList<String>();
				for (String st : mDataHolder.getRecent().getImages()) {
					random.add(st);
				}
				Collections.shuffle(random);
				mDataHolder.getRecent().setImages(random);
				adapter = new GridImageAdapter(this,
						mDataHolder.getRecent(),
						mDataHolder.getFavourites());
				mGrid.setAdapter(adapter);
				// end of random
				//mGrid.setAdapter(new GridImageAdapter(this, mDataHolder.getRecent(), mDataHolder.getFavourites()));
			} else if (position == 1) {
				Dialog progressDialog = ProgressDialog.show(MainActivity.this,
						"", getString(R.string.please_wait));
				SyncData data = new SyncData(progressDialog, 11);
				data.execute();
			} else if (position == 2) {
				currentSelectedItem = 0;
				mGrid.setAdapter(new GridImageAdapter(this, mDataHolder
						.getRecent(), mDataHolder.getFavourites()));
				Intent aboutUs = new Intent(this, AboutUsActivity.class);
				startActivity(aboutUs);
			} else if (position == 3) {
				currentSelectedItem = 0;
				mRateUsCheck = true;
				mGrid.setAdapter(new GridImageAdapter(this, mDataHolder
						.getRecent(), mDataHolder.getFavourites()));
				Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
				Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(goToMarket);
				} catch (ActivityNotFoundException e) {
					startActivity(new Intent(
							Intent.ACTION_VIEW,
							Uri.parse("http://play.google.com/store/apps/details?id="
									+ this.getPackageName())));
				}
			} else if (position == 4) {
				currentSelectedItem = 0;
				mGrid.setAdapter(new GridImageAdapter(this, mDataHolder
						.getRecent(), mDataHolder.getFavourites()));
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri
						.parse(getString(R.string.ANDROID_DEV_STORE)));
				startActivity(intent);
			}
		} else {
			mGrid.setAdapter(new GridImageAdapter(this, mDataHolder
					.getCategories().get(position - NUMBER_OF_FUNCTION_CAT), mDataHolder
					.getFavourites()));
		}
	}

	@Override
	public void setTitle(int position) {
		if (position < NUMBER_OF_FUNCTION_CAT) {
			if (position == 0) {
				setTitle(getString(R.string.recent));
			} else if (position == 1) {
				setTitle(getString(R.string.favourites));
			} else if (position == 2) {
				setTitle(getString(R.string.about_us));
			} else if (position == 3) {
				setTitle(getString(R.string.rate_app));
			} else if (position == 4) {
				setTitle(getString(R.string.more_apps));
			}

		} else {
			setTitle(mDataHolder.getCategories().get(position - NUMBER_OF_FUNCTION_CAT).getName());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, FullScreenGalleryActivity.class);

		if (currentSelectedItem < NUMBER_OF_FUNCTION_CAT) {
			if (currentSelectedItem == 0) {
				intent.putExtra(PARC_RECENT, mDataHolder.getRecent());
				intent.putExtra(PARC_DATA_FAVOURITES, mDataHolder.getFavourites());
				//intent.putExtra(PARC_DATA_HOLDER, mDataHolder);
			} else if (currentSelectedItem == 1) {
				intent.putStringArrayListExtra(PARC_FAVOURITES,
						mDataHolder.getFavourites());
				intent.putExtra(PARC_DATA_FAVOURITES, mDataHolder.getFavourites());
				//intent.putExtra(PARC_DATA_HOLDER, mDataHolder);
			} else if (currentSelectedItem == 2) {
				intent.putExtra(PARC_RECENT, mDataHolder.getRecent());
				intent.putExtra(PARC_DATA_FAVOURITES, mDataHolder.getFavourites());
				//intent.putExtra(PARC_DATA_HOLDER, mDataHolder);
			} else if (currentSelectedItem == 3) {
				intent.putExtra(PARC_RECENT, mDataHolder.getRecent());
				intent.putExtra(PARC_DATA_FAVOURITES, mDataHolder.getFavourites());
				//intent.putExtra(PARC_DATA_HOLDER, mDataHolder);
			} else if (currentSelectedItem == 4) {
				intent.putExtra(PARC_RECENT, mDataHolder.getRecent());
				intent.putExtra(PARC_DATA_FAVOURITES, mDataHolder.getFavourites());
				//intent.putExtra(PARC_DATA_HOLDER, mDataHolder);
			}
		} else {
			intent.putExtra(PARC_CATEGORIES,
					mDataHolder.getCategories().get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT));
			intent.putExtra(PARC_DATA_FAVOURITES, mDataHolder.getFavourites());
			//intent.putExtra(PARC_DATA_HOLDER, mDataHolder);
		}
		intent.putExtra(PARC_POSITION, position);
		intent.putExtra(PARC_RATE_US_CHECK, mRateUsCheck);
		//startActivity(intent);
		startActivityForResult(intent, REQUEST_POSITION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESPONSE_POSITION && requestCode == REQUEST_POSITION) {
			if (data.hasExtra(PARC_POSITION)) {
				this.mPosition = data.getExtras().getInt(MainActivity.PARC_POSITION);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		ArrayList<String> random;
		GridImageAdapter adapter;
		switch (item.getItemId()) {
		/*case R.id.action_search:
			if (currentSelectedItem != 2)
				dialog.showDialog();
			return true;*/
		case R.id.action_refresh:
			Dialog progressDialog = ProgressDialog.show(MainActivity.this, "",
					getString(R.string.please_wait));
			SyncData data;
			if (currentSelectedItem < NUMBER_OF_FUNCTION_CAT) {
				if (currentSelectedItem == 0) {
					data = new SyncData(progressDialog, 10);
				} else if (currentSelectedItem == 1) {
					data = new SyncData(progressDialog, 11);
				} else {
					data = new SyncData();
					if (progressDialog != null)
					{
						progressDialog.dismiss();
					}
				}
			} else {
				data = new SyncData(progressDialog, 12);
			}

			data.execute();
			return true;
		case R.id.random:
			if (currentSelectedItem < NUMBER_OF_FUNCTION_CAT) {
				if (currentSelectedItem == 0) {
					random = new ArrayList<String>();
					for (String st : mDataHolder.getRecent().getImages()) {
						random.add(st);
					}
					Collections.shuffle(random);
					mDataHolder.getRecent().setImages(random);
					adapter = new GridImageAdapter(this,
							mDataHolder.getRecent(),
							mDataHolder.getFavourites());
					mGrid.setAdapter(adapter);
				} else if (currentSelectedItem == 1) {
					Collections.shuffle(mDataHolder.getFavourites());
					adapter = new GridImageAdapter(this,
							mDataHolder.getFavourites());
					mGrid.setAdapter(adapter);
				} else {

				}
			} else {
				random = new ArrayList<String>();
				for (String st : mDataHolder.getCategories()
						.get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT).getImages()) {
					random.add(st);
				}
				Collections.shuffle(random);
				mDataHolder.getCategories().get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT)
						.setImages(random);
				adapter = new GridImageAdapter(this, mDataHolder
						.getCategories().get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT),
						mDataHolder.getFavourites());
				mGrid.setAdapter(adapter);
			}
			return true;
		case R.id.sort_up:
			if (currentSelectedItem < NUMBER_OF_FUNCTION_CAT) {
				if (currentSelectedItem == 0) {
					Collections.sort(mDataHolder.getRecent().getImages());
					adapter = new GridImageAdapter(this,
							mDataHolder.getRecent(),
							mDataHolder.getFavourites());
					mGrid.setAdapter(adapter);
				} else if (currentSelectedItem == 1) {
					Collections.sort(mDataHolder.getFavourites());
					adapter = new GridImageAdapter(this,
							mDataHolder.getFavourites());
					mGrid.setAdapter(adapter);
				} else {

				}
			} else {
				Collections.sort(mDataHolder.getCategories()
						.get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT).getImages());
				adapter = new GridImageAdapter(this, mDataHolder
						.getCategories().get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT),
						mDataHolder.getFavourites());
				mGrid.setAdapter(adapter);
			}
			return true;
		case R.id.sort_down:
			Comparator comparator = Collections.reverseOrder();
			if (currentSelectedItem < NUMBER_OF_FUNCTION_CAT) {
				if (currentSelectedItem == 0) {
					Collections.sort(mDataHolder.getRecent().getImages(),
							comparator);
					adapter = new GridImageAdapter(this,
							mDataHolder.getRecent(),
							mDataHolder.getFavourites());
					mGrid.setAdapter(adapter);
				} else if (currentSelectedItem == 1) {
					Collections.sort(mDataHolder.getFavourites(), comparator);
					adapter = new GridImageAdapter(this,
							mDataHolder.getFavourites());
					mGrid.setAdapter(adapter);
				} else {

				}
			} else {
				Collections.sort(
						mDataHolder.getCategories()
								.get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT).getImages(),
						comparator);
				adapter = new GridImageAdapter(this, mDataHolder
						.getCategories().get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT),
						mDataHolder.getFavourites());
				mGrid.setAdapter(adapter);
			}
			return true;
		case R.id.more_apps:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri
					.parse(getString(R.string.ANDROID_DEV_STORE)));
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void search(String result) {
		Dialog progressDialog = ProgressDialog.show(MainActivity.this, "",
				getString(R.string.please_wait));
		progressDialog.show();
		GridImageAdapter adapter;
		ArrayList<String> al = new ArrayList<String>();
		String text = result.toUpperCase();
		if (currentSelectedItem < NUMBER_OF_FUNCTION_CAT) {
			if (currentSelectedItem == 0) {
				for (String st : mDataHolder.getRecent().getImages()) {
					if (st.toUpperCase().contains(text)) {
						al.add(st);
					}
				}
				mDataHolder.getRecent().setImages(al);
				adapter = new GridImageAdapter(this, mDataHolder.getRecent(),
						mDataHolder.getFavourites());
				mGrid.setAdapter(adapter);
			} else if (currentSelectedItem == 1) {
				for (String st : mDataHolder.getFavourites()) {
					if (st.toUpperCase().contains(text)) {
						al.add(st);
					}
				}
				mDataHolder.setFavourites(new ArrayList<String>());
				for (String st : al) {
					mDataHolder.getFavourites().add(st);
				}
				adapter = new GridImageAdapter(this,
						mDataHolder.getFavourites());
				mGrid.setAdapter(adapter);
			} else {
				progressDialog.dismiss();
			}
		} else {
			for (String st : mDataHolder.getCategories()
					.get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT).getImages()) {
				if (st.toUpperCase().contains(text)) {
					al.add(st);
				}
			}
			mDataHolder.getCategories().get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT)
					.setImages(al);
			adapter = new GridImageAdapter(this, mDataHolder.getCategories()
					.get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT), mDataHolder.getFavourites());
			mGrid.setAdapter(adapter);
		}
		progressDialog.dismiss();
	}

	private class SyncData extends AsyncTask<Void, Void, Integer> {
		private Dialog progressDialog;
		private int selection;
		private Recent recent;
		private ArrayList<String> favourites;
		private ArrayList<Category> categories;
		private String pressCategory;

		public SyncData() {
			super();
		}

		public SyncData(Dialog progressDialog, int selection) {
			this.progressDialog = progressDialog;
			this.selection = selection;
			this.progressDialog.show();
		}

		public SyncData(Dialog progressDialog, int selection, String pressCategory) {
			this.progressDialog = progressDialog;
			this.selection = selection;
			this.progressDialog.show();
			this.pressCategory = pressCategory;
		}

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				if (selection != 2 && selection != 3 && selection != 4) {
					Categories allCategories = Controller.fetchCategories();
					recent = allCategories.getRecent();
					favourites = new ArrayList<String>();
					String dir = Environment.getExternalStorageDirectory()
							+ "/" + getString(R.string.app_name)
							+ "/favourites";
					File dirFav = new File(dir);
					dirFav.mkdirs();
					if (dirFav.listFiles()!=null) {
                        for (File favFolders : dirFav.listFiles()) {
                            if (!favFolders.isDirectory())
                                favourites.add(favFolders.toString());
                        }
                    }
					categories = allCategories.getCategories();
					mDataHolder.setFavourites(favourites);
					mDataHolder.setRecent(recent);
					mDataHolder.setCategories(categories);
				} else {
					favourites = new ArrayList<String>();
					String dir = Environment.getExternalStorageDirectory()
							+ "/" + getString(R.string.app_name)
							+ "/favourites";
					File dirFav = new File(dir);
					dirFav.mkdirs();
                    if (dirFav.listFiles()!=null) {
                        for (File favFolders : dirFav.listFiles()) {
                            if (!favFolders.isDirectory())
                                favourites.add(favFolders.toString());
                        }
                    }
					mDataHolder.setFavourites(favourites);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return -1;
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			int position = mPosition;
			if (result == 1) {
				if (selection == 2 || selection == 3 || selection == 4) {
					if(pressCategory != null && pressCategory.equals("recent")) {
						/*Comment out to not create new grid view. Keep using the old one.*/
						/*mGrid.setAdapter(new GridImageAdapter(MainActivity.this,
								mDataHolder.getRecent(), favourites));*/
					/*	ArrayList<String> ImageListCurrent;
						GridImageAdapter adapter;
						ImageListCurrent = new ArrayList<String>();
						for(int i=0; i < mDataHolder.getRecent().getImages().size(); i++) {
							if (position < mDataHolder.getRecent().getImages().size()) {
								ImageListCurrent.add(mDataHolder.getRecent().getImages().get(position));
								position++;
							} else {
								ImageListCurrent.add(mDataHolder.getRecent().getImages().get(mPosition + i - mDataHolder.getRecent().getImages().size()));
							}
						}

						mDataHolder.getRecent().setImages(ImageListCurrent);
						adapter = new GridImageAdapter(MainActivity.this,
								mDataHolder.getRecent(),
								mDataHolder.getFavourites());
						mGrid.setAdapter(adapter);
						*/

					} else if(pressCategory != null && pressCategory.equals("category")) {
						mGrid.setAdapter(new GridImageAdapter(MainActivity.this,
								mDataHolder.getCategories().get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT), favourites));
					} else {
						mGrid.setAdapter(new GridImageAdapter(MainActivity.this,
								favourites));
					}
				} else if (selection == 10) {
					mGrid.setAdapter(new GridImageAdapter(MainActivity.this,
							recent, favourites));
				} else if (selection == 11) {
					mGrid.setAdapter(new GridImageAdapter(MainActivity.this,
							favourites));
				} else if (selection == 12) {
					mGrid.setAdapter(new GridImageAdapter(MainActivity.this,
							categories.get(currentSelectedItem - NUMBER_OF_FUNCTION_CAT), favourites));
				}
				if (progressDialog != null) {
					progressDialog.dismiss();
				} else {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					hideSplashScreen();
					selectItem(0);
				}
			} else {
				showToast("No Internet Connection!!!");
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
			}
			selection = 0;
		}
	}
}

