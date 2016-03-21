package checkpoint.andela.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andela on 14/03/2016.
 */
public class PushUpRecord {
    public Long _id;
    private String date;
    private int numberOfPushUp;

    public PushUpRecord() {
        this.date = setDate();
    }

    public PushUpRecord(int numberOfPushUp) {
        this.date = setDate();
        this.numberOfPushUp = numberOfPushUp;
    }

    public Long get_id() {
        return _id;
    }

    public String getDate() {
        return date;
    }

    public int getNumberOfPushUp() {
        return numberOfPushUp;
    }

    public void setNumberOfPushUp(int numberOfPushUp) {
        this.numberOfPushUp = numberOfPushUp;
    }

    public String setDate() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }
}
