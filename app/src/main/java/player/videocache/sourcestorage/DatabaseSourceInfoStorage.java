package player.videocache.sourcestorage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import player.videocache.Preconditions;
import player.videocache.SourceInfo;


class DatabaseSourceInfoStorage extends SQLiteOpenHelper implements SourceInfoStorage {
    private static final String TABLE = "SourceInfo";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_LENGTH = "length";
    private static final String COLUMN_MIME = "mime";
    private static final String[] ALL_COLUMNS = new String[]{"_id", "url", "length", "mime"};


    private static final String CREATE_SQL = "CREATE TABLE SourceInfo (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,url TEXT NOT NULL,mime TEXT,length INTEGER);";


    DatabaseSourceInfoStorage(Context context) {
        super(context, "AndroidVideoCache.db", null, 1);
        Preconditions.checkNotNull(context);
    }


    public void onCreate(SQLiteDatabase db) {
        Preconditions.checkNotNull(db);
        db.execSQL("CREATE TABLE SourceInfo (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,url TEXT NOT NULL,mime TEXT,length INTEGER);");
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Should not be called. There is no any migration");
    }


    public SourceInfo get(String url) {
        Preconditions.checkNotNull(url);
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().query("SourceInfo", ALL_COLUMNS, "url=?", new String[]{url}, null, null, null);
            return (cursor == null || !cursor.moveToFirst()) ? null : convert(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public void put(String url, SourceInfo sourceInfo) {
        Preconditions.checkAllNotNull(new Object[]{url, sourceInfo});
        SourceInfo sourceInfoFromDb = get(url);
        boolean exist = (sourceInfoFromDb != null);
        ContentValues contentValues = convert(sourceInfo);
        if (exist) {
            getWritableDatabase().update("SourceInfo", contentValues, "url=?", new String[]{url});
        } else {
            getWritableDatabase().insert("SourceInfo", null, contentValues);
        }
    }


    public void release() {
        close();
    }


    private SourceInfo convert(Cursor cursor) {
        return new SourceInfo(cursor
                .getString(cursor.getColumnIndexOrThrow("url")), cursor
                .getLong(cursor.getColumnIndexOrThrow("length")), cursor
                .getString(cursor.getColumnIndexOrThrow("mime")));
    }


    private ContentValues convert(SourceInfo sourceInfo) {
        ContentValues values = new ContentValues();
        values.put("url", sourceInfo.url);
        values.put("length", Long.valueOf(sourceInfo.length));
        values.put("mime", sourceInfo.mime);
        return values;
    }
}
