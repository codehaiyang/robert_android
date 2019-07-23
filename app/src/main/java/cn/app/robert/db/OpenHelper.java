package cn.app.robert.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import androidx.annotation.Nullable;
import cn.app.robert.entity.ActionBean;
import cn.app.robert.entity.Actions;

public class OpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "OpenHelper";

    public static final String DB_NAME = "database.db";

    public static final int DB_VERSION = 1;

    public static final String TABLE_ACTIONS = "actions";

    public static final String TABLE_ACTION_DETAIL = "actions_detail";

    private Cursor cursor;

    /**
     * 建表语句（创建动作列表）
     */
    public static final String CREATE_ACTION_SQL = "create table actions ("
            + "id integer primary key autoincrement,"
            + "name varchar(20),"
            + "duration integer,"
            + "path varchar(255),"
            + "number integer)";

    /**
     * 建表语句（创建动作列表）
     */
    public static final String CREATE_ACTION_ITEM_SQL = "create table actions_detail ("
            + "id integer primary key autoincrement,"
            + "actions integer,"
            + "number integer,"
            + "color integer,"
            + "colorMode integer,"
            + "tapCount integer,"
            + "speed integer,"
            + "slashDuration integer,"
            + "musicPosition integer,"
            + "lastPosition integer,"
            + "sleepInterval integer)";

    public OpenHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ACTION_SQL);
        // 创建动作表
        db.execSQL(CREATE_ACTION_ITEM_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void del(){
        SQLiteDatabase writableDatabase = getWritableDatabase();
        writableDatabase.execSQL("drop table " + TABLE_ACTIONS);
        writableDatabase.execSQL("drop table " + TABLE_ACTION_DETAIL);
    }

    /**
     * 添加动作
     * @param values
     * @param tableName
     */
    public void insert(ContentValues values, String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(tableName, null, values);
        Log.i(TAG, "增加一行");
        db.close();
    }

    /**
     * 获取保存的列表
     * @return
     */
    public List<Actions> getActions(){
        Cursor query = query(TABLE_ACTIONS);
        List<Actions> actions = new ArrayList<>();
        while (query.moveToNext()){
            Actions action = new Actions();
            int nameIndex = query.getColumnIndex("name");
            String name = query.getString(nameIndex);
            int durationIndex = query.getColumnIndex("duration");
            int duration = query.getInt(durationIndex);
            int pathIndex = query.getColumnIndex("path");
            String path = query.getString(pathIndex);
            int numberIndex = query.getColumnIndex("number");
            String number = query.getString(numberIndex);
            action.setName(name);
            action.setDuration(duration);
            action.setPath(path);
            action.setNumber(number);
            actions.add(action);
        }
        query.close();
        return actions;
    }

    public List<Actions> getActions(String actioName){
        Cursor query = queryByName(TABLE_ACTIONS,actioName);
        List<Actions> actions = new ArrayList<>();
        while (query.moveToNext()){
            Actions action = new Actions();
            int nameIndex = query.getColumnIndex("name");
            String name = query.getString(nameIndex);
            int durationIndex = query.getColumnIndex("duration");
            int duration = query.getInt(durationIndex);
            int pathIndex = query.getColumnIndex("path");
            String path = query.getString(pathIndex);
            int numberIndex = query.getColumnIndex("number");
            String number = query.getString(numberIndex);
            action.setName(name);
            action.setDuration(duration);
            action.setPath(path);
            action.setNumber(number);
            actions.add(action);
        }
        query.close();
        return actions;
    }

    public List<ActionBean> getActionDetail(String number){
        Cursor query = query(TABLE_ACTION_DETAIL,number);
        List<ActionBean> actions = new ArrayList<>();
        while (query.moveToNext()){
            ActionBean action = new ActionBean(0);
            int actionsIndex = query.getColumnIndex("actions");
            int actionId = query.getInt(actionsIndex);
            int colorIndex = query.getColumnIndex("color");
            int color = query.getInt(colorIndex);
            int colorModeIndex = query.getColumnIndex("colorMode");
            int colorMode = query.getInt(colorModeIndex);
            int tapCountIndex = query.getColumnIndex("tapCount");
            int tapCount = query.getInt(tapCountIndex);
            int speedIndex = query.getColumnIndex("speed");
            int speed = query.getInt(speedIndex);
            int slashDurationIndex = query.getColumnIndex("slashDuration");
            int slashDuration = query.getInt(slashDurationIndex);
            int sleepIntervalIndex = query.getColumnIndex("sleepInterval");
            int sleepInterval= query.getInt(sleepIntervalIndex);
            int musicPositionIndex = query.getColumnIndex("musicPosition");
            int musicPosition= query.getInt(musicPositionIndex);
            int lastPositionIndex = query.getColumnIndex("lastPosition");
            int lastPosition= query.getInt(lastPositionIndex);
            action.setAction(actionId);
            action.setColor(color);
            action.setColorMode(colorMode);
            action.setTapCount(tapCount);
            action.setSpeed(speed);
            action.setSlashDuration(slashDuration);
            action.setSleepInterval(sleepInterval);
            action.setMusicPosition(musicPosition);
            action.setLastPosition(lastPosition);
            actions.add(action);
        }
        query.close();
        return actions;
    }


    /**
     * 删除某一行
     * @param musicName
     * @param tableName
     */
    public void delete(String musicName, String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, "name=?", new String[]{String.valueOf(musicName)});
    }

    public void deleteByNumber(String num, String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(tableName, "number=?", new String[]{String.valueOf(num)});
    }

    /**
     * 查询所有
     * @param tableName
     * @return
     */
    public Cursor query(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        cursor = db.query(tableName, null, null, null, null, null, null);
        return cursor;
    }

    /**
     * 条件查询
     * @param tableName
     * @param num
     * @return
     */
    public Cursor query(String tableName,String num) {
        SQLiteDatabase db = getWritableDatabase();
        cursor = db.query(tableName, null, " number = ? ", new String[]{num}, null, null, null);
        return cursor;
    }

    public Cursor queryByName(String tableName,String name) {
        SQLiteDatabase db = getWritableDatabase();
        cursor = db.query(tableName, null, " name = ? ", new String[]{name}, null, null, null);
        return cursor;
    }

    public ContentValues actionToContentValues(Actions action) {
        Log.d(TAG, "actionToContentValues: " + action);
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", action.getName());
        contentValues.put("duration",action.getDuration());
        contentValues.put("path",action.getPath());
        contentValues.put("number",action.getNumber());
        return contentValues;
    }

    public ContentValues actionBeanToContentValues(ActionBean action,String number) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("actions",action.getAction());
        contentValues.put("color", action.getColor());
        contentValues.put("colorMode", action.getColorMode());
        contentValues.put("tapCount", action.getTapCount());
        contentValues.put("speed",action.getSpeed());
        contentValues.put("slashDuration",action.getSlashDuration());
        contentValues.put("sleepInterval",action.getSleepInterval());
        contentValues.put("musicPosition",action.getMusicPosition());
        contentValues.put("lastPosition",action.getLastPosition());
        contentValues.put("number",number);
        return contentValues;
    }

    public String getNumber() {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String newDate=sdf.format(new Date());
        String result="";
        Random random=new Random();
        for(int i=0;i<3;i++){
            result+=random.nextInt(10);
        }
        return newDate+result;
    }
}
