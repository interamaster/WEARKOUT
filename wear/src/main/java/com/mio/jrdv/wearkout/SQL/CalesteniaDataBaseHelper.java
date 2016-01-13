package com.mio.jrdv.wearkout.SQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mio.jrdv.wearkout.model.Ejercicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joseramondelgado on 10/01/16.
 */
public class CalesteniaDataBaseHelper extends SQLiteOpenHelper{


    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Ejercicios.db";
    private static final String TABLE_NAME = "ejercicios_table";
    private static final String KEY_ID = "_id";
    private static final String KEY_NAME = "name";
    private static final String KEY_GIF_NAME = "Gif_name";
    private static final String KEY_NAME_ENGLISH = "name_english";
    private static final String KEY_LEVEL = "level";


    String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NAME+" TEXT,"+KEY_GIF_NAME+" TEXT,"+KEY_NAME_ENGLISH+" TEXT,"+KEY_LEVEL+" TEXT)";
    String DROP_TABLE = "DROP TABLE IF EXISTS "+TABLE_NAME;


    public CalesteniaDataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL(DROP_TABLE);
        // Create tables again
        onCreate(db);
    }


    //metodos de ayuda solo necesito 2

    //para insertar en la SQL:

    public void addEjercicio (Ejercicio ejercicio) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, ejercicio.get_name());
            values.put(KEY_GIF_NAME, ejercicio.get_Gif_name());
            values.put(KEY_NAME_ENGLISH, ejercicio.get_name_english());
            values.put(KEY_LEVEL, ejercicio.get_level());


            db.insert(TABLE_NAME, null, values);
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }





    //PARA recuperar de la SQL:

    // Getting single Ejercicio
    public Ejercicio getEjerciocio(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[] { KEY_ID,
                        KEY_NAME, KEY_GIF_NAME,KEY_NAME_ENGLISH,KEY_LEVEL }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Ejercicio ejercicio = new Ejercicio(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),cursor.getString(4));
        // return contact
        return ejercicio;
    }


    //PARA RECUPERARLOS TOODS Y VER QUE ESTA OK

    // Getting All Contacts
    public List<Ejercicio> getAllEjercicios() {
        List<Ejercicio> ejercicioList = new ArrayList<Ejercicio>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Ejercicio ejercicio = new Ejercicio();
                ejercicio.set_id(Integer.parseInt(cursor.getString(0)));
                ejercicio.set_name(cursor.getString(1));
                ejercicio.set_Gif_name(cursor.getString(2));
                ejercicio.set_name_english(cursor.getString(3));
                ejercicio.set_level(cursor.getString(4));

                // Adding contact to list
                ejercicioList.add(ejercicio);
            } while (cursor.moveToNext());
        }

        // return contact list
        return ejercicioList;
    }

    //PARA RECUPERAR TODOS  LOS DE UN NIVEL!!!

    public List<Ejercicio> getAllEjerciciosLEVEL(String LEVEL) {

        List<Ejercicio> ejercicioLEVELList = new ArrayList<Ejercicio>();
        // Select All Query
        String selectQuery = "SELECT  * FROM "+ TABLE_NAME+" WHERE "+ KEY_LEVEL +" = '"+LEVEL+"'";//OJO CON LAS "'"!!!

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Ejercicio ejercicio = new Ejercicio();
                ejercicio.set_id(Integer.parseInt(cursor.getString(0)));
                ejercicio.set_name(cursor.getString(1));
                ejercicio.set_Gif_name(cursor.getString(2));
                ejercicio.set_name_english(cursor.getString(3));
                ejercicio.set_level(cursor.getString(4));

                // Adding contact to list
                ejercicioLEVELList.add(ejercicio);
            } while (cursor.moveToNext());
        }

        // return contact list
        return ejercicioLEVELList;
    }




    //parasaber el total, esto lo uso para no volver a crearlos cada vez que arranque


    // Getting contacts Count
    public int getEjerciciosCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count=cursor.getCount();
        cursor.close();

        // return count
       // return cursor.getCount();
        return count;
    }


}
