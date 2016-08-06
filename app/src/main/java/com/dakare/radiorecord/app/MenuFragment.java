package com.dakare.radiorecord.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_menu, null);
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.menu_label, (ImageView) inflate.findViewById(R.id.menu_label));
        return inflate;
    }
}
