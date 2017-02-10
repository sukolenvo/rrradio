package com.dakare.radiorecord.app.player.playlist.spannable;

import com.dakare.radiorecord.app.player.playlist.PlaylistItem;

public interface PlaylistItemBuilder {

    CharSequence buildItemName(int position, PlaylistItem item);

    CharSequence buildItemNameHighlighted(PlaylistItem item);
}
