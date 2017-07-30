package cmu.edu.expenser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import static cmu.edu.expenser.SQLiteHelper.COLUMN_AVERAGE;

/**
 * Created by dandanshi on 29/07/2017.
 */

public class ItemDAO {
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = {
            SQLiteHelper.COLUMN_USERID,
            SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TOTAL,
            SQLiteHelper.COLUMN_DATE,
            SQLiteHelper.COLUMN_CATEGORY,
            SQLiteHelper.COLUMN_PEOPLE,
            SQLiteHelper.COLUMN_AVERAGE};

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

    public void insertItem(String userId, double total, String date, String category, int people) {
        ContentValues newItem = new ContentValues();
        newItem.put(SQLiteHelper.COLUMN_USERID, userId);
        newItem.put(SQLiteHelper.COLUMN_TOTAL, total);
        newItem.put(SQLiteHelper.COLUMN_DATE, date);
        newItem.put(SQLiteHelper.COLUMN_CATEGORY, category);
        newItem.put(SQLiteHelper.COLUMN_PEOPLE, people);

        double average = total / people;
        newItem.put(SQLiteHelper.COLUMN_AVERAGE, average);

        open();
        database.insert(SQLiteHelper.TABLE_NAME, null, newItem);
        close();
    }

    public void updateItem(long id, String userId, double total, String date, String category, int people)  {
        ContentValues editItem = new ContentValues();
        editItem.put(SQLiteHelper.COLUMN_USERID, userId);
        editItem.put(SQLiteHelper.COLUMN_TOTAL, total);
        editItem.put(SQLiteHelper.COLUMN_DATE, date);
        editItem.put(SQLiteHelper.COLUMN_CATEGORY, category);
        editItem.put(SQLiteHelper.COLUMN_PEOPLE, people);

        double average = total / people;
        editItem.put(SQLiteHelper.COLUMN_AVERAGE, average);

        open();
        database.update(SQLiteHelper.TABLE_NAME, editItem,
                "_id=" + id, null); close();
    }

//    public Cursor getAllItemTotal() {
//        database = dbHelper.getReadableDatabase();
//        return database.query(SQLiteHelper.TABLE_NAME, new String[] {"_id", "total"},
//                null, null, null, null, "_id");
//    }
//

    public Cursor getAllItems(String userId) {
        database = dbHelper.getReadableDatabase();
        String[] tableColumns = new String[] {"_id", "userID", "total",
                "date", "category", "people", "average"};
        String whereClause = "userID = ?";
        String[] whereArgs = new String[] {userId};
        String orderBy = "date";

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, orderBy);
        return cursor;
    }

    public List<Item> getAllItemsLists(String userId) {
        List<Item> events = new ArrayList<Item>();
        String[] tableColumns = new String[] {"_id", "userID", "total",
                "date", "category", "people", "average"};
        String whereClause = "userID = ?";
        String[] whereArgs = new String[] {userId};
        String orderBy = "date";

        Cursor cursor = database.query(
                SQLiteHelper.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Item item = cursorToItem(cursor);
            events.add(item);
            cursor.moveToNext();
        } // make sure to close the cursor cursor.close();
        return events;
    }

    private Item cursorToItem(Cursor cursor) {
        String userId = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_USERID));
        int total = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_TOTAL));

        String date = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DATE));
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        Date transactionDate = new Date();
        try {
            transactionDate = df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String category = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CATEGORY));
        int people = cursor.getInt(cursor.getColumnIndex(SQLiteHelper.COLUMN_PEOPLE));

        Item item = new Item(userId, total, transactionDate, category, people);
        return item;
    }

//    public Cursor getOneEvent(long id) {
//        database = dbHelper.getReadableDatabase();
//        return database.query(SQLiteHelper.TABLE_NAME, null, "_id=" + id,
//                null, null, null, null);
//    }
//
//    public void deleteEvent(long id) {
//        open();
//        database.delete(SQLiteHelper.TABLE_NAME, "_id=" + id, null);
//        close();
//    }
}
