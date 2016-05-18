package com.dakare.radiorecord.app.player;

import android.content.Context;
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
        titleView.setText(item.getTitle() + " - " + item.getSubtitle());
        if (this.position == position)
        {
            titleView.setTextColor(view.getResources().getColor(R.color.playlist_active));
            view.findViewById(R.id.playlist_icon).setVisibility(View.VISIBLE);
            positionView.setVisibility(View.GONE);
        } else
        {
            titleView.setTextColor(view.getResources().getColor(R.color.playlist_default));
            view.findViewById(R.id.playlist_icon).setVisibility(View.GONE);
            positionView.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
