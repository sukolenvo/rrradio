package com.dakare.radiorecord.app.database.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.dakare.radiorecord.app.database.DBHelper;
import com.dakare.radiorecord.app.database.table.*;
import com.dakare.radiorecord.app.load.loader.database.BasicCategoryDbTable;

import java.util.List;

public class StorageProvider extends ContentProvider {

    private SQLiteDatabase dataBase;
    private DownloadAudioTable downloadAudioTable;
    private SectionPathCacheTable sectionPathCacheTable;
    private SectionMusicCacheTable sectionMusicCacheTable;
    private TopsCacheTable topsCacheTable;
    private HistoryDateCacheTable historyDateCacheTable;
    private HistoryMusicCacheTable historyMusicCacheTable;

    @Override
    public boolean onCreate() {
        dataBase = new DBHelper(getContext()).getWritableDatabase();
        downloadAudioTable = new DownloadAudioTable(dataBase);
        sectionPathCacheTable = new SectionPathCacheTable(dataBase);
        sectionMusicCacheTable = new SectionMusicCacheTable(dataBase);
        topsCacheTable = new TopsCacheTable(dataBase);
        historyDateCacheTable = new HistoryDateCacheTable(dataBase);
        historyMusicCacheTable = new HistoryMusicCacheTable(dataBase);
        return true;
    }

    @Override
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
        switch (StorageUriMatcher.matchUri(uri)) {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return downloadAudioTable.delete(selection, selectionArgs);
            case StorageUriMatcher.SECTION_PATH:
                return sectionPathCacheTable.delete(selection, selectionArgs);
            case StorageUriMatcher.SECTION_MUSIC:
                String categoryName = uri.getLastPathSegment();
                return sectionMusicCacheTable.delete("category = ?", new String[]{categoryName});
            case StorageUriMatcher.TOPS_CACHE:
                String station = uri.getLastPathSegment();
                return topsCacheTable.delete("station = ?", new String[]{station});
            case StorageUriMatcher.HISTORY_DATE:
                station = uri.getLastPathSegment();
                return historyDateCacheTable.delete("station = ?", new String[]{station});
            case StorageUriMatcher.HISTORY_MUSIC:
                List<String> pathSegments = uri.getPathSegments();
                station = pathSegments.get(pathSegments.size() - 2);
                String date = pathSegments.get(pathSegments.size() - 1);
                return historyMusicCacheTable.delete("station = ? AND date = ?", new String[]{station, date});
            default:
                throw new IllegalArgumentException("Cannot insert values with uri " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return "android.cursor.item/com.dakare.radiorecord.record";
    }

    @Override
    public Uri insert(final Uri uri, final ContentValues values) {
        switch (StorageUriMatcher.matchUri(uri)) {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return Uri.withAppendedPath(uri, downloadAudioTable.save(values) + "");
            default:
                throw new IllegalArgumentException("Cannot insert values with uri " + uri);
        }
    }

    @Override
    public int bulkInsert(final Uri uri, final ContentValues[] values) {
        switch (StorageUriMatcher.matchUri(uri)) {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return downloadAudioTable.bulkSave(values);
            case StorageUriMatcher.SECTION_PATH:
                return sectionPathCacheTable.bulkSave(values);
            case StorageUriMatcher.SECTION_MUSIC:
                String categoryName = uri.getLastPathSegment();
                return sectionMusicCacheTable.bulkSave(categoryName, values);
            case StorageUriMatcher.TOPS_CACHE:
                String station = uri.getLastPathSegment();
                return topsCacheTable.bulkSave(station, values);
            case StorageUriMatcher.HISTORY_DATE:
                station = uri.getLastPathSegment();
                return historyDateCacheTable.bulkSave(station, values);
            case StorageUriMatcher.HISTORY_MUSIC:
                List<String> pathSegments = uri.getPathSegments();
                station = pathSegments.get(pathSegments.size() - 2);
                String date = pathSegments.get(pathSegments.size() - 1);
                return historyMusicCacheTable.bulkSave(station, date, values);
            default:
                throw new IllegalArgumentException("Cannot perform bulk insert for uri " + uri);
        }
    }

    @Override
    public Cursor query(final Uri uri, final String[] projection, final String selection,
                        final String[] selectionArgs, final String sortOrder) {
        switch (StorageUriMatcher.matchUri(uri)) {
            case StorageUriMatcher.DOWNLOAD_AUDIO_URI:
                return downloadAudioTable.find(projection, selection, selectionArgs, sortOrder);
            case StorageUriMatcher.SECTION_PATH:
                return sectionPathCacheTable.find(projection, selection, selectionArgs, sortOrder);
            case StorageUriMatcher.SECTION_MUSIC:
                String categoryName = uri.getLastPathSegment();
                return sectionMusicCacheTable.find(projection, "category = ?", new String[]{categoryName}, sortOrder);
            case StorageUriMatcher.TOPS_CACHE:
                String station = uri.getLastPathSegment();
                return topsCacheTable.find(projection, "station = ?", new String[]{station}, sortOrder);
            case StorageUriMatcher.HISTORY_DATE:
                station = uri.getLastPathSegment();
                return historyDateCacheTable.find(projection, "station = ?", new String[]{station}, sortOrder);
            case StorageUriMatcher.HISTORY_MUSIC:
                List<String> pathSegments = uri.getPathSegments();
                station = pathSegments.get(pathSegments.size() - 2);
                String date = pathSegments.get(pathSegments.size() - 1);
                return historyMusicCacheTable.find(new String[]{HistoryMusicCacheTable.COLUMN_ARTIST,
                                HistoryMusicCacheTable.COLUMN_SONG, HistoryMusicCacheTable.COLUMN_URL,
                                HistoryMusicCacheTable.COLUMN_WHEN, HistoryMusicCacheTable.COLUMN_VISIBLE,
                                BasicCategoryDbTable.COLUMN_FROM_DATE},
                        "station = ? AND date = ?", new String[]{station, date}, sortOrder);
            default:
                throw new IllegalArgumentException("Cannot perform query for uri " + uri);
        }
    }

    @Override
    public int update(final Uri uri, final ContentValues values, final String selection,
                      final String[] selectionArgs) {
        switch (StorageUriMatcher.matchUri(uri)) {
            case StorageUriMatcher.DOWNLOAD_AUDIO_ITEM_URI:
                downloadAudioTable.updateById(Integer.parseInt(uri.getLastPathSegment()), values);
                return 1;
            default:
                throw new IllegalArgumentException("Cannot perform update for uri " + uri);
        }
    }

    @Override
    public void shutdown() {
        super.shutdown();
        dataBase.close();
    }
}
