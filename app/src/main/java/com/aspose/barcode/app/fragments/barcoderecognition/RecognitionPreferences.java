package com.aspose.barcode.app.fragments.barcoderecognition;

import android.util.Size;

import com.aspose.barcode.barcoderecognition.BaseDecodeType;
import com.aspose.barcode.barcoderecognition.QualitySettings;

public class RecognitionPreferences
{
    private RecognitionTypeMode recognitionType = RecognitionTypeMode.CUSTOM_RECOGNITION_MODE;
    private boolean saveToFile = true;
    private Size resolution;
    private BaseDecodeType decodeType;
    private QualitySettings qualitySettings;

    public Size getResolution() {
        return resolution;
    }

    public void setResolution(Size resolution) {
        this.resolution = resolution;
    }

    public BaseDecodeType getDecodeType() {
        return decodeType;
    }

    public void setDecodeType(BaseDecodeType decodeType) {
        this.decodeType = decodeType;
    }

    public QualitySettings getQualitySettings() {
        return qualitySettings;
    }

    public void setQualitySettings(QualitySettings qualitySettings) {
        this.qualitySettings = qualitySettings;
    }

    public RecognitionTypeMode getRecognitionTypeMode() {
        return recognitionType;
    }

    public void setRecognitionTypeMode(RecognitionTypeMode recognitionType) { this.recognitionType = recognitionType; }

    public boolean isSaveToFile() {
        return saveToFile;
    }

    public void setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
    }
}
