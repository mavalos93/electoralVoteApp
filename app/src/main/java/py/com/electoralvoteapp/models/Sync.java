package py.com.electoralvoteapp.models;

/**
 * Created by Manu0 on 10/22/2017.
 */

public class Sync {

    public static final String TABLE_VOTES = "TABLE_VOTES";
    public static final String CANDIDATES = "CANDIDATES";

    private String progressId;
    private int progress = 0;
    private int max = 0;
    private String type;
    private String title;
    private String message;
    private boolean stop;
    private String timeFinished;

    public String getProgressId() {
        return progressId;
    }

    public void setProgressId(String progressId) {
        this.progressId = progressId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isStop() {
        return stop;
    }

    public String getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(String timeFinished) {
        this.timeFinished = timeFinished;
    }
}
