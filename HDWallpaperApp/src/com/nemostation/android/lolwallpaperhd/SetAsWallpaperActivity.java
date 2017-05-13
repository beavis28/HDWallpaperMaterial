package com.nemostation.android.lolwallpaperhd;

import java.io.IOException;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.view.WindowManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.nemostation.android.lolwallpaperhd.fragments.FullScreenGalleryFragment;
//import com.edmodo.cropper.CropImageView;


public class SetAsWallpaperActivity extends BaseActivity {

	//private String categoryName;
	private String image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.set_as_wallpaper_activity);

		getActionBar().hide();

		/*categoryName = getIntent().getStringExtra(
				FullScreenGalleryFragment.FULL_SCREEN_GALLERY_CATEGORY);*/
		image = getIntent().getStringExtra(
				FullScreenGalleryFragment.FULL_SCREEN_GALLERY_IMAGE);

		/*mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
		ImageLoader.getInstance().loadImage(image,
				ImageViewUtil.options, new SimpleImageLoadingListener() {

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						mCropImageView.setImageBitmap(loadedImage);
					}
				});*/
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setAsWallpaper(View v) {
		// May take few seconds, so we run it in background...
		new SetWallpaper().execute();
	}

	private class SetWallpaper extends AsyncTask<Void, Void, Integer> {

		private ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(SetAsWallpaperActivity.this);

			mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			mProgressDialog.setMessage(getString(R.string.please_wait));
			mProgressDialog.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
            //Bitmap bitmap = mCropImageView.getCroppedImage();
			Bitmap bitmap = null;
            WallpaperManager myWallpaperManager = WallpaperManager
                    .getInstance(getApplicationContext());
            try {
                myWallpaperManager.setBitmap(bitmap);
                return 1;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
		}

		@Override
		protected void onPostExecute(Integer result) {
			mProgressDialog.hide();
			if (result == 1) {
				showToast(R.string.wallpaper_set);
				finish();
			} else {
				showToast(R.string.wallpaper_not_set);
			}
		}
	}
}
