package com.dakare.radiorecord.app.player.playlist.spannable;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;

public class LightPlaylistItemBuilder implements PlaylistItemBuilder {

    @Override
    public CharSequence buildItemName(final int position, final PlaylistItem item) {
        String positionString = (position + 1) + ".  ";
        Spannable spannable = new SpannableString(positionString + item.getTitle() + " - " + item.getSubtitle());
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, positionString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int titleEnd = positionString.length() + item.getTitle().length();
        spannable.setSpan(new ForegroundColorSpan(Color.rgb(0x2b, 0x58, 0x7a)), positionString.length(), titleEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK), titleEnd, titleEnd + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ForegroundColorSpan(0xFF919191),titleEnd + 3, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    @Override
    public CharSequence buildItemNameHighlighted(final PlaylistItem item) {
        SpannableString spannableString = new SpannableString(item.getTitle() + " - " + item.getSubtitle());
        spannableString.setSpan(new ForegroundColorSpan(0xffe96465), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }
}
