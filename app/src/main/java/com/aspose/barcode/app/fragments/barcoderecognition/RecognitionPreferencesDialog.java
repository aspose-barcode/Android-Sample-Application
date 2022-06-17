package com.aspose.barcode.app.fragments.barcoderecognition;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.aspose.barcode.app.R;
import com.aspose.barcode.barcoderecognition.BaseDecodeType;
import com.aspose.barcode.barcoderecognition.QualitySettings;

import java.util.ArrayList;
import java.util.List;

public abstract class RecognitionPreferencesDialog extends AlertDialog.Builder
{
    private final static int MAX_RECOMMENDED_SIZE = 972800;

    private final RecognitionPreferences preferences;
    private final View layout;

    public RecognitionPreferencesDialog(Context context, LayoutInflater inflater, RecognitionPreferences preferences)
    {
        super(context);
        this.preferences = preferences;
        this.layout = inflater.inflate(R.layout.recognition_preferences_layout, null);
        inflateDialogLayout();
        setPositiveButton("OK", (dialog, which) -> updateRecognitionPreferences());
        setNegativeButton("Cancel", (dialog, which) -> {});
    }

    private void inflateDialogLayout()
    {
        initSupportedResolutionsSpinner(layout.findViewById(R.id.resolution_spinner), preferences.getResolution());
        initSupportedQualitySettingsSpinner(layout.findViewById(R.id.quality_settings_spinner), preferences.getQualitySettings());
        initSupportedDecodeTypesSpinner(layout.findViewById(R.id.decode_type_spinner), preferences.getDecodeType());


        setView(layout);
    }

    private void updateRecognitionPreferences()
    {
        preferences.setResolution(getSupportedResolutions()[((Spinner)(layout.findViewById(R.id.resolution_spinner))).getSelectedItemPosition()]);
        preferences.setQualitySettings(getSupportedQualitySettings()[((Spinner)(layout.findViewById(R.id.quality_settings_spinner))).getSelectedItemPosition()].getQualitySettingsValue());
        preferences.setDecodeType(getSupportedDecodeTypes()[((Spinner)(layout.findViewById(R.id.decode_type_spinner))).getSelectedItemPosition()]);
        preferences.setSaveToFile(((CheckBox)layout.findViewById(R.id.save_image_check_box)).isChecked());
    }

    private void initSupportedResolutionsSpinner(Spinner spinner, Size chosenItem)
    {
        int chosePosition = 0;
        List<String> spinnerArray = new ArrayList<>();
        for(int i = 0; i < getSupportedResolutions().length; i++)
        {
            Size size = getSupportedResolutions()[i];
            String resolutionStr = size.getWidth() + "x" + size.getHeight();
            if(size.getWidth() * size.getHeight() > MAX_RECOMMENDED_SIZE)
                resolutionStr += ("(Not recommended)");
            spinnerArray.add(resolutionStr);
            if(size.equals(chosenItem))
                chosePosition = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(chosePosition);
    }

    private void initSupportedQualitySettingsSpinner(Spinner spinner, QualitySettings chosenQualitySettings)
    {
        int chosePosition = 0;
        List<String> spinnerArray = new ArrayList<>();
        for(int i = 0; i < getSupportedQualitySettings().length; i++)
        {
            AvailableQualitySettings qualitySettings = getSupportedQualitySettings()[i];
            spinnerArray.add(qualitySettings.name());
            if(chosenQualitySettings.equals(qualitySettings.getQualitySettingsValue()))
                chosePosition = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(chosePosition);
    }

    private void initSupportedDecodeTypesSpinner(Spinner spinner, BaseDecodeType chosenDecodeType)
    {
        int chosePosition = 0;
        List<String> spinnerArray = new ArrayList<>();
        for(int i = 0; i < getSupportedDecodeTypes().length; i++)
        {
            BaseDecodeType decodeType = getSupportedDecodeTypes()[i];
            spinnerArray.add(decodeType.toString());
            if(chosenDecodeType.equals(decodeType))
                chosePosition = i;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(chosePosition);
    }

    public abstract BaseDecodeType[] getSupportedDecodeTypes();
    public abstract AvailableQualitySettings[] getSupportedQualitySettings();
    public abstract Size[] getSupportedResolutions();
}
