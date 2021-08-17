package com.dakare.radiorecord.app;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class MenuFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_menu, null);
        Picasso.get()
                .load(R.drawable.menu_label)
                .into((ImageView) inflate.findViewById(R.id.menu_label));
        return inflate;
    }
}
