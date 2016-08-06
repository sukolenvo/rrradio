package com.dakare.radiorecord.app.load.section;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SectionMusicItem implements Parcelable {
    private String artist;
    private String song;
    private String url;

    public SectionMusicItem(final Parcel parcel) {
        artist = parcel.readString();
        song = parcel.readString();
        url = parcel.readString();
    }

    public void setArtist(final String artist) {
        if (artist != null) {
            String[] values = artist.split(" - ", 2);
            if (values.length == 1) {
                this.artist = values[0];
            } else {
                this.artist = values[0];
                this.song = values[1];
            }
        }
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
        public SectionMusicItem createFromParcel(Parcel in) {
            return new SectionMusicItem(in);
        }

        public SectionMusicItem[] newArray(int size) {
            return new SectionMusicItem[size];
        }
    };
}
