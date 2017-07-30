package cmu.edu.expenser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.name;
import static cmu.edu.expenser.R.string.total;
import static cmu.edu.expenser.SQLiteHelper.COLUMN_PEOPLE;

/**
 * Created by dandanshi on 29/07/2017.
 */

public class ItemDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {
            SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TOTAL,
            SQLiteHelper.COLUMN_DATE,
            SQLiteHelper.COLUMN_CATEGORY,
            SQLiteHelper.COLUMN_PEOPLE };

    public ItemDAO(Context context) {
        dbHelper = new SQLiteHelper (context,
                SQLiteHelper.DATABASE_NAME, null, SQLiteHelper.DATABASE_VERSION);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insertItem(int total, String date, String category, int people) {
        ContentValues newItem = new ContentValues();
        newItem.put(SQLiteHelper.COLUMN_TOTAL, total);
        newItem.put(SQLiteHelper.COLUMN_DATE, date);
        newItem.put(SQLiteHelper.COLUMN_CATEGORY, category);
        newItem.put(SQLiteHelper.COLUMN_PEOPLE, people);

        open();
        database.insert(SQLiteHelper.TABLE_NAME, null, newItem);
        close();
    }

    public void updateEvent(long id, int total, String date, String category, int people)  {
        ContentValues editEvent = new ContentValues();
        editEvent.put(SQLiteHelper.COLUMN_TOTAL, total);
        editEvent.put(SQLiteHelper.COLUMN_DATE, date);
        editEvent.put(SQLiteHelper.COLUMN_CATEGORY, category);
        editEvent.put(SQLiteHelper.COLUMN_PEOPLE, people);

        open();
        database.update(SQLiteHelper.TABLE_NAME, editEvent,
                "_id=" + id, null); close();
    }

    public Cursor getAllEventNames() {
        database = dbHelper.getReadableDatabase();
        return database.query(SQLiteHelper.TABLE_NAME, new String[] {"_id", "name"},
                null, null, null, null, "_id");
    }

    public Cursor getAllEvents() {
        database = dbHelper.getReadableDatabase();
        return database.query(SQLiteHelper.TABLE_NAME, new String[] {"_id", "name", "dateTime",
                "location"}, null, null, null, null, "_id");
    }

    public List<Item> getAllEventLists() {
        List<Item> events = new ArrayList<Item>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_NAME,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            events.add(item);
            cursor.moveToNext();
        } // make sure to close the cursor cursor.close();
        return events;
    }

    private Item cursorToItem(Cursor cursor) {
        int total = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_TOTAL));
        String date = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DATE));
        String category = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CATEGORY));
        int people = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_PEOPLE));
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date transactionDate = new Date();
        try {
            transactionDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Item item = new Item(total, transactionDate, category, people);
        return item;
    }

    public Cursor getOneEvent(long id) {
        database = dbHelper.getReadableDatabase();
        return database.query(SQLiteHelper.TABLE_NAME, null, "_id=" + id,
                null, null, null, null);
    }

    public void deleteEvent(long id) {
        open();
        database.delete(SQLiteHelper.TABLE_NAME, "_id=" + id, null);
        close();
    }
}
