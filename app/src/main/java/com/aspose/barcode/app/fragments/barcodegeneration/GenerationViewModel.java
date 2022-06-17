package com.aspose.barcode.app.fragments.barcodegeneration;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aspose.barcode.app.fragments.barcoderecognition.RecognitionPreferences;

public class GenerationViewModel extends ViewModel
{
    private MutableLiveData<GenerationPreferences> liveData;

    public GenerationViewModel()
    {
        liveData = new MutableLiveData<>();
    }

    public void setGenerationPreferences(GenerationPreferences preferences)
    {
        liveData.setValue(preferences);
    }

    public LiveData<GenerationPreferences> getData()
    {
        return liveData;
    }
}
