package com.dakare.radiorecord.app.database.provider;

import android.content.UriMatcher;
import android.net.Uri;

public class StorageUriMatcher extends UriMatcher {
	
	private static final String AUTHORITY = "com.dakare.radiorecord.app";
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	public static final int DOWNLOAD_AUDIO_URI = 1;
    public static final int DOWNLOAD_AUDIO_ITEM_URI = 2;

	public StorageUriMatcher() {
		super(UriMatcher.NO_MATCH);
	}
	
	static {
		sURIMatcher.addURI(AUTHORITY, "download_audio", DOWNLOAD_AUDIO_URI);
        sURIMatcher.addURI(AUTHORITY, "download_audio/#", DOWNLOAD_AUDIO_ITEM_URI);
	}
	
	public static int matchUri(final Uri uri) {
		return sURIMatcher.match(uri);
	}
}
