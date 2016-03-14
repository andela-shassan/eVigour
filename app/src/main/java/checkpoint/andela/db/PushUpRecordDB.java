package checkpoint.andela.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import checkpoint.andela.model.PushUpRecord;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

/**
 * Created by andela on 14/03/2016.
 */
public class PushUpRecordDB extends SQLiteOpenHelper{
    private static final String DB_NAME = "evigour.db";
    private static final int DB_VERSION = 1;

    public PushUpRecordDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    static {
        cupboard().register(PushUpRecord.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
