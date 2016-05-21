package com.dakare.radiorecord.app.player;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import lombok.Setter;

public class PlaylistAdapter extends ArrayAdapter<PlaylistItem>
{

    private final LayoutInflater layoutInflater;
    @Setter
    private int position;

    public PlaylistAdapter(final Context context)
    {
        super(context, 0);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent)
    {
        View view;
        if (convertView == null)
        {
            view = layoutInflater.inflate(R.layout.item_playlist, null);
        } else
        {
            view = convertView;
        }
        TextView positionView = (TextView) view.findViewById(R.id.playlist_position);
        positionView.setText(position + 1 + "");
        TextView titleView = (TextView) view.findViewById(R.id.playlist_title);
        PlaylistItem item = getItem(position);
        if (this.position == position)
        {
            titleView.setText(item.getTitle() + " - " + item.getSubtitle());
            titleView.setTextColor(view.getResources().getColor(R.color.playlist_active));
            view.findViewById(R.id.playlist_icon).setVisibility(View.VISIBLE);
            positionView.setVisibility(View.GONE);
        } else
        {
            Spannable spannable = new SpannableString(item.getTitle() + " - " + item.getSubtitle());
            spannable.setSpan(new ForegroundColorSpan(Color.rgb(0x2b, 0x58, 0x7a)), 0, item.getTitle().length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), item.getTitle().length(), item.getTitle().length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(view.getResources().getColor(R.color.playlist_default)), item.getTitle().length() + 3, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleView.setText(spannable);
            view.findViewById(R.id.playlist_icon).setVisibility(View.GONE);
            positionView.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
