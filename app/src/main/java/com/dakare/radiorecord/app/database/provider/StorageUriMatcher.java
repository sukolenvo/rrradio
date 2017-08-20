package com.dakare.radiorecord.app.database.provider;

import android.content.UriMatcher;
import android.net.Uri;

public class StorageUriMatcher extends UriMatcher {

    private static final String AUTHORITY = "com.dakare.radiorecord.app";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int DOWNLOAD_AUDIO_URI = 1;
    public static final int DOWNLOAD_AUDIO_ITEM_URI = 2;
    public static final int SECTION_PATH = 3;
    public static final int SECTION_MUSIC = 4;
    public static final int TOPS_CACHE = 5;
    public static final int HISTORY_DATE = 6;
    public static final int HISTORY_MUSIC = 7;

    public StorageUriMatcher() {
        super(UriMatcher.NO_MATCH);
    }

    static {
        sURIMatcher.addURI(AUTHORITY, "download_audio", DOWNLOAD_AUDIO_URI);
        sURIMatcher.addURI(AUTHORITY, "download_audio/#", DOWNLOAD_AUDIO_ITEM_URI);
        sURIMatcher.addURI(AUTHORITY, "section_path", SECTION_PATH);
        sURIMatcher.addURI(AUTHORITY, "section_music/*", SECTION_MUSIC);
        sURIMatcher.addURI(AUTHORITY, "tops_cache/*", TOPS_CACHE);
        sURIMatcher.addURI(AUTHORITY, "history_date/*", HISTORY_DATE);
        sURIMatcher.addURI(AUTHORITY, "history_music/*/*", HISTORY_MUSIC);
    }

    public static int matchUri(final Uri uri) {
        return sURIMatcher.match(uri);
    }
}
