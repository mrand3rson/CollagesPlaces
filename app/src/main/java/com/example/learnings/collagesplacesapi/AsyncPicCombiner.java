package com.example.learnings.collagesplacesapi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class AsyncPicCombiner extends AsyncTask<ArrayList<String>, Void, Void> {

    private final int offsetWidth = 10;
    private final int offsetHeight = 5;
    private final int horizontalCount = 2;
    //private final int verticalCount = 2;
    ArrayList<Bitmap> bitmapsArray;
    BitmapGetter getter;
    private Context context;
    private int i;

    public AsyncPicCombiner(Context context, BitmapGetter getter) {
        this.context = context;
        this.getter = getter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(ArrayList<String>[] params) {
        ArrayList<String> urls = params[0];
        ArrayList<Integer> indexes = new ArrayList<>(4);

        bitmapsArray = new ArrayList<>();
        for (i = 0; i < urls.size() && i < 4; i++) {
            int random;
            do {
                random = new Random().nextInt(urls.size());
            }
            while (indexes.contains(random));
            indexes.add(random);

            String query = "https://maps.googleapis.com/maps/api/place/photo?maxheight=200"
                    + "&key=" + context.getString(R.string.google_maps_key)
                    + "&photoreference=" + urls.get(random);

            Bitmap bitmap;
            try {
                InputStream in = new URL(query).openStream();
                bitmap = BitmapFactory.decodeStream(in);
                bitmapsArray.add(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void o) {
        super.onPostExecute(o);

        Bitmap bmp;
        int maxWidth;
        int maxHeight;

        switch (bitmapsArray.size()) {
            case 4: {
                maxWidth = getMaxWidth();
                maxHeight = getMaxHeight();

                bmp = Bitmap.createBitmap(maxWidth,
                        maxHeight, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(bmp);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    for (i = 0; i < bitmapsArray.size(); i++) {
                        int left;
                        int top;
                        int right;
                        int bottom;

                        if (isStartOfRow(i)) {
                            left = 0;
                            right = bitmapsArray.get(i).getWidth();
                        } else {
                            left = bitmapsArray.get(i-1).getWidth() + offsetWidth;
                            right = maxWidth;
                        }

                        if (isFirstRow(i)) {
                            top = 0;
                            bottom = bitmapsArray.get(i).getHeight();
                        } else {
                            top = bitmapsArray.get(i-horizontalCount).getHeight() + offsetHeight;
                            bottom = maxHeight;
                        }

                        c.drawBitmap(bitmapsArray.get(i),
                                null,
                                new Rect(left,
                                        top,
                                        right,
                                        bottom),
                                null);
                    }

                }
                else {
                    Bitmap rowBitmap = null;
                    Canvas rowCanvas = null;

                    for (int i = 0; i < bitmapsArray.size(); i++) {
                        if (isStartOfRow(i)) {
                            rowBitmap = Bitmap.createBitmap(getRowWidth(i/horizontalCount),
                                    maxHeight / 2, Bitmap.Config.ARGB_8888);
                            rowCanvas = new Canvas(rowBitmap);
                        }
                        int left = (isStartOfRow(i)? 0: bitmapsArray.get(i-1).getWidth() + offsetWidth);
                        int top = 0;

                        rowCanvas.drawBitmap(bitmapsArray.get(i),
                                left,
                                top,
                                null);

                        if (isEndOfRow(i)) {
                            int rowTop = (isFirstRow(i) ? 0 : rowBitmap.getHeight() + offsetHeight);
                            int rowLeft = (isNarrow(i, maxWidth) ?
                                    maxWidth / 2 - rowBitmap.getWidth() / 2 :
                                    0);
                            c.drawBitmap(rowBitmap,
                                    rowLeft,
                                    rowTop,
                                    null);
                        }
                    }
                }

                break;
            }
            default: {
                bmp = Bitmap.createBitmap(bitmapsArray.get(0).getWidth(), bitmapsArray.get(0).getHeight(), Bitmap.Config.ARGB_8888);
            }
        }


        getter.Send(bmp);
    }

    private boolean isNarrow(int i, int maxWidth) {
        int rowWidth = 0;

        if (i % horizontalCount == 0) {
            for (int j = i; j < i + horizontalCount; j++) {
                rowWidth += bitmapsArray.get(j).getWidth();
            }
            return rowWidth < maxWidth;
        }
        else {
            int rowStart = i/horizontalCount*horizontalCount;
            int rowEnd = rowStart + horizontalCount;

            for (int j = rowStart; j < rowEnd; j++) {
                rowWidth += bitmapsArray.get(j).getWidth();
            }
            return rowWidth < maxWidth + offsetWidth;
        }
    }

    private boolean isStartOfRow(int i) {
        return (i + 1) % horizontalCount == 1;
    }

    private boolean isEndOfRow(int i) {
        return (i + 1) % horizontalCount == 0;
    }

    private boolean isFirstRow(int i) {
        return i < horizontalCount;
    }

    private int getRowWidth(int row) {
        int rowWidth = 0;
        int rowStart = row * horizontalCount;
        for (int i = rowStart; i < rowStart+horizontalCount; i++) {
            rowWidth += bitmapsArray.get(i).getWidth();
        }
        return rowWidth;
    }

    private int getMaxWidth() {
        return Math.max(bitmapsArray.get(0).getWidth() + bitmapsArray.get(1).getWidth(),
                bitmapsArray.get(2).getWidth() + bitmapsArray.get(3).getWidth()) + offsetWidth;
    }

    private int getMaxHeight() {
        return Math.max(bitmapsArray.get(0).getHeight() + bitmapsArray.get(2).getHeight(),
                bitmapsArray.get(1).getHeight() + bitmapsArray.get(3).getHeight());
    }
}
