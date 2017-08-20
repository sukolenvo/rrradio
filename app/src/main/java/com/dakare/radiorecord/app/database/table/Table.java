package com.dakare.radiorecord.app.database.table;

import android.database.sqlite.SQLiteDatabase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Table {
    @Getter(AccessLevel.PROTECTED)
    private final SQLiteDatabase database;
}
