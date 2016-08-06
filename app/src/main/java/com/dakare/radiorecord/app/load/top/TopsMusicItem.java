package com.dakare.radiorecord.app.load.top;

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
public class TopsMusicItem implements Parcelable {
    private String artist;
    private String song;
    @Getter
    private String url;

    public TopsMusicItem(final Parcel parcel) {
        artist = parcel.readString();
        song = parcel.readString();
        url = parcel.readString();
    }

    public String getArtist() {
        return TextUtils.isEmpty(artist) ? RecordApplication.getInstance().getString(R.string.artist_stub) : artist;
    }

    public String getSong() {
        return TextUtils.isEmpty(song) ? RecordApplication.getInstance().getString(R.string.song_name_stub) : song;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(artist);
        dest.writeString(song);
        dest.writeString(url);
    }

    public static final Creator CREATOR = new Creator() {
        public TopsMusicItem createFromParcel(Parcel in) {
            return new TopsMusicItem(in);
        }

        public TopsMusicItem[] newArray(int size) {
            return new TopsMusicItem[size];
        }
    };
}
