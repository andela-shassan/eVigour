package checkpoint.andela.graph;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

import checkpoint.andela.db.PushUpRecordDB;
import checkpoint.andela.evigour.R;
import checkpoint.andela.model.PushUpRecord;
import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ReportGraph extends AppCompatActivity {

    private SQLiteDatabase db;
    private LineChart mpChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_graph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.general_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Report Graph");
        mpChart = (LineChart) findViewById(R.id.mp_chart);
        reportGraph();
    }

    public void reportGraph() {
        ArrayList<Entry> values = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        Entry record;
        int i = 0;

        PushUpRecordDB dbHelper = new PushUpRecordDB(this);
        QueryResultIterable<PushUpRecord> pushRecords = null;
        db = dbHelper.getWritableDatabase();
        Cursor allRecord = cupboard().withDatabase(db).query(PushUpRecord.class).getCursor();

        if (allRecord != null && allRecord.getCount() > 0) {
            try {
                pushRecords = cupboard().withCursor(allRecord).iterate(PushUpRecord.class);
                for (PushUpRecord p : pushRecords) {
                    record = new Entry(p.getNumberOfPushUp(), i);
                    values.add(0, record);
                    labels.add(p.getDate());
                    i++;
                }
            } finally {
                pushRecords.close();
            }
            LineDataSet report = new LineDataSet(values, " Push up record");
            report.setAxisDependency(YAxis.AxisDependency.LEFT);
            report.setColor(Color.parseColor("#9C27B0"));
            report.setCircleColor(Color.RED);
            report.setCircleColorHole(Color.RED);
            report.setHighLightColor(Color.parseColor("#448AFF"));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(report);

            LineData data = new LineData(labels, dataSets);

            XAxis xAxis = mpChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(10f);
            xAxis.setDrawAxisLine(true);
            xAxis.setDrawGridLines(false);

            mpChart.setData(data);

            Legend l = mpChart.getLegend();
            l.setEnabled(false);
            mpChart.setAutoScaleMinMaxEnabled(true);
            mpChart.setDescription("");
            mpChart.setDrawBorders(false);
            mpChart.setVisibleXRangeMaximum(5);
            mpChart.setBorderColor(Color.parseColor("#FFFFFF"));
            mpChart.setScaleEnabled(true);
            mpChart.setTouchEnabled(true);
            mpChart.getAxisLeft().setEnabled(true);
            mpChart.getAxisLeft().setTextColor(Color.parseColor("#FFFFFF"));
            mpChart.getAxisRight().setEnabled(false);
            mpChart.getXAxis().setEnabled(true);
            mpChart.getXAxis().setTextColor(Color.parseColor("#FFFFFF"));
            mpChart.animateXY(2500, 2500);
            mpChart.invalidate();
        }
    }
}
