package com.aspose.barcode.app.fragments.barcoderecognition;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.webkit.URLUtil;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.aspose.barcode.app.backgroundprocess.BackgroundProcess;
import com.aspose.barcode.app.backgroundprocess.ProcessFinishedListener;
import com.aspose.barcode.barcoderecognition.BarCodeReader;
import com.aspose.barcode.barcoderecognition.BarCodeResult;
import com.aspose.barcode.barcoderecognition.BaseDecodeType;
import com.aspose.barcode.barcoderecognition.QualitySettings;

public class BarcodeRecognizer extends BackgroundProcess
{
    private static final String NOT_RECOGNIZED = "Not recognized";
    private static final String RECOGNITION_ABORTED = "Recognition aborted";

    protected final Context context;
    private final BarCodeReader barcodeReader;
    protected BarCodeResult[] results;

    @Override
    public void runProcess() {
        this.results = barcodeReader.readBarCodes();
    }

    public BarcodeRecognizer(Context context, Bitmap barcodeImage, BaseDecodeType decodeType, QualitySettings qualitySettings)
    {
        this.context = context;
        barcodeReader = new BarCodeReader(barcodeImage, decodeType);
        barcodeReader.setQualitySettings(qualitySettings);
    }

    public void processBackgroundResults()
    {
        String recognitionResultsMessage = RECOGNITION_ABORTED;
        if(results != null)
        {
            recognitionResultsMessage = NOT_RECOGNIZED;
            if (results.length > 0 && results[0] != null)
            {
                recognitionResultsMessage = "";
                for (int i = 1; i <= results.length; i++) {
                    recognitionResultsMessage += i + ". " + results[i - 1].getCodeText() + ";\n";
                }
            }
        }
        showMessageDialog(recognitionResultsMessage);
    }

    protected void showMessageDialog(String message)
    {
        AlertDialog.Builder messageAlertDialog = new AlertDialog.Builder(context);

        SpannableString s = new SpannableString(message);
        Linkify.addLinks(s, Linkify.ALL);
        messageAlertDialog.setMessage(s);

        if(!(message.equals(NOT_RECOGNIZED) || message.equals(RECOGNITION_ABORTED)))
        {
            messageAlertDialog.setPositiveButton("Copy to clipboard", (dialog, which) ->
            {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("BARCODE", message);
                clipboard.setPrimaryClip(clip);
            });
        }
        messageAlertDialog.setNegativeButton("Cancel", (dialog, which) -> {});
        AlertDialog dialog = messageAlertDialog.create();
        dialog.show();
        ((TextView)dialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
    }
}