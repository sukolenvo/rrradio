package com.dakare.radiorecord.app.player.playlist.spannable;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.RecordApplication;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;

public class DarkPlaylistItemBuilder implements PlaylistItemBuilder {

    private final int primaryColor;
    private final int secondaryColor;

    public DarkPlaylistItemBuilder() {
        this.primaryColor = RecordApplication.getInstance().getResources().getColor(R.color.primary_text_dark);
        this.secondaryColor = RecordApplication.getInstance().getResources().getColor(R.color.secondary_text_dark);
    }

    @Override
    public CharSequence buildItemName(final int position, final PlaylistItem item) {
        String positionString = (position + 1) + ".  ";
        Spannable spannable = new SpannableString(positionString + item.getTitle() + " - " + item.getSubtitle());
        int titleEnd = positionString.length() + item.getTitle().length();
        spannable.setSpan(new ForegroundColorSpan(primaryColor), 0, titleEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(secondaryColor),titleEnd, spannable.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    public CharSequence buildItemNameHighlighted(final PlaylistItem item) {
        SpannableString spannableString = new SpannableString(item.getTitle() + " - " + item.getSubtitle());
        spannableString.setSpan(new ForegroundColorSpan(primaryColor), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
