package com.example.salim.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by salim on 15/07/16.
 */
public class NotebookDbAdapter {


    public static final String NOTE_TABLE = "note";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_MESSAGE = "message";
    public static final String Column_CATEGORY = "category";
    public static final String COLUMN_DATE = "date";
    public static final String CREATE_TABLE_NOTE = "create table " + NOTE_TABLE + " ( "
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_MESSAGE + " text not null, "
            + Column_CATEGORY + " text not null, "
            + COLUMN_DATE + ");";
    private static final String DATABASE_NAME = "notebook.db";
    private static final int DATABASE_VERSION = 1;
    private String[] allColumns = {COLUMN_ID, COLUMN_TITLE, COLUMN_MESSAGE, Column_CATEGORY, COLUMN_DATE};
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private NotebookDbHelpher notebookDbHelpher;

    public NotebookDbAdapter(Context ctx) {
        context = ctx;
    }

    public String[] getAllColumns() {
        return allColumns;
    }

    public void setAllColumns(String[] allColumns) {
        this.allColumns = allColumns;
    }

    public NotebookDbAdapter open() throws android.database.SQLException {
        notebookDbHelpher = new NotebookDbHelpher(context);
        sqLiteDatabase = notebookDbHelpher.getWritableDatabase();
        return this;
    }

    public void close() {
        notebookDbHelpher.close();
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> notes = new ArrayList<Note>();

        Cursor cursor = sqLiteDatabase.query(NOTE_TABLE, allColumns, null, null, null, null, null);
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Note note = cursorToNote(cursor);
            notes.add(note);
        }
        cursor.close();
        return notes;
    }

    private Note cursorToNote(Cursor cursor) {
        return new Note(cursor.getString(1), cursor.getString(2), cursor.getLong(0), cursor.getLong(4), Note.Category.valueOf(cursor.getString(3)));
    }


    public Note createNote(String title, String message, Note.Category category) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_MESSAGE, message);
        values.put(Column_CATEGORY, category.name());
        values.put(COLUMN_DATE, Calendar.getInstance().getTimeInMillis());

        long insertId = sqLiteDatabase.insert(NOTE_TABLE, null, values);
        Cursor cursor = sqLiteDatabase.query(NOTE_TABLE, allColumns, COLUMN_ID + "=" + insertId, null, null, null, null, null);

        cursor.moveToFirst();
        return cursorToNote(cursor);
    }

    public long updateNote (long idTodate , String newTitle , String newMessage , Note.Category newCategory){

        ContentValues values = new ContentValues();

        values.put(COLUMN_TITLE, newTitle);
        values.put(COLUMN_MESSAGE, newMessage);
        values.put(Column_CATEGORY, newCategory.name());
        values.put(COLUMN_DATE, Calendar.getInstance().getTimeInMillis());

        return sqLiteDatabase.update(NOTE_TABLE,values,COLUMN_ID +  "=" + idTodate,null);

    }

    public long deleteNote (long idTodate){
        return sqLiteDatabase.delete(NOTE_TABLE,COLUMN_ID + "=" + idTodate , null);
    }

    private static class NotebookDbHelpher extends SQLiteOpenHelper {


        NotebookDbHelpher(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE_NOTE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Log.w(NotebookDbHelpher.class.getName(),
                    "Upgrading database from version" + oldVersion + "to" + newVersion + ",which will destroy all old data");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + NOTE_TABLE);
            onCreate(sqLiteDatabase);


        }


    }


}
