package com.dakare.radiorecord.app.download.service;

import com.dakare.radiorecord.app.database.DownloadAudioTable;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum DownloadsSort
{
    NAME_ASC(DownloadAudioTable.COLUMN_TITLE, "ASC"),
    NAME_DESC(DownloadAudioTable.COLUMN_TITLE, "DESC"),
    CREATED_ASC(DownloadAudioTable.COLUMN_SAVED_DATE, "ASC"),
    CREATED_DESC(DownloadAudioTable.COLUMN_SAVED_DATE, "DESC");

    private final String column;
    private final String order;

    public String getSort()
    {
        return column + " " + order;
    }
}
