package com.project.sveglia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Pat on 05/06/18.
 */

public class DB_Manager {

    private Context context;
    private DB_Helper db_helper;
    private SQLiteDatabase database;
    private SQLiteDatabase databaseRead;

    public DB_Manager(Context c){
        context = c;
    }

    public DB_Manager open () throws SQLException {
        db_helper = new DB_Helper(context);
        database = db_helper.getWritableDatabase();
        databaseRead = db_helper.getReadableDatabase();

//inserisco la card falsa

        boolean[]b = new boolean[7];
        if(this.getAllID().size()==0){
            this.insert_view(999999999, Long.valueOf(32423), "", b, "1", 1, 0, 1, 0, null, null, null, null, null, "1");
            this.inizializza_setting();
        }

        return this;
    }

    public void close(){
        db_helper.close();
    }



//inserire sveglia con tutti i parametri
    public void insert_view(int id,
                            Long time,
                            String nome,
                            boolean [] boolean_day,
                            String on_off,
                            int ritarda,
                            int id_suoneria,
                            int posizione_suoneria,
                            int travel_to,
                            String from,
                            String to,
                            String mezzo,
                            String add_from_bed_to_car,
                            String maps_direction_request,
                            String visible_alarm
                            //String array_id //inserito da un altra funzione!!
    ){
        ContentValues cv = new ContentValues();
        cv.put(DB_Helper.ID_VIEW,id);
        cv.put(DB_Helper.TIME_VIEW,time);
        cv.put(DB_Helper.NOME, nome);
        cv.put(DB_Helper.BOOLEAN_DAY, bool_to_string(boolean_day));
        cv.put(DB_Helper.ON_OFF,on_off);
        cv.put(DB_Helper.RITARDA,ritarda);
        cv.put(DB_Helper.ID_SUONERIA,id_suoneria);
        cv.put(DB_Helper.POSIZIONE_SUONERIA,posizione_suoneria);
        cv.put(DB_Helper.TRAVEL_TO,travel_to);
        cv.put(DB_Helper.GOOGLE_FROM, from);
        cv.put(DB_Helper.GOOGLE_TO, to);
        cv.put(DB_Helper.MEZZO,mezzo);
        cv.put(DB_Helper.ADD_FROM_BED_TO_CAR, add_from_bed_to_car);
        cv.put(DB_Helper.MAPS_DIRECTION_REQUEST, maps_direction_request);
        cv.put(DB_Helper.VISIBLE_ALARM, visible_alarm);
        //cv.put(DB_Helper.ARRAY_ID_SVEGLIE,array_id);
        try{
            database.insert(DB_Helper.TABLE_VIEW,null,cv);

        }catch(SQLException e){
            System.out.println("___________errore inserimento_______"+e.toString());
        }
        //database.insert(DB_Helper.TABLE_VIEW,null,cv);
    }


//inserire vector delle ripetizioni
    public void insert_repetition_id(int id, Vector<Integer> vector){
        String str="";
        str = vector.toString();
        //System.out.println(id);
        String sql = "UPDATE TABLE_VIEW "  +
                " SET array_id_sveglie = '"+ str +"' " +
                " WHERE _id = "+ id +";";
        database.execSQL(sql);
    }
//recupero vector delle ripetizioni
    public Vector<Integer> getVectorID_Ripetizioni(int id_view){
        Vector<Integer> vector = new Vector<>(1);

        ArrayList<String> array_vector = new ArrayList<>();


        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM TABLE_VIEW WHERE _id = "+ id_view, null);

        res.moveToFirst();

        while (!res.isAfterLast()){
            array_vector.add(res.getString(res.getColumnIndex(db_helper.ARRAY_ID_SVEGLIE)));
            res.moveToNext();
        }

        String str = array_vector.get(0);


        String str_no_parentesi = str.substring(1,str.length()-1);
        String [] array = str_no_parentesi.split(", ");
        for (int i=0;i<array.length ;i++ ) {
            vector.add(Integer.parseInt(array[i]));

        }
        return vector;
    }

//inserire sveglia nella table_sveglie
    public void insert_sveglia(int id, Long time){
        ContentValues cv = new ContentValues();
        cv.put(DB_Helper.ID_SVEGLIA, id);
        cv.put(DB_Helper.TIME_SVEGLIA, time);
        database.insert(DB_Helper.TABLE_SVEGLIE, null, cv);
    }

    //INIZIALIZZARE TABELLA SETTING
    public void inizializza_setting(){
        ContentValues uno = new ContentValues();
        ContentValues due = new ContentValues();
        ContentValues tre = new ContentValues();
        ContentValues quattro = new ContentValues();
        ContentValues cinque = new ContentValues();
        ContentValues sei = new ContentValues();

        uno.put(DB_Helper.COMANDO, "BAD_TO_CAR");
        uno.put(DB_Helper.VALORE, "900000");
        due.put(DB_Helper.COMANDO,"DURATA_SUONERIA");
        due.put(DB_Helper.VALORE,"60000");
        tre.put(DB_Helper.COMANDO, "RITARDA_MINUTI");
        tre.put(DB_Helper.VALORE,"300000");
        quattro.put(DB_Helper.COMANDO,"RITARDA_VOLTE");
        quattro.put(DB_Helper.VALORE, "3");
        cinque.put(DB_Helper.COMANDO, "SENSORI_ON");
        cinque.put(DB_Helper.VALORE,"0");
        sei.put(DB_Helper.COMANDO,"SENSORI_OPZIONE");
        sei.put(DB_Helper.VALORE,"ritarda");

        database.insert(DB_Helper.TABLE_SETTING,null,uno);
        database.insert(DB_Helper.TABLE_SETTING,null,due);
        database.insert(DB_Helper.TABLE_SETTING,null,tre);
        database.insert(DB_Helper.TABLE_SETTING,null,quattro);
        database.insert(DB_Helper.TABLE_SETTING,null,cinque);
        database.insert(DB_Helper.TABLE_SETTING,null,sei);



    }
//FUNZIONI PER CANCELLARE__________________________________

    public void delete_view(int id){
        database.delete(DB_Helper.TABLE_VIEW, DB_Helper.ID_VIEW + " = " + id, null);
    }

    public void delete_sveglia(int id){
        database.delete(DB_Helper.TABLE_SVEGLIE, DB_Helper.ID_SVEGLIA + " = " + id, null);
    }


//funzioni per recuperare i dati dal DB___________________________
    public ArrayList<String>getAllTimeView(){
        ArrayList<String> array_time = new ArrayList<>();


        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM TABLE_VIEW", null);

        res.moveToFirst();

        while (!res.isAfterLast()){
            array_time.add(res.getString(res.getColumnIndex(db_helper.TIME_VIEW)));
            res.moveToNext();
        }

        return array_time;

    }

    public ArrayList<String>getAllNameView(){
        ArrayList<String> array_name = new ArrayList<>();

        Cursor res = databaseRead.rawQuery("SELECT * FROM TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array_name.add(res.getString(res.getColumnIndex(db_helper.NOME)));
            res.moveToNext();
        }

        return array_name;

    }

    public ArrayList<String>getAllOn_Off(){
        ArrayList<String> array_on = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array_on.add(res.getString(res.getColumnIndex(db_helper.ON_OFF)));
            res.moveToNext();
        }

        return array_on;

    }

    public ArrayList<String>getAllID(){
        ArrayList<String> array_id = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array_id.add(res.getString(res.getColumnIndex(db_helper.ID_VIEW)));
            res.moveToNext();
        }

        return array_id;

    }

    public ArrayList<String>getAllRepetitionsDay(){
        ArrayList<String> array_repetitions = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array_repetitions.add(res.getString(res.getColumnIndex(db_helper.BOOLEAN_DAY)));
            res.moveToNext();
        }

        return array_repetitions;

    }

    public ArrayList<String>getAllRitarda(){
        ArrayList<String> array_ritarda = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array_ritarda.add(res.getString(res.getColumnIndex(db_helper.RITARDA)));
            res.moveToNext();
        }

        return array_ritarda;

    }

    public ArrayList<String>getAllIDsuoneria(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.ID_SUONERIA)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllPosSuoneria(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.POSIZIONE_SUONERIA)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllTravelTO(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.TRAVEL_TO)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllFrom(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.GOOGLE_FROM)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllTo(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.GOOGLE_TO)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllMezzo(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);
        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.MEZZO)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllAddFromBedToCar(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);

        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.ADD_FROM_BED_TO_CAR)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllMapsDirectionRequest(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);

        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.MAPS_DIRECTION_REQUEST)));
            res.moveToNext();
        }

        return array;

    }

    public ArrayList<String>getAllVisibleAlarm(){
        ArrayList<String> array = new ArrayList<>();

        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor res = db.rawQuery("select * from TABLE_VIEW", null);

        res.moveToFirst();

        while (!res.isAfterLast()){
            array.add(res.getString(res.getColumnIndex(db_helper.VISIBLE_ALARM)));
            res.moveToNext();
        }

        return array;

    }


//________________________________________________________________
    //FUNZIONI DI APPOGGIO

    //Setto Visible_Alarm quando devo aprire google maps
    public void setVisibleAlarm(int id){
        String sql="UPDATE TABLE_VIEW "+
                " SET visible_alarm = '0' " +
                " WHERE _id = " + id+" ;";
        database.execSQL(sql);
    }

    //SETTA ON_OF QUANDO SI PREME SWITCH
    public void SetOn_Off(int id, boolean acceso){
        if(!acceso){
            String sql="UPDATE TABLE_VIEW "+
                    " SET on_off = '0' " +
                    " WHERE _id = " + id+" ;";
            database.execSQL(sql);
        }
        if (acceso){

            String sql="UPDATE TABLE_VIEW "+
                    " SET on_off = '1' " +
                    " WHERE _id = " + id+" ;";
            database.execSQL(sql);

        }

    }

    //funzione di appoggio per convertire vettore di boolean in string per inserirle nel DB
    public String bool_to_string(boolean[] array){
        String str = "";
        for(int i=0;i<7;i++){
            if(array[i]){
                str=str+"1";
            }else{
                str=str+"0";
            }
        }
        //System.out.println(str);
        return str;
    }

    //NUMERO RI RIGHE NELLA TABELLA VIEW
    public int numberOfRows(){
        SQLiteDatabase db = db_helper.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db,db_helper.TABLE_VIEW);
        return numRows;
    }

    //FUNZIONI PER SETTARE LE IMPOSTAZIONI________________________

    public void setBadToCar(Long millis){
        String sql = "UPDATE TABLE_SETTING "+
                "SET valore = '" + millis + "' "+
                "WHERE comando = 'BAD_TO_CAR'";
        database.execSQL(sql);
    }
    public void setDurataSuoneria(Long millis){
        String sql = "UPDATE TABLE_SETTING "+
                "SET valore = '" + millis + "' "+
                "WHERE comando = 'DURATA_SUONERIA'";
        database.execSQL(sql);
    }
    public void setRitardaMinuti(Long millis){
        String sql = "UPDATE TABLE_SETTING "+
                "SET valore = '" + millis + "' "+
                "WHERE comando = 'RITARDA_MINUTI'";
        database.execSQL(sql);
    }
    public void setRitardaVolte(int volte){
        String sql = "UPDATE TABLE_SETTING "+
                "SET valore = '" + volte + "' "+
                "WHERE comando = 'RITARDA_VOLTE'";
        database.execSQL(sql);
    }
    public void setSensori_on(boolean attivati){
        if(attivati){
            String sql = "UPDATE TABLE_SETTING "+
                    "SET valore = '1' "+
                    "WHERE comando = 'SENSORI_ON'";
            database.execSQL(sql);
        }
        if(!attivati){
            String sql = "UPDATE TABLE_SETTING "+
                    "SET valore = '0' "+
                    "WHERE comando = 'SENSORI_ON'";
            database.execSQL(sql);
        }

    }
    public void setSensoriOpzione(String opzione){
        String sql = "UPDATE TABLE_SETTING "+
                "SET valore = '" + opzione + "' "+
                "WHERE comando = 'SENSORI_OPZIONE'";
        database.execSQL(sql);
    }

    //FUNZIONI PER RICAVARE LE IMPOSTAZIONI_____________________

    public Long getBadToCar(){
        Long res=null;
        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from TABLE_SETTING", null);
        cursor.moveToFirst();
        String str = cursor.getString(1);
        res = Long.parseLong(str);
        return res;
    }
    public Long getDurataSuoneria(){
        Long res=null;
        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from TABLE_SETTING", null);
        cursor.moveToFirst();
        for (int i=0;i<1;i++)cursor.moveToNext();
        String str = cursor.getString(1);
        res = Long.parseLong(str);
        return res;
    }
    public Long getRitardaMinuti(){
        Long res=null;
        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from TABLE_SETTING", null);
        cursor.moveToFirst();
        for (int i=0;i<2;i++)cursor.moveToNext();
        String str = cursor.getString(1);        res = Long.parseLong(str);
        return res;
    }
    public int getRitardaVolte(){
        int res;
        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from TABLE_SETTING", null);
        cursor.moveToFirst();
        for (int i=0;i<3;i++)cursor.moveToNext();
        String str = cursor.getString(1);        res =Integer.parseInt(str);
        return res;
    }
    public boolean getSensoriOn(){
        boolean res=false;
        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from TABLE_SETTING", null);
        cursor.moveToFirst();
        for (int i=0;i<4;i++)cursor.moveToNext();
        String str = cursor.getString(1);        Character character;
        character=str.charAt(0);
        if (character.equals((Character)'0')){
            res = false;
        }
        if (character.equals((Character)'1')){
            res = true;
        }
        return res;
    }
    public String getSensoriOpzione(){
        SQLiteDatabase db= db_helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from TABLE_SETTING", null);
        cursor.moveToFirst();
        for (int i=0;i<5;i++)cursor.moveToNext();
        String str = cursor.getString(1);
        return str;
    }

}


