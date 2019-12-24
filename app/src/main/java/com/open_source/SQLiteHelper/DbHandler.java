package com.open_source.SQLiteHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.open_source.activity.ChatActivity;
import com.open_source.modal.ChatMsgModel;
import com.open_source.modal.RetroObject;

import java.util.ArrayList;

public class DbHandler extends SQLiteOpenHelper {

    public static final String COLUMN_ID = "_id";
    public static final String CHAT_ID = "chat_id";
    public static final String COLUMN_MASSAGE = "massage";
    public static final String COLUMN_FROMID = "fromid";
    public static final String COLUMN_TOID = "toid";
    public static final String COLUMN_TO_NAME = "to_name";
    public static final String COLUMN_PROPERTY_Id = "property_id";
    public static final String COLUMN_SEND = "send";
    public static final String COLUMN_MSGTYPE = "msg_type";
    public static final String CONTENT_TYPE = "content_type";
    public static final String COLUMN_TIME = "time";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "OpenSpace.db";
    private static final String TABLE_CHAT = "OpenSpaceChat";


    public DbHandler(Context context,
                     SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CHAT_TABLE = "CREATE TABLE " + TABLE_CHAT + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_MASSAGE
                + " TEXT, " + COLUMN_FROMID + " TEXT, " + COLUMN_TOID + " TEXT, " + CHAT_ID + " TEXT," +
                COLUMN_PROPERTY_Id + " TEXT," + COLUMN_TO_NAME + " TEXT," + COLUMN_TIME + " TEXT," +
                COLUMN_MSGTYPE + " TEXT,"+CONTENT_TYPE + " TEXT,"+ COLUMN_SEND + " INTEGER" + ");";
        Log.e("create", CREATE_CHAT_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);

    }

    public void addItemDb(ArrayList<RetroObject> itemDb) {
        Log.e("=======", String.valueOf(itemDb.size()));
        SQLiteDatabase db = null;
        for (int i = 0; i < itemDb.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(CHAT_ID, itemDb.get(i).getChat_id());
            values.put(COLUMN_FROMID, itemDb.get(i).getFrom_id());
            values.put(COLUMN_TOID, itemDb.get(i).getTo_id());
            values.put(COLUMN_TO_NAME, itemDb.get(i).getTo_name());
            values.put(COLUMN_MASSAGE, itemDb.get(i).getMessage());
            values.put(COLUMN_MSGTYPE, itemDb.get(i).getMessage_type());
            values.put(CONTENT_TYPE, itemDb.get(i).getContent_type());
            values.put(COLUMN_TIME, itemDb.get(i).getTime());
            values.put(COLUMN_PROPERTY_Id, itemDb.get(i).getProperty_id());
            values.put(COLUMN_SEND, 1);
            db = this.getWritableDatabase();
            db.insert(TABLE_CHAT, null, values);
            Log.e("insert", "success");
        }
        db.close();
    }

    public void addNotiItemDb(ChatMsgModel itemDb) {
        ContentValues values = new ContentValues();
        values.put(CHAT_ID, itemDb.getChat_id());
        values.put(COLUMN_FROMID, itemDb.getFromid());
        values.put(COLUMN_TOID, itemDb.getToid());
        values.put(COLUMN_TO_NAME, itemDb.getTo_name());
        values.put(COLUMN_MASSAGE, itemDb.getMassage());
        values.put(COLUMN_MSGTYPE, itemDb.getMsg_type());
        values.put(CONTENT_TYPE, itemDb.getContent_type());
        values.put(COLUMN_TIME, itemDb.getTime());
        values.put(COLUMN_PROPERTY_Id, itemDb.getProperty_id());
        values.put(COLUMN_SEND, 1);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_CHAT, null, values);
        Log.e("insert", "success");
        db.close();
    }

    public ArrayList<ChatMsgModel> GetChat(String from, String to) {
        Log.e("=======database: ",from);
        Log.e("=======:database ",to);
        ArrayList<ChatMsgModel> array_chat = new ArrayList<>();
        String query = "Select * FROM " + TABLE_CHAT + " WHERE ( " + COLUMN_FROMID + " =  \"" + from + "\"" + " or " + COLUMN_FROMID + " =  \"" + to + "\" )" + " and ( " + COLUMN_TOID + " =  \"" + to + "\"" + " or " + COLUMN_TOID + " =  \"" + from + "\" )";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.e("=======:c ",cursor.getCount()+"");
        if (cursor.getCount()>0)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ChatActivity.chat_id = cursor.getString(cursor.getColumnIndex(CHAT_ID));
                array_chat.add(new ChatMsgModel(cursor.getString(cursor.getColumnIndex(COLUMN_ID)), cursor.getString(cursor.getColumnIndex(COLUMN_MASSAGE)), cursor.getString(cursor.getColumnIndex(COLUMN_FROMID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TOID)), cursor.getString(cursor.getColumnIndex(CHAT_ID)), cursor.getString(cursor.getColumnIndex(COLUMN_SEND)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_MSGTYPE)), cursor.getString(cursor.getColumnIndex(COLUMN_TIME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TO_NAME)), cursor.getString(cursor.getColumnIndex(CONTENT_TYPE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_PROPERTY_Id)))); //add the item
                cursor.moveToNext();

            }
        }
        else {
            ChatActivity.chat_id="0";
        }
        return array_chat;
    }

    public boolean deleteChat(String from, String to) {
        boolean result = false;
        String query = "Select * FROM " + TABLE_CHAT + " WHERE ( " + COLUMN_FROMID + " =  \"" + Integer.parseInt(from) + "\"" + " or " + COLUMN_FROMID + " =  \"" + Integer.parseInt(to) + "\" )" + " and ( " + COLUMN_TOID + " =  \"" + Integer.parseInt(to) + "\"" + " or " + COLUMN_TOID + " =  \"" + Integer.parseInt(from) + "\" )";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(COLUMN_ID));
            //Log.e("========",id );
            db.delete(TABLE_CHAT, COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)});
            cursor.moveToNext();
            result = true;
        }
        db.close();
        return result;
    }

}
