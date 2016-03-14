package checkpoint.andela.model;

/**
 * Created by andela on 14/03/2016.
 */
public class PushUpRecord {
    public Long _id;
    private String date;
    private int numberOfPushUp;

    public PushUpRecord() {
    }

    public PushUpRecord(String date, int numberOfPushUp) {
        this.date = date;
        this.numberOfPushUp = numberOfPushUp;
    }

    public Long get_id() {
        return _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getNumberOfPushUp() {
        return numberOfPushUp;
    }

    public void setNumberOfPushUp(int numberOfPushUp) {
        this.numberOfPushUp = numberOfPushUp;
    }
}
