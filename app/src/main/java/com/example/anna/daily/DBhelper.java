package com.example.anna.daily;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.anna.daily.model.Deal;
import com.example.anna.daily.model.Task;

import java.util.ArrayList;
import java.util.List;

import static com.example.anna.daily.MainActivity.base64Image;

/**
 * Created by InnoSoft
 */

public class DBhelper extends SQLiteOpenHelper {

    public static final int DBVERSION = 2;
    public static final String DBNAME = "dailyDB";

    //Set up Deal Table
    public static final String DEAL_TABLE = "dealTB";
    public static final String DEAL_ID = "id";
    public static final String DEAL_NAME= "name";
    public static final String IMAGE_PATH= "imagePath";

    //Set up Task Table
    public static final String TASK_TABLE = "taskTB";
    public static final String TASK_NAME = "name";
    public static final String TASK_DEAL_ID = "deal_id";
    public static final String TASK_NUMBER = "roll_number";

    //CREATE TB
    public static String queryCreateDealTable = "CREATE TABLE " + DEAL_TABLE + "(id integer primary key autoincrement, "+ DEAL_NAME +" text, "+ IMAGE_PATH +" text);";
    public static String queryCreateTaskTable = "CREATE TABLE " + TASK_TABLE + "("+ TASK_NAME +" text, "+ TASK_NUMBER +" integer, "+ TASK_DEAL_ID +" integer);";

    //DROP TB
    static final String DROP_DEAL_TB = "DROP TABLE IF EXISTS dealTB";
    static final String DROP_TASK_TB = "DROP TABLE IF EXISTS taskTB";

    public SQLiteDatabase db;


    public DBhelper(Context context) {

        super(context, DBNAME, null, DBVERSION);
    }


    /** -------------------- DEAL Operations  ----------------------**/

   //CLOSE DB
    public void closeDB()
    {
        try {
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** INSERT Deal **/
    public boolean insertDeal(Deal deal) {

        try
        {
            ContentValues cv = new ContentValues();
            cv.put(DEAL_NAME, deal.getName());
            cv.put(IMAGE_PATH, deal.getImagePath());

            db = getWritableDatabase();
            long result = db.insert(DEAL_TABLE, null, cv);

            if(result>0)
            {
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    return false;
    }

    /** UPDATE Deal **/
    public boolean updateDeal(int id,String newName, String imagePath) {

        try {

            ContentValues cv = new ContentValues();
            cv.put(DEAL_NAME,newName);
            if (!imagePath.matches("")) {
                cv.put(IMAGE_PATH, imagePath);
            }

            int result = db.update(DEAL_TABLE, cv, DEAL_ID + " =?", new String[]{String.valueOf(id)});
            if (result > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    /** DELETE Deal **/
    public boolean deleteDeal(int id)
    {

        try
        {
            int result = db.delete(DEAL_TABLE,DEAL_ID+" =?",new String[]{String.valueOf(id)});
            if(result>0)
            {
                return true;
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }


    /** GET ALL Deals **/
    public List<Deal> getAllDeals() {

        List<Deal> list = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.query(DEAL_TABLE, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Deal deal = new Deal();
            deal.setId(cursor.getInt(0));
            deal.setName(cursor.getString(1));
            deal.setImagePath(cursor.getString(2));

            Log.d("DealID column", String.valueOf(cursor.getInt(0)));
            Log.d("DealNAme column", String.valueOf(cursor.getLong(1)));
            Log.d("DealImage column", cursor.getString(2));

            list.add(deal);
        }

        cursor.close();
        return list;
    }

    /** For searching %dealNAme% in DB and give DealLIst back **/
    public List<Deal> searchByDealName(String dealName){

        List<Deal> dealList = new ArrayList<>();
        db = getReadableDatabase();
        //String selection = "name LIKE ?";
        //String selectionArgs[] = {'%'+dealName+'%'};

        String countQuery = "SELECT * FROM "+DEAL_TABLE+" WHERE "+DEAL_NAME+" LIKE "+dealName;
        Cursor c = db.rawQuery(countQuery, null);

       // Cursor c = db.query(DEAL_TABLE, null, selection, selectionArgs, null, null, null);

        while (c.moveToNext()) {

            Deal deal = new Deal();
            deal.setId(c.getInt(0));
            deal.setName(c.getString(1));

            dealList.add(deal);
            c.close();
        }
        return dealList;
    }

    public int getTasksCountByDealId(int id){


        String countQuery = "SELECT * FROM "+TASK_TABLE+" WHERE "+TASK_DEAL_ID+"= " +id;
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;

    }

/** -------------------- TASK Operations  ----------------------**/

    /** INSERT Task **/
    public boolean insertTask(Task task) {

        try
        {
            ContentValues cv = new ContentValues();
            cv.put(TASK_NAME, task.getTaskName());
            cv.put(TASK_NUMBER, task.getTask_number());
            cv.put(TASK_DEAL_ID, task.getDeal_id());

            db = getWritableDatabase();
            long result = db.insert(TASK_TABLE, null, cv);
            if(result>0)
            {
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /** UPDATE Task **/
    public boolean updateTask(String newName, int deal_id, int task_number) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(TASK_NAME, newName);

            int result = db.update(TASK_TABLE, cv, TASK_DEAL_ID+" =? and "+ TASK_NUMBER+" =?",new String[]{String.valueOf(deal_id),String.valueOf(task_number)});
            if (result > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /** DELETE Task **/
    public boolean deleteTask(int deal_id, int task_number)
    {
        try
        {
            int result = db.delete(TASK_TABLE,TASK_DEAL_ID+" =? and "+ TASK_NUMBER+" =?",new String[]{String.valueOf(deal_id),String.valueOf(task_number)});
            if(result>0)
            {
                return true;
            }

        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return false;
    }


    /** GET ALL Tasks **/
    public List<Task> getAllTasks() {
        List<Task> list = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.query(TASK_TABLE, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Task task = new Task();
            task.setTaskName(cursor.getString(0));
            task.setTask_number(cursor.getInt(1));
            task.setDeal_id(cursor.getInt(2));

            Log.d("TaskName column", String.valueOf(cursor.getInt(0)));
            Log.d("TaskNumber column", String.valueOf(cursor.getLong(1)));
            Log.d("DealID column", cursor.getString(2));

            list.add(task);
        }

        cursor.close();
        return list;
    }

    public List<Task> getAllTasksByDealID(int id) {

        List<Task> list = new ArrayList<>();
        String query = "SELECT * FROM "+TASK_TABLE+" WHERE "+TASK_DEAL_ID+"= " +id;
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            Task task = new Task();
            task.setTaskName(cursor.getString(0));
            task.setTask_number(cursor.getInt(1));
            task.setDeal_id(cursor.getInt(2));

            Log.d("TaskName column", String.valueOf(cursor.getInt(0)));
            Log.d("TaskNumber column", String.valueOf(cursor.getLong(1)));
            Log.d("DealID column", cursor.getString(2));

            list.add(task);
        }
        cursor.close();
        return list;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(queryCreateDealTable);
        db.execSQL(queryCreateTaskTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(DROP_DEAL_TB);
        db.execSQL(DROP_TASK_TB);

        db.execSQL(queryCreateDealTable);
        db.execSQL(queryCreateTaskTable);
    }


}
