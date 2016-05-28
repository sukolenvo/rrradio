package com.dakare.radiorecord.app.load.history;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class HistoryMusicItem implements Parcelable
{
    private String artist;
    private String song;
    private String url;
    private String when;
    @Getter
    private boolean visible = true;

    public HistoryMusicItem(final Parcel parcel)
    {
        artist = parcel.readString();
        song = parcel.readString();
        url = parcel.readString();
        when = parcel.readString();
        boolean[] bools = new boolean[1];
        parcel.readBooleanArray(bools);
        visible = bools[0];
    }

    public String getArtist()
    {
        return TextUtils.isEmpty(artist) ? RecordApplication.getInstance().getString(R.string.artist_stub) : artist;
    }

    public String getSong()
    {
        return TextUtils.isEmpty(song) ? RecordApplication.getInstance().getString(R.string.song_name_stub) : song;
    }

    public String getUrl()
    {
        return url;
    }

    public String getWhen()
    {
        return when;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeString(artist);
        dest.writeString(song);
        dest.writeString(url);
        dest.writeString(when);
        dest.writeBooleanArray(new boolean[]{visible});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public HistoryMusicItem createFromParcel(Parcel in) {
            return new HistoryMusicItem(in);
        }

        public HistoryMusicItem[] newArray(int size) {
            return new HistoryMusicItem[size];
        }
    };
}
