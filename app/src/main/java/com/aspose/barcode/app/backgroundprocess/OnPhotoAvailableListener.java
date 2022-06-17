package com.aspose.barcode.app.backgroundprocess;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import androidx.annotation.NonNull;

public interface OnPhotoAvailableListener
{
    void invoke(@NonNull Bitmap photoImage, @NonNull Context context);
}
