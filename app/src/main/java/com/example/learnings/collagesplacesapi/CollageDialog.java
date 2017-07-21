package com.example.learnings.collagesplacesapi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.learnings.collagesplacesapi.Remote.AsyncPicCombiner;
import com.example.learnings.collagesplacesapi.Remote.BitmapGetter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;


public class CollageDialog extends DialogFragment {

    int i;
    Uri uri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ArrayList<String> urls = getArguments().getStringArrayList("urls");
        View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_collage, container);

        final View progress = v.findViewById(R.id.progress);
        final View layout = v.findViewById(R.id.layout);


        final ImageView image = (ImageView) v.findViewById(R.id.iv1);
        this.getDialog().setTitle(getString(R.string.places));
        if (urls != null) {
            AsyncPicCombiner combiner = new AsyncPicCombiner(getContext(), new BitmapGetter() {
                @Override
                public void Send(Bitmap bmp) {
                    uri = getImageUri(bmp);
                    image.setImageBitmap(bmp);

                    progress.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }
            });
            combiner.execute(urls);
        } else {
            this.dismiss();
        }

        ImageButton ib = (ImageButton) v.findViewById(R.id.ib2);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/png");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        try {
            String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), inImage, UUID.randomUUID().toString() + ".png", "drawing");
            return Uri.parse(path);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}
