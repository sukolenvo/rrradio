package com.dakare.radiorecord.app.database;

import android.database.sqlite.SQLiteDatabase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(suppressConstructorProperties = true)
public class Table
{
    @Getter(AccessLevel.PROTECTED)
	private final SQLiteDatabase database; 
}
