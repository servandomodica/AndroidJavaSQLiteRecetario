package com.example.recetas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {


	public AdminSQLiteOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}


	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL("create table recetas (titulo text primary key,descripcion text)");
	}


	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

	}

}
