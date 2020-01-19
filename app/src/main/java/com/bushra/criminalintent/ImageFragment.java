package com.bushra.criminalintent;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class ImageFragment extends DialogFragment {

    private static final String ARG_IMAGE = "image";
    private ImageView cImageView;
    Bitmap bitmap;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image, null);

        bitmap = getArguments().getParcelable(ARG_IMAGE);

        cImageView = v.findViewById(R.id.dialog_image);
        cImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

    }


    public static ImageFragment newInstance(Bitmap bitmap) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE, bitmap);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

}
