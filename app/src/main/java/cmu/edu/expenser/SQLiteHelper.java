package cmu.edu.expenser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by dandanshi on 6/3/17.
 */

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "receipt";
    public static final String DATABASE_NAME = "Expenser";
    public static final int DATABASE_VERSION = 1;

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERID = "userId";
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_PEOPLE = "people";
    public static final String COLUMN_AVERAGE = "average";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_NAME
                    + "(_id integer primary key autoincrement,"
                    + "userId TEXT, "
                    + "total REAL, "
                    + "date TEXT, "
                    + "category TEXT, "
                    + "people INT, "
                    + "average REAL);";

    /**
     * @param context * @param name * @param factory * @param version */
    public SQLiteHelper(Context context, String name,
                        SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); onCreate(db);
    }
}