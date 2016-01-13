package com.mio.jrdv.wearkout.model;

/**
 * Created by joseramondelgado on 10/01/16.
 */
public class Ejercicio {

    //private vars
    int _id;
    String _name;
    String _Gif_name;

    String _level;
    String _name_english;

    //constructor 1 con id pra SQL
    public Ejercicio( int id,  String name,String Gif_name, String name_english,String level) {
        this._Gif_name = Gif_name;
        this._id = id;
        this._level = level;
        this._name = name;
        this._name_english = name_english;

    }
    //constructor 2 sin id para CREARLOS
    public Ejercicio( String name,String Gif_name, String name_english,String level) {
        this._Gif_name = Gif_name;
        this._level = level;
        this._name = name;
        this._name_english = name_english;

    }


    // Empty constructor
    public Ejercicio(){

    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_Gif_name() {
        return _Gif_name;
    }

    public void set_Gif_name(String _Gif_name) {
        this._Gif_name = _Gif_name;
    }

    public String get_level() {
        return _level;
    }

    public void set_level(String _level) {
        this._level = _level;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_name_english() {
        return _name_english;
    }

    public void set_name_english(String _name_english) {
        this._name_english = _name_english;
    }



}

