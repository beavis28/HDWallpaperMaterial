package com.nemostation.android.fullnarutoartwallpaper.fragments;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.media.MediaScannerConnection;
import com.nemostation.android.fullnarutoartwallpaper.BaseActivity;
import com.nemostation.android.fullnarutoartwallpaper.FullScreenGalleryActivity;
import com.nemostation.android.fullnarutoartwallpaper.R;
import com.nemostation.android.fullnarutoartwallpaper.util.ImageViewUtil;
import com.nemostation.android.fullnarutoartwallpaper.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FullScreenGalleryFragment extends Fragment implements
		OnClickListener {

	public static final String FULL_SCREEN_GALLERY_CATEGORY = "com.nemostation.android.fullnarutoartwallpaper.FullScreenGalleryActivity."
			+ "FullScreenGalleryFragment.category";
	public static final String FULL_SCREEN_GALLERY_IMAGE = "com.nemostation.android.fullnarutoartwallpaper.FullScreenGalleryActivity."
			+ "FullScreenGalleryFragment.image";

	private ArrayList<String> mFavouritesList;

    public static boolean SHOW_FULL_SCREEN_IMAGE=false;
    public static boolean NOT_SHOW_FULL_SCREEN_IMAGE=true;
	private ImageView mImageView;
	private ProgressBar mBar;

    private AdView mAdView;
    private LinearLayout mLinearLayout_ActionBar;

	//private TextView mImageTitle;
	private TextView mImageCategory;

	private String mCategory;
	private String mImage;

	private ImageView mSetAsWallpaper;
	private ImageView mFavorite;

    private ImageView mDownload;

	private ImageView mShare;
	private ImageView mBack;

	private File fileToSave;

	private SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			mBar.setVisibility(View.GONE);
			if (getActivity() != null) {
                if (failReason.getCause().getMessage() != null) {
                    ((BaseActivity) getActivity())
                            .showToast(getString(R.string.error) + "\n"
                                    + failReason.getCause().getMessage());
                } else {
                    ((BaseActivity) getActivity())
                            .showToast(getString(R.string.error));
                }
			}
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			mBar.setVisibility(View.GONE);
			if (view == null) { // it is configured to set as favorite or share
				BufferedOutputStream out = null;
				try {
					out = new BufferedOutputStream(new FileOutputStream(
							fileToSave));
					loadedImage.compress(CompressFormat.JPEG, 100, out);
					if (!fileToSave.toString().contains("favourites") && !fileToSave.toString().contains("Download")) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_SUBJECT, mImage);
                        share.putExtra(Intent.EXTRA_STREAM,
                                Uri.fromFile(fileToSave));
                        Intent chooser = Intent.createChooser(share,getString(R.string.share));
                        if (chooser != null) {
                            startActivity(chooser);
                        }
                        if (fileToSave.exists()) {
                            fileToSave.delete();
                        }
					} else if (fileToSave.toString().contains("Download")) {
                        // Tell the media scanner about the new file so that it is
                        // immediately available to the user.
                        MediaScannerConnection.scanFile(getActivity(), new String[] { fileToSave.toString() }, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        mBar.setVisibility(View.GONE);
                                    }
                                });
                    }

				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
			}
		}
	};

	public static final FullScreenGalleryFragment newInstance(String category,
			String image) {
		FullScreenGalleryFragment f = new FullScreenGalleryFragment();
		Bundle args = new Bundle(2);
		args.putString(FULL_SCREEN_GALLERY_CATEGORY, category);
		args.putString(FULL_SCREEN_GALLERY_IMAGE, image);
		f.setArguments(args);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mCategory = getArguments().getString(FULL_SCREEN_GALLERY_CATEGORY);
		mImage = getArguments().getString(FULL_SCREEN_GALLERY_IMAGE);
		mFavouritesList = ((FullScreenGalleryActivity) getActivity())
				.getFavourites();

		View v = inflater.inflate(R.layout.full_screen_gallery_item, container,
				false);
		mImageView = (ImageView) v
				.findViewById(R.id.full_screen_gallery_item_image);

        mAdView = (AdView) v .findViewById(R.id.full_screen_gallery_ad_view);
        mLinearLayout_ActionBar = (LinearLayout) v .findViewById(R.id.full_screen_gallery_action_bar);

		mBar = (ProgressBar) v
				.findViewById(R.id.full_screen_gallery_item_imageProgressBar);

		/*mImageTitle = (TextView) v
				.findViewById(R.id.full_screen_gallery_item_image_label);*/
		mImageCategory = (TextView) v
				.findViewById(R.id.full_screen_gallery_item_category_name);

		mSetAsWallpaper = (ImageView) v
				.findViewById(R.id.full_screen_gallery_item_set_as_wallpaper);
		mFavorite = (ImageView) v
				.findViewById(R.id.full_screen_gallery_item_add_to_favorite);
		mShare = (ImageView) v
				.findViewById(R.id.full_screen_gallery_item_share);
		mBack = (ImageView) v.findViewById(R.id.full_screen_gallery_item_back);
        mDownload = (ImageView) v
                .findViewById(R.id.full_screen_gallery_item_download);

		mImageView.setOnClickListener(this);
        if (mImage.contains("http")) {//loading from url
            ImageViewUtil.setImageWithImageLoader(mImageView, getActivity(),
                    mCategory, mImage, listener);
        }
        else {//loading from favorite list
            ImageViewUtil.setImageWithImageLoader(mImageView, getActivity(),
                    mCategory,"file://" + Environment.getExternalStorageDirectory() + "/"
                            + getString(R.string.app_name) + "/favourites/" + mCategory + "--" + mImage, listener);
        }
		//mImageTitle.setTypeface(BaseActivity.sRobotoLight);
		mImageCategory.setTypeface(BaseActivity.sRobotoLight);

        mAdView.loadAd(new AdRequest.Builder().build());

		/*String[] imageSub = mImage.split("[.]");
		if (imageSub.length > 1) {
			mImageTitle.setText(imageSub[0]);
		} else {
			mImageTitle.setText(mImage);
		}*/
		mImageCategory.setText(mCategory.toUpperCase());
		// ovdje raditi
		for (String str : mFavouritesList) {
			String holderFav[] = str.split("[/]");
			// Log.v("holder", holderFav[holderFav.length-1] + mImage);
			if (holderFav[holderFav.length - 1].equals(mCategory + "--"	+ getImgName(mImage))) {
                mFavorite.setImageResource(R.drawable.love_red_icon);
				break;
			} else {
                mFavorite.setImageResource(R.drawable.love_white_icon);
			}
		}

        if (SHOW_FULL_SCREEN_IMAGE) {
            mLinearLayout_ActionBar.setVisibility(View.GONE);
            mAdView.setVisibility(View.GONE);
            mImageCategory.setVisibility(View.GONE);
        } else if (NOT_SHOW_FULL_SCREEN_IMAGE){
            mLinearLayout_ActionBar.setVisibility(View.VISIBLE);
            mAdView.setVisibility(View.VISIBLE);
            mImageCategory.setVisibility(View.VISIBLE);
        }
		// dovde
		mSetAsWallpaper.setOnClickListener(this);
		mFavorite.setOnClickListener(this);
		mShare.setOnClickListener(this);
		mBack.setOnClickListener(this);
        mDownload.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
        String dirShare = Environment.getExternalStorageDirectory() + "/"
                + getString(R.string.app_name);
		String dirFav = Environment.getExternalStorageDirectory() + "/"
				+ getString(R.string.app_name) + "/favourites";
        /*String dirDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)  + "/"
                + getString(R.string.app_name) + "/Download";*/
        String dirDownload = Environment.getExternalStorageDirectory() + "/"
                + getString(R.string.app_name) + "/Download";
        String image = "";
		switch (v.getId()) {
            case R.id.full_screen_gallery_item_image:
                if (SHOW_FULL_SCREEN_IMAGE) {
                    mLinearLayout_ActionBar.setVisibility(View.VISIBLE);
                    mAdView.setVisibility(View.VISIBLE);
                    mImageCategory.setVisibility(View.VISIBLE);
                    SHOW_FULL_SCREEN_IMAGE=false;
                    NOT_SHOW_FULL_SCREEN_IMAGE=true;
                } else if (NOT_SHOW_FULL_SCREEN_IMAGE){
                    mLinearLayout_ActionBar.setVisibility(View.GONE);
                    mAdView.setVisibility(View.GONE);
                    mImageCategory.setVisibility(View.GONE);
                    NOT_SHOW_FULL_SCREEN_IMAGE=false;
                    SHOW_FULL_SCREEN_IMAGE=true;
                }
                break;
            case R.id.full_screen_gallery_item_set_as_wallpaper:
                MainActivity.verifyStoragePermissions(getActivity());
                if (mImage.contains("http")) {//loading from url
                    image = mImage;
                }
                else {
                    image = "file://" + Environment.getExternalStorageDirectory() + "/"
                            + getString(R.string.app_name) + "/favourites/" + mCategory + "--" + mImage;
                }
                ImageLoader.getInstance().loadImage(image, new SimpleImageLoadingListener()
                {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                    {
                        if (getActivity() != null) {
                            WallpaperManager myWallpaperManager = WallpaperManager.getInstance(getActivity().getApplicationContext());
                            try {
                                myWallpaperManager.setBitmap(loadedImage);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (getActivity() != null) {
                                    ((BaseActivity) getActivity())
                                            .showToast(R.string.wallpaper_set);
                                }
                                else {
                                    ((BaseActivity) getActivity())
                                            .showToast(R.string.wallpaper_not_set);
                                }
                            }
                        } else {
                            ((BaseActivity) getActivity())
                                    .showToast(R.string.error);
                        }
                    }
                });
                break;
            case R.id.full_screen_gallery_item_share:
                MainActivity.verifyStoragePermissions(getActivity());
                mBar.setVisibility(View.VISIBLE);
                fileToSave = new File(dirShare, mCategory + "--" + getImgName(mImage));
                if (mImage.contains("http")) {//loading from url
                    ImageLoader.getInstance().loadImage(mImage,	listener);
                }
                else {
                    ImageLoader.getInstance().loadImage("file://" + Environment.getExternalStorageDirectory() + "/"
                            + getString(R.string.app_name) + "/favourites/" + mCategory + "--" + mImage,	listener);
                }
                break;

            case R.id.full_screen_gallery_item_add_to_favorite:
                MainActivity.verifyStoragePermissions(getActivity());
                mBar.setVisibility(View.VISIBLE);
                File file = new File(dirFav, mCategory + "--" + getImgName(mImage));

                if (file.exists()) {
                    file.delete();
                    mBar.setVisibility(View.GONE);
                    if (getActivity() != null) {
                        ((BaseActivity) getActivity())
                                .showToast("Image has been removed from favourites!");
                        mFavorite.setImageResource(R.drawable.love_white_icon);
                        removeFragmentFavourites(mCategory, getImgName(mImage));
                    }
                } else {
                    fileToSave = new File(dirFav, mCategory + "--" + getImgName(mImage));
                    ImageLoader.getInstance().loadImage(mImage,listener);
                    if (getActivity() != null) {
                        ((BaseActivity) getActivity())
                                .showToast("Image has been added to favourites!");
                        mFavorite.setImageResource(R.drawable.love_red_icon);
                        fillFragmentFavourites(mCategory, getImgName(mImage));
                    }
                }
                break;

            case R.id.full_screen_gallery_item_download:
                MainActivity.verifyStoragePermissions(getActivity());
                mBar.setVisibility(View.VISIBLE);
                File dirDownloadfolder = new File(dirDownload);
                fileToSave = new File(dirDownload, mCategory + "--" + getImgName(mImage));
                getAlbumStorageDir(getString(R.string.app_name));
                if (!dirDownloadfolder.exists()) {
                    dirDownloadfolder.mkdirs();
                }
                if (!fileToSave.exists()) {
                    if (mImage.contains("http")) {//loading from url
                        ImageLoader.getInstance().loadImage(mImage,listener);

                    } else {
                        ImageLoader.getInstance().loadImage("file://" + Environment.getExternalStorageDirectory() + "/"
                                + getString(R.string.app_name) + "/favourites/" + mCategory + "--" + mImage,listener);
                    }
                    if (getActivity() != null) {
                        ((BaseActivity) getActivity())
                                .showToast("Image has been saved to " + dirDownload);
                    }
                } else {
                    if (getActivity() != null) {
                        ((BaseActivity) getActivity())
                                .showToast("Image is already existing." + dirDownload);
                    }
                }
                break;

            case R.id.full_screen_gallery_item_back:
                getActivity().onBackPressed();
                break;
		}
	}


	public void fillFragmentFavourites(String cat, String img) {
		String dir = Environment.getExternalStorageDirectory() + "/"
				+ getActivity().getString(R.string.app_name) + "/favourites/";
		mFavouritesList.add(dir + cat + "--" + img);
		((FullScreenGalleryActivity) getActivity()).fillFavourites(mCategory,
				mImage);
	}

	public void removeFragmentFavourites(String cat, String img) {
		String dir = Environment.getExternalStorageDirectory() + "/"
				+ getActivity().getString(R.string.app_name) + "/favourites/";
		mFavouritesList.remove(dir + cat + "--" + img);
		((FullScreenGalleryActivity) getActivity()).removeFavourites(mCategory,
				mImage);
	}

	public static String getImgName(String img) {
		String temp = img.replaceAll("https","");
        temp = temp.replaceAll("http","");
        temp = temp.replaceAll(".com","");
        temp = temp.replaceAll("[^a-zA-Z0-9]","");
        temp = temp.replaceAll("jpg",".jpg");
        temp = temp.replaceAll("png",".png");
        temp = temp.replaceAll("jpeg",".jpeg");
        return temp;
	}

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            ((BaseActivity) getActivity())
                    .showToast("Directory cannot create");
        }
        return file;
    }
}