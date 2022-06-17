package com.aspose.barcode.app.fragments.barcoderecognition;

import com.aspose.barcode.barcoderecognition.QualitySettings;

public enum AvailableQualitySettings
{
    HighPerformance(QualitySettings.getHighPerformance()),
    NormalQuality(QualitySettings.getNormalQuality()),
    HighQuality(QualitySettings.getHighQuality()),
    MaxBarCodes(QualitySettings.getMaxBarCodes()),
    HighQualityDetection(QualitySettings.getHighQualityDetection()),
    MaxQualityDetection(QualitySettings.getMaxQualityDetection());

    private final QualitySettings qualitySettings;

    private AvailableQualitySettings(QualitySettings qualitySettings)
    {
        this.qualitySettings = qualitySettings;
    }

    public QualitySettings getQualitySettingsValue()
    {
        return qualitySettings;
    }
}
