package com.nemostation.android.fullnarutowallpaper.util;

import android.content.Context;
import android.widget.ImageView;

import android.graphics.Bitmap;
import com.nemostation.android.fullnarutowallpaper.R;
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
            uri = image.trim();
            if (listener != null) {
                loader.displayImage(uri, imageView, optionsForThumbs, listener);
            } else {
                loader.displayImage(uri, imageView, optionsForThumbs);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
    }


	public static final DisplayImageOptions optionsForThumbs = new DisplayImageOptions.Builder()
			.imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)
			.showImageOnFail(R.drawable.icon_default)
			.showImageForEmptyUri(R.drawable.icon_default)
			.showImageOnLoading(R.drawable.icon_default)
			.cacheInMemory(false).build();

	public static final DisplayImageOptions options = new DisplayImageOptions.Builder()
			.imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.icon_default)
			.showImageForEmptyUri(R.drawable.icon_default)
			.showImageOnLoading(R.drawable.icon_default)
			.cacheInMemory(false).build();

	private static ImageLoaderConfiguration imageConfig;

	private static ImageLoaderConfiguration getImageConfig(Context context) {
		if (imageConfig == null) {
			imageConfig = new ImageLoaderConfiguration.Builder(context)
                    .threadPoolSize(3)
                    .build();
		}
		return imageConfig;
	}
}
