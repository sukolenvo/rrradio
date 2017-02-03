package com.dakare.radiorecord.app.player.playlist;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import com.dakare.radiorecord.app.load.selection.AbstractSelectionAdapter;
import lombok.Getter;
import lombok.Setter;

public class PlaylistAdapter extends ArrayAdapter<PlaylistItem> implements View.OnClickListener {

    public static final int WRITE_PERMISSION_CODE = 99;

    private final LayoutInflater layoutInflater;
    @Setter
    @Getter
    private int selectedPosition;
    private final Context context;
    private AbstractSelectionAdapter.PermissionProvider permissionProvider;

    public PlaylistAdapter(final Context context, final AbstractSelectionAdapter.PermissionProvider permissionProvider) {
        super(context, 0);
        this.permissionProvider = permissionProvider;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
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
        if (item.isDownloadable()) {
            downloadIcon.setVisibility(View.VISIBLE);
        } else {
            downloadIcon.setVisibility(View.GONE);
        }
        downloadIcon.setTag(position);
        if (this.selectedPosition == position) {
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
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, R.string.permission_guide, Toast.LENGTH_LONG).show();
            permissionProvider.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION_CODE);
        } else {
            int lastClickPosition = (int) v.getTag();
            downloadItem(lastClickPosition);
        }
    }

    private void downloadItem(final int position) {
        PlaylistItem item = getItem(position);
        StorageContract.getInstance().insertDownloadAudio(item);
        context.startService(new Intent(context, FileService.class));
        Toast.makeText(context, R.string.download_starting, Toast.LENGTH_LONG).show();
    }
}
