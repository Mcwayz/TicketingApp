package com.example.ticketingpos.ticket.model;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabase extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "History.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "my_history";
    private static final String COLUMN_ID = "id";
    private static final String TICKET_ID = "ticket_id";
    private static final String BARCODE = "barcode";
    private static final String TICKET_TYPE = "ticket_type";
    private static final String TICKET_NUMBER = "ticket_number";
    private static final String CHILDREN = "children_no";
    private static final String ADULT = "adult_no";
    private static final String PRICE = "price";
    private static final String METHOD = "payment";
    private static final String DATE_TIME = "date_time";
    private static final String STATUS = "status";
    private static final String PRINT_NO = "print_no";
    public MyDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                        " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        TICKET_ID + " TEXT, " +
                        BARCODE + " TEXT, " +
                        TICKET_TYPE + " TEXT, " +
                        TICKET_NUMBER + " TEXT, " +
                        CHILDREN + " INTEGER, " +
                        ADULT + " INTEGER, " +
                        PRICE + " TEXT, " +
                        METHOD + " TEXT, " +
                        DATE_TIME + " TEXT, " +
                        PRINT_NO + " INTEGER, " +
                        STATUS +" TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
         onCreate(db);
    }

    public void saveTransaction(String ticket_id, String barcode, String ticket_type, String ticket_number, int children_no,
                                int adult_no, String price, String method, String date, int print, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TICKET_ID, ticket_id);
        cv.put(BARCODE, barcode);
        cv.put(TICKET_TYPE, ticket_type);
        cv.put(TICKET_NUMBER, ticket_number);
        cv.put(CHILDREN, children_no);
        cv.put(ADULT, adult_no);
        cv.put(PRICE, price);
        cv.put(METHOD, method);
        cv.put(DATE_TIME, date);
        cv.put(PRINT_NO, print);
        cv.put(STATUS, status);
        long result = db.insert(TABLE_NAME, null, cv);
        if(result == -1) {
            // Toast.makeText(context, "Transaction Record Failed", Toast.LENGTH_SHORT).show();
        }else{
            // Toast.makeText(context, "Transaction Record Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM "+ TABLE_NAME+ " ORDER BY "+COLUMN_ID+" DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if(db !=null){
           cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor searchData(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + DATE_TIME + " LIKE '%" + query + "%'"+
                " OR "+ TICKET_ID + " LIKE '%" + query + "%'"+
                " OR "+ TICKET_NUMBER + " LIKE '%" + query + "%'"+
                " OR "+ TICKET_TYPE + " LIKE '%" + query + "%'"+
                " OR "+ METHOD + " LIKE '%" + query + "%'"+
                " ORDER BY " + COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(search, null);
        return cursor;
    }

    public String updateTicket(String ticket_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PRINT_NO, "print_no + 1");
        values.put(STATUS, "Reprint");
        String whereClause = "ticket_id = ?";
        String[] whereArgs = new String[] {ticket_id};
        int numRowsUpdated = db.update(TABLE_NAME, values, whereClause, whereArgs);
        db.close();
        if (numRowsUpdated > 0) {
            return "Ticket updated successfully.";
        } else {
            return "Ticket not found.";
        }
    }


}
