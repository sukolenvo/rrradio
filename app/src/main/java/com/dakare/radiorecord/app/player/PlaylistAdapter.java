package com.dakare.radiorecord.app.player;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.dakare.radiorecord.app.R;
import com.dakare.radiorecord.app.database.provider.StorageContract;
import com.dakare.radiorecord.app.download.service.FileService;
import com.dakare.radiorecord.app.player.playlist.PlaylistItem;
import lombok.Setter;

public class PlaylistAdapter extends ArrayAdapter<PlaylistItem> implements View.OnClickListener {

    public static final int WRITE_PERMISSION_CODE = 99;

    private final LayoutInflater layoutInflater;
    @Setter
    private int position;
    private final Activity activity;
    private int lastClickPosition;

    public PlaylistAdapter(final Activity activity) {
        super(activity, 0);
        this.activity = activity;
        layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.item_playlist, null);
            view.findViewById(R.id.download_icon).setOnClickListener(this);
        } else {
            view = convertView;
        }
        TextView titleView = (TextView) view.findViewById(R.id.playlist_title);
        PlaylistItem item = getItem(position);
        View downloadIcon = view.findViewById(R.id.download_icon);
        if (item.isLive() || item.getUrl().startsWith("file://")) {
            downloadIcon.setVisibility(View.GONE);
        } else {
            downloadIcon.setVisibility(View.VISIBLE);
        }
        downloadIcon.setTag(position);
        if (this.position == position) {
            titleView.setText(item.getTitle() + " - " + item.getSubtitle());
            titleView.setTextColor(view.getResources().getColor(R.color.playlist_active));
            view.findViewById(R.id.playlist_icon).setVisibility(View.VISIBLE);
        } else {
            String positionString = (position + 1) + ".  ";
            Spannable spannable = new SpannableString(positionString + item.getTitle() + " - " + item.getSubtitle());
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, positionString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            int titleEnd = positionString.length() + item.getTitle().length();
            spannable.setSpan(new ForegroundColorSpan(Color.rgb(0x2b, 0x58, 0x7a)), positionString.length(), titleEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), titleEnd, titleEnd + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(view.getResources().getColor(R.color.playlist_default)),
                    titleEnd + 3, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleView.setText(spannable);
            view.findViewById(R.id.playlist_icon).setVisibility(View.GONE);
        }
        return view;
    }

    @Override
    public void onClick(final View v) {
        lastClickPosition = (int) v.getTag();
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        } else {
            downloadItem(lastClickPosition);
        }
    }

    private void downloadItem(final int position) {
        PlaylistItem item = getItem(position);
        StorageContract.getInstance().insertDownloadAudio(item);
        activity.startService(new Intent(activity, FileService.class));
        Toast.makeText(activity, R.string.download_starting, Toast.LENGTH_LONG).show();
    }

    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (requestCode == WRITE_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            downloadItem(lastClickPosition);
        }
    }
}
