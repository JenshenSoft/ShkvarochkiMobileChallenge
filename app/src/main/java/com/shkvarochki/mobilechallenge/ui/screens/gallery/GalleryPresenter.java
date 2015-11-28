package com.shkvarochki.mobilechallenge.ui.screens.gallery;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.webkit.MimeTypeMap;

/**
 * Created by Евгений on 28.11.2015.
 */
public class GalleryPresenter implements IGalleryPresenter {

    public static final int LoaderId_Photos = 0;
    private final IGalleryView galleryView;
    private LoaderManager.LoaderCallbacks<Cursor> photosLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            String jpgExtension = "jpeg";
            String pngExtension = "png";
            String jpgMimeType = mimeTypeMap.getMimeTypeFromExtension(jpgExtension);
            String pngMimeType = mimeTypeMap.getMimeTypeFromExtension(pngExtension);

            Log.d("MIME", jpgMimeType + "; " + pngMimeType);

            final String selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ? OR " +
                    MediaStore.Files.FileColumns.MIME_TYPE + " = ?";
            final String[] selectionArgs = new String[]{jpgMimeType, pngMimeType};

            final String orderBy = MediaStore.Images.Media.DATE_MODIFIED + " DESC";
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            return new CursorLoader(galleryView.getContext(), uri, projection, selection, selectionArgs, orderBy);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            galleryView.setData(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            galleryView.onLoaderReset();

        }
    };

    public GalleryPresenter(IGalleryView galleryView) {
        this.galleryView = galleryView;
    }

    /* listeners */

    @Override
    public void initLoaders() {
        galleryView.getSupportLoaderManager().initLoader(LoaderId_Photos, null, photosLoaderCallbacks).onContentChanged();
    }

    @Nullable
    public String getPath(Context context, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor == null)
                return null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (!cursor.moveToFirst())
                return null;
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }
}
