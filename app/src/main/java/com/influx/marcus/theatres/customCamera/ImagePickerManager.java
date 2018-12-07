package com.influx.marcus.theatres.customCamera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Mickael on 10/10/2016.
 */

public class ImagePickerManager extends PickerManager {

    public ImagePickerManager(Activity activity) {
        super(activity);
    }

    protected void sendToExternalApp( ){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");

        activity.startActivityForResult(Intent.createChooser(intent, "Select avatar..."),
                REQUEST_CODE_SELECT_IMAGE);
    }

    @Override
    public void setUri(Uri uri)
    {
        mProcessingPhotoUri = uri;
    }

}
