package com.aspose.barcode.app.fragments.barcoderecognition;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RecognitionViewModel extends ViewModel
{
    private MutableLiveData<RecognitionPreferences> liveData;


    public RecognitionViewModel()
    {
        liveData = new MutableLiveData<>();
    }

    public void setRecognitionPreferences(RecognitionPreferences preferences)
    {
        liveData.setValue(preferences);
    }

    public LiveData<RecognitionPreferences> getData()
    {
        return liveData;
    }
}
