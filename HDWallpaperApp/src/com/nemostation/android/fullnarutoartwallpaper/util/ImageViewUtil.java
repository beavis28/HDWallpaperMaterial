package com.nemostation.android.fullnarutoartwallpaper.util;

import android.content.Context;
import android.widget.ImageView;

import android.graphics.Bitmap;
import com.nemostation.android.fullnarutoartwallpaper.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageViewUtil {

/*	public static void setImageWithImageLoader(ImageView imageView,
			Context context, String categoryName, String image,
			ImageLoadingListener listener) {
		ImageLoader loader = ImageLoader.getInstance();
		if (!loader.isInited()) {
			loader.init(getImageConfig(context));
		}
		try {
			String uri;
			if (categoryName != null) {
				uri = Controller.WALLPAPER_URL + categoryName.trim() + "/"
						+ image.trim();
			} else {
				uri = Controller.WALLPAPER_URL + image.trim();
			}
			if (listener != null) {
				loader.displayImage(uri, imageView, options, listener);
			} else {
				loader.displayImage(uri, imageView, options);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}
*/
    public static void setImageWithImageLoader(ImageView imageView,
                                               Context context, String categoryName, String image,
                                               ImageLoadingListener listener) {
        ImageLoader loader = ImageLoader.getInstance();
        if (!loader.isInited()) {
            loader.init(getImageConfig(context));
        }
        try {
            String uri;
            if (categoryName != null) {
                uri = image.trim();
            } else {
                uri = image.trim();
            }
            if (listener != null) {
                loader.displayImage(uri, imageView, options, listener);
            } else {
                loader.displayImage(uri, imageView, options);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }

/*	public static void setThumbsImageWithImageLoader(ImageView imageView,
			Context context, String categoryName, String image,
			ImageLoadingListener listener) {
		ImageLoader loader = ImageLoader.getInstance();
		if (!loader.isInited()) {
			loader.init(getImageConfig(context));
		}
		try {
			String uri;
			uri = Controller.THUMBS_URL + categoryName.trim() + "/"
					+ image.trim();
			if (listener != null) {
				loader.displayImage(uri, imageView, optionsForThumbs, listener);
			} else {
				loader.displayImage(uri, imageView, optionsForThumbs);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
	}
*/
    public static void setThumbsImageWithImageLoader(ImageView imageView,
                                                     Context context, String categoryName, String image,
                                                     ImageLoadingListener listener) {
        ImageLoader loader = ImageLoader.getInstance();
        if (!loader.isInited()) {
            loader.init(getImageConfig(context));
        }
        try {
            String uri;
            uri = image.trim().replace("_b.jpg","_n.jpg").replace("_b.png","_n.png");
            if (listener != null) {
                loader.displayImage(uri, imageView, optionsForThumbs, listener);
            } else {
                loader.displayImage(uri, imageView, optionsForThumbs);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }


	private static final DisplayImageOptions optionsForThumbs = new DisplayImageOptions.Builder()
			.imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
			.showImageOnFail(R.drawable.icon_default)
			.showImageForEmptyUri(R.drawable.icon_default)
			.showImageOnLoading(R.drawable.icon_default)
            .cacheOnDisk(true)
            .build();

	private static final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.icon_default)
			.showImageForEmptyUri(R.drawable.icon_default)
			.showImageOnLoading(R.drawable.icon_default)
			.build();

	private static ImageLoaderConfiguration imageConfig;

	private static ImageLoaderConfiguration getImageConfig(Context context) {
		if (imageConfig == null) {
			imageConfig = new ImageLoaderConfiguration.Builder(context)
                    .threadPoolSize(2)
                    .diskCacheExtraOptions(480, 320, null)
                    .build();
		}
		return imageConfig;
	}
}
