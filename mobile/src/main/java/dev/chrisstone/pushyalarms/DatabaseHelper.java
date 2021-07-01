package dev.chrisstone.pushyalarms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "alarmclock.db";

    private static final String SQL_CREATE_ALARM = "CREATE TABLE " + DatabaseContract.Alarm.TABLE_NAME + " (" +
            DatabaseContract.Alarm._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            DatabaseContract.Alarm.COLUMN_NAME_ALARM_NAME + " TEXT," +
            DatabaseContract.Alarm.COLUMN_NAME_ALARM_TIME_HOUR + " INTEGER," +
            DatabaseContract.Alarm.COLUMN_NAME_ALARM_TIME_MINUTE + " INTEGER," +
            DatabaseContract.Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS + " TEXT," +
            DatabaseContract.Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY + " BOOLEAN," +
            DatabaseContract.Alarm.COLUMN_NAME_ALARM_TONE + " TEXT," +
            DatabaseContract.Alarm.COLUMN_NAME_ALARM_ENABLED + " BOOLEAN" +
            " )";

    private static final String SQL_DELETE_ALARM =
            "DROP TABLE IF EXISTS " + DatabaseContract.Alarm.TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALARM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM);
        onCreate(db);
    }

    private AlarmModel populateModel(Cursor c) {
        AlarmModel model = new AlarmModel();
        model.id = c.getLong(c.getColumnIndex(DatabaseContract.Alarm._ID));
        model.name = c.getString(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_NAME));
        model.timeHour = c.getInt(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_TIME_HOUR));
        model.timeMinute = c.getInt(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_TIME_MINUTE));
        model.repeatWeekly = c.getInt(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY)) != 0;
        if (!c.getString(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_TONE)).isEmpty())
            model.alarmTone = Uri.parse(c.getString(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_TONE)));
        else
            model.alarmTone = null;
        model.isEnabled = c.getInt(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_ENABLED)) != 0;

        String[] repeatingDays = c.getString(c.getColumnIndex(DatabaseContract.Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS)).split(",");
        for (int i = 0; i < repeatingDays.length; ++i) {
            model.setRepeatingDay(i, !repeatingDays[i].equals("false"));
        }

        return model;
    }

    private ContentValues populateContent(AlarmModel model) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Alarm.COLUMN_NAME_ALARM_NAME, model.name);
        values.put(DatabaseContract.Alarm.COLUMN_NAME_ALARM_TIME_HOUR, model.timeHour);
        values.put(DatabaseContract.Alarm.COLUMN_NAME_ALARM_TIME_MINUTE, model.timeMinute);
        values.put(DatabaseContract.Alarm.COLUMN_NAME_ALARM_REPEAT_WEEKLY, model.repeatWeekly);
        values.put(DatabaseContract.Alarm.COLUMN_NAME_ALARM_TONE, model.alarmTone != null ? model.alarmTone.toString() : "");
        values.put(DatabaseContract.Alarm.COLUMN_NAME_ALARM_ENABLED, model.isEnabled);

        StringBuilder repeatingDays = new StringBuilder();
        for (int i = 0; i < 7; ++i) {
            repeatingDays.append(model.getRepeatingDay(i)).append(",");
        }
        values.put(DatabaseContract.Alarm.COLUMN_NAME_ALARM_REPEAT_DAYS, repeatingDays.toString());

        return values;
    }

    public long createAlarm(AlarmModel model) {
        ContentValues values = populateContent(model);
        return getWritableDatabase().insert(DatabaseContract.Alarm.TABLE_NAME, null, values);
    }

    public void updateAlarm(AlarmModel model) {
        ContentValues values = populateContent(model);
        getWritableDatabase().update(DatabaseContract.Alarm.TABLE_NAME, values, DatabaseContract.Alarm._ID + " = ?", new String[]{String.valueOf(model.id)});
    }

    public AlarmModel getAlarm(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + DatabaseContract.Alarm.TABLE_NAME + " WHERE " + DatabaseContract.Alarm._ID + " = " + id;

        Cursor c = db.rawQuery(select, null);

        if (c.moveToNext()) {
            return populateModel(c);
        }
        return null;
    }

    public List<AlarmModel> getAlarms() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + DatabaseContract.Alarm.TABLE_NAME;

        Cursor c = db.rawQuery(select, null);

        List<AlarmModel> alarmList = new ArrayList<>();

        while (c.moveToNext())
            alarmList.add(populateModel(c));

        if (!alarmList.isEmpty())
            return alarmList;

        return null;
    }

    public int deleteAlarm(long id) {
        return getWritableDatabase().delete(DatabaseContract.Alarm.TABLE_NAME, DatabaseContract.Alarm._ID + " = ?",
                new String[]{String.valueOf(id)});
    }
}