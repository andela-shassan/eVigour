package checkpoint.andela.graph;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;

import checkpoint.andela.db.PushUpRecordDB;
import checkpoint.andela.evigour.R;
import checkpoint.andela.model.PushUpRecord;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ReportGraph extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_graph);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Report Graph");
        graph();
    }

    private void graph(){
        PushUpRecordDB dbHelper = new PushUpRecordDB(this);
        QueryResultIterable<PushUpRecord> pushRecords = null;
        db = dbHelper.getWritableDatabase();
        Cursor allRecord = cupboard().withDatabase(db).query(PushUpRecord.class).getCursor();
        LineSet dataSet = new LineSet();

        try {
            pushRecords = cupboard().withCursor(allRecord).iterate(PushUpRecord.class);
            for (PushUpRecord p : pushRecords) {
                dataSet.addPoint(p.getDate(), p.getNumberOfPushUp());
                Log.d("semiu", p.getDate()+" "+p.getNumberOfPushUp());
            }
        } finally {
            pushRecords.close();
        }
        LineChartView li = (LineChartView) findViewById(R.id.linechart);
        dataSet.setDotsColor(Color.parseColor("#b3b5bb"));

        dataSet.setSmooth(true);
        dataSet.setColor(Color.parseColor("#9C27B0"));
        dataSet.setDotsColor(Color.parseColor("#ff0000"));
        dataSet.setFill(Color.parseColor("#E1BEE7"));

        li.addData(dataSet);
        li.setStep(5);
        li.show();
    }
}
