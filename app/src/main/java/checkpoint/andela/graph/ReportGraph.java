
package checkpoint.andela.graph;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

        //drawGraph();
        //willIAm();
        graph();
    }

    private void drawGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 9),
                new DataPoint(5, 5),
                new DataPoint(6, 3),
                new DataPoint(7, 4),
                new DataPoint(8, 6)
        });
        graph.addSeries(series);
    }

    private void willIAm(){
        LineChartView li = (LineChartView) findViewById(R.id.linechart);
        String[] test = {"we", "Yu", "Her", "She", "They"};

        float[] value = { 46,60,12,8,33};
        LineSet dataSet = new LineSet(test, value);
        dataSet.setDotsColor(Color.parseColor("#b3b5bb"));

        dataSet.setSmooth(true);
        dataSet.setDotsColor(Color.parseColor("#ff0000"));
        dataSet.setFill(Color.parseColor("#E1BEE7"));

        li.addData(dataSet);
        li.setStep(5);
        li.show();

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
            }
        } finally {
            pushRecords.close();
        }
        LineChartView li = (LineChartView) findViewById(R.id.linechart);
        dataSet.setDotsColor(Color.parseColor("#b3b5bb"));

        dataSet.setSmooth(true);
        dataSet.setDotsColor(Color.parseColor("#ff0000"));
        dataSet.setFill(Color.parseColor("#E1BEE7"));
        //dataSet.endAt(2);

        li.addData(dataSet);
        li.setStep(5);
        li.show();
    }
}
