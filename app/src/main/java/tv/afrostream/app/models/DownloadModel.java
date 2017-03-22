package tv.afrostream.app.models;

/**
 * Created by bahri on 15/02/2017.
 */


        import android.os.Parcel;
        import android.os.Parcelable;

public class DownloadModel  implements Parcelable{

    public DownloadModel(){

    }

    private long progress;
    private long currentFileSize;
    private long totalFileSize;

    public long getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeLong(progress);
        dest.writeLong(currentFileSize);
        dest.writeLong(totalFileSize);
    }

    private DownloadModel(Parcel in) {

        progress = in.readInt();
        currentFileSize = in.readInt();
        totalFileSize = in.readInt();
    }

    public static final Parcelable.Creator<DownloadModel> CREATOR = new Parcelable.Creator<DownloadModel>() {
        public DownloadModel createFromParcel(Parcel in) {
            return new DownloadModel(in);
        }

        public DownloadModel[] newArray(int size) {
            return new DownloadModel[size];
        }
    };
}