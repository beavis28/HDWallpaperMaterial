package com.nemostation.android.fulllolwallpaperarts.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.nemostation.android.fulllolwallpaperarts.models.Categories;
import com.google.gson.GsonBuilder;

public class Controller {

	private static final int CONNECTION_TIMEOUT = 45 * 1000; // 45 seconds

	//public static final String ROOT_URL = "http://nemostation.com/android-app-data/";
	//public static final String WALLPAPER_URL = ROOT_URL + "wallpaper/pokemonhd/";
	//public static final String THUMBS_URL = ROOT_URL + "wallpaper/thumbs/";

	private static BufferedReader getInputStream(String urlString)
			throws IOException {
		urlString = urlString.replaceAll(" ", "%20");
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(CONNECTION_TIMEOUT);
		return new BufferedReader(new InputStreamReader(conn.getInputStream()));
	}

	public static Categories fetchCategories() throws IOException {
		//String urlString = WALLPAPER_URL + "/service.php";
		//For Naruto HD Wallpaper App
		//String urlString = "https://raw.githubusercontent.com/NemoNguyen/walldatabase/master/naruto.txt";
		//String urlString = "https://raw.githubusercontent.com/NemoNguyen/walldatabase/master/leagueoflegends.txt";
		String urlString = "https://raw.githubusercontent.com/NemoNguyen/walldatabase/master/dragonballz.txt";
		BufferedReader in = getInputStream(urlString);

		try {
			return new GsonBuilder().create().fromJson(in, Categories.class);
		} finally {
			closeReader(in);
		}
	}

	private static void closeReader(BufferedReader in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}
