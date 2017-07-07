package com.example.hp.movie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DatabaseAdapter {
 private DatabaseHelper helper;

    public DatabaseAdapter(Context context) {
        helper = new DatabaseHelper(context);

    }

    public long Insert(String movImage, String movTitle, String movRel, String movOverview, String movId , String movRate) {
        SQLiteDatabase db = helper.getWritableDatabase(); //return sqlite obj
        Log.v("database refrence", String.valueOf(db));
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.IMAGE, movImage);
        contentValues.put(DatabaseHelper.TITLE, movTitle);
        contentValues.put(DatabaseHelper.RELEASE_DATE, movRel);
        contentValues.put(DatabaseHelper.OVERVIEW, movOverview);
        contentValues.put(DatabaseHelper.ID, movId);
        contentValues.put(DatabaseHelper.RATE,movRate);
        long id = db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);//neg if sth is wrong or id indecate row id
        Log.v("Value inserted in table ",movTitle);
        db.close();
        return id;
    }
//public ArrayList<movie> selectAllMovies() {
//    ArrayList<movie> favoriteMovies = new ArrayList<>();
   public ArrayList<movie> selectAllMovies() {
       ArrayList<movie> selectedMovies = new ArrayList<>();

        String[] Columns = {
                DatabaseHelper.ID,
                DatabaseHelper.IMAGE,
                DatabaseHelper.TITLE,
                DatabaseHelper.OVERVIEW,
                DatabaseHelper.RELEASE_DATE,
                DatabaseHelper.RATE
                };

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, Columns, null, null, null, null, null);
       StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            movie favMov = new movie();
            //          int index1 = cursor.getColumnIndex(DatabaseHelper.UID);
//          int cid = cursor.getInt(index1);
//            //or
            int cid = cursor.getInt(0);
            String Image = cursor.getString(1);
            favMov.setMovieImage(Image);
            String Title = cursor.getString(2);
            favMov.setMovieTitle(Title);
            String Overview = cursor.getString(3);
            favMov.setMovieOverView(Overview);
            String ReleaseDate = cursor.getString(4);
            favMov.setReleaseDate(ReleaseDate);
            String rate = cursor.getString(5);
            favMov.setMovieTopRated(rate);

            selectedMovies.add(favMov);
        //    buffer.append(cid + "\n" + Image + "\n" + Title + "\n"+ Overview+"\n"+ReleaseDate+"\n"+rate +"\n");
        //    Log.v("buffer " , buffer.toString());
        }
      cursor.close();
       db.close();
        return selectedMovies;


}


    public String getData(String ID){
        //select row where movie id = ....;
            String[] Columns = {
                   // DatabaseHelper.ID,
                    DatabaseHelper.IMAGE,
                    DatabaseHelper.TITLE,
                    DatabaseHelper.OVERVIEW,
                    DatabaseHelper.RELEASE_DATE,
                    DatabaseHelper.RATE

            };
            SQLiteDatabase db = helper.getWritableDatabase();
            Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, Columns, DatabaseHelper.ID+" = '"+ID+"'", null, null, null, null);
            StringBuffer buffer = new StringBuffer();
        if( cursor.getCount()> 0)
        {
            while (cursor.moveToNext()) {

                    int index1 = cursor.getColumnIndex(DatabaseHelper.IMAGE);
                    int index2 = cursor.getColumnIndex(DatabaseHelper.TITLE);
                    int index3 = cursor.getColumnIndex(DatabaseHelper.OVERVIEW);
                    int index4 = cursor.getColumnIndex(DatabaseHelper.RELEASE_DATE);
                    int index5 = cursor.getColumnIndex(DatabaseHelper.RATE);

                    String Image = cursor.getString(index1);
                    String Title = cursor.getString(index2);
                    String Overview = cursor.getString(index3);
                    String Date = cursor.getString(index4);
                    String rate = cursor.getString(index5);


                    buffer.append(Image + "\n" + Title + "\n" + Overview + "\n" + Date + "\n" + rate);
                    Log.v("getData method ", String.valueOf(buffer));
                }
            }

        return buffer.toString();

    }

    public int deleteMovie(String Id){
        SQLiteDatabase db = helper.getWritableDatabase();
//        String[] whereArgs ={DatabaseHelper.ID};
       int count =  db.delete(DatabaseHelper.TABLE_NAME,DatabaseHelper.ID+" = '"+Id+"'", null);
        db.close();
        return count;

    }


    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "movieDatabase";
        private static final String TABLE_NAME = "movieTable";
        private static final int DATABASE_VERSION = 2;
        private static final String UID = "_id";
        private static final String IMAGE = "Image";
        private static final String TITLE = "Title";
        private static final String RELEASE_DATE = "Release";
        private static final String OVERVIEW = "Overview";
        private static final String ID = "id";
        private static final String RATE = "Rate";

        private static final String CREATE_TABLE
                = "CREATE TABLE " + TABLE_NAME + "(" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + IMAGE + " VARCHAR(225), " + TITLE + " VARCHAR(225) , " + RELEASE_DATE + " VARCHAR(225) , "
                + OVERVIEW + " VARCHAR(225) , " + ID + " VARCHAR(225) , " + RATE + " VARCHAR(225));";

        private static final String DROP_TABLE
                = "DROP TABLE " + TABLE_NAME + " IF EXISTS";
        public Context context;

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // CREATE TABLE movieTable(_id INTEGER PRIMARY KEY AUTOINCREMENT , Image VARCHAR(225))
            try {
                Log.i("database", CREATE_TABLE);
                db.execSQL(CREATE_TABLE);
                Log.v("on create is called", "table created");
            } catch (SQLException e) {
                Log.v("ERROR IN CREATING TABLE", String.valueOf(e));
                e.printStackTrace();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}

