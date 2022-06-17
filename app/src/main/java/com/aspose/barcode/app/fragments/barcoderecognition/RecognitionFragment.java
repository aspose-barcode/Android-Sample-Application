package com.aspose.barcode.app.fragments.barcoderecognition;

import static com.aspose.barcode.app.contact.ContactsHelper.readContacts;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.aspose.barcode.app.R;
import com.aspose.barcode.app.activities.MainActivity;
import com.aspose.barcode.app.backgroundprocess.BackgroundProcess;
import com.aspose.barcode.app.backgroundprocess.OnPhotoAvailableListener;
import com.aspose.barcode.app.backgroundprocess.ProcessWaitingDialog;
import com.aspose.barcode.app.contact.Contact;
import com.aspose.barcode.app.fragments.barcodegeneration.GenerationFragment;
import com.aspose.barcode.barcoderecognition.BaseDecodeType;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.barcoderecognition.QualitySettings;
import com.aspose.barcode.barcoderecognition.SingleDecodeType;
import com.aspose.barcode.generation.BaseEncodeType;
import com.aspose.barcode.generation.EncodeTypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
@Deprecated
public class RecognitionFragment extends Fragment
{
//        private ActivityBarcodeRecognitionBinding recognitionBinding;
    private RecognitionViewModel recognitionViewModel;
    private static final String TAG = "###RecognitionFragment";

    //Controls
    Button recognizeButton;
    Button switchCameraButton;
//    Button flashlightButton;

    Spinner decodeTypeSpinner;
    TextView decodeTypeSpinnerLabel;
    Spinner cameraResolutionSpinner;
    TextView cameraResolutionSpinnerLabel;
    Spinner recognitionQualitySpinner;
    TextView recognitionQualitySpinnerLabel;

    CheckBox saveToFileCheckBox;
    CheckBox importContactCheckBox;

    // Camera
    private TextureView cameraView;
    private CameraService cameraService;

    private OnPhotoAvailableListener onPhotoAvailableListener = new OnPhotoAvailableListener() {
        @Override
        public void invoke(@NonNull Bitmap photoImage, @NonNull Context context) {
            BarcodeRecognizer barcodeRecognizer;
            if (!importContactCheckBox.isChecked())
            {
                if (saveToFileCheckBox.isChecked())
                    saveImageToFile(photoImage);

                BaseDecodeType decodeType = getSpinnerDecodeType();
                QualitySettings qualitySettings = getSpinnerQuality();

                barcodeRecognizer = new BarcodeRecognizer(context, photoImage, decodeType, qualitySettings);
            }
            else
            {
                barcodeRecognizer = new ContactRecognizer(context, photoImage);
            }

            ProcessWaitingDialog<BarcodeRecognizer> recognizeWaitingDialog = new ProcessWaitingDialog<>(context, barcodeRecognizer, "Recognition");
            recognizeWaitingDialog.setOnDismissListener(dialog -> cameraService.initCamera());
            recognizeWaitingDialog.show();
        }
    };

    private static final String TEMP_IMAGE_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "BarcodeExample";
    private static final Map<String, QualitySettings> QUALITY_SETTINGS = new HashMap<String, QualitySettings>();

    public RecognitionFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        return inflater.inflate(R.layout.fragment_recognition, container, false);
    }

    static
    {
        QUALITY_SETTINGS.put("HighPerformance", QualitySettings.getHighPerformance());
        QUALITY_SETTINGS.put("NormalQuality", QualitySettings.getNormalQuality());
        QUALITY_SETTINGS.put("HighQuality", QualitySettings.getHighQuality());
        QUALITY_SETTINGS.put("MaxBarCodes", QualitySettings.getMaxBarCodes());
        QUALITY_SETTINGS.put("HighQualityDetection", QualitySettings.getHighQualityDetection());
        QUALITY_SETTINGS.put("MaxQualityDetection", QualitySettings.getMaxQualityDetection());
    }

    private void saveImageToFile(Bitmap image)
    {
        try
        {
            String filePath = TEMP_IMAGE_FILE_PATH + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
            OutputStream output = new FileOutputStream(filePath);
            image.compress(Bitmap.CompressFormat.JPEG, 100, output);
            Toast.makeText(getContext(), "Image saved to: " + filePath, Toast.LENGTH_SHORT).show();
        } catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(), "Image saving failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private QualitySettings getSpinnerQuality()
    {
        int itemPosition = recognitionQualitySpinner.getSelectedItemPosition();
        return QUALITY_SETTINGS.get(QUALITY_SETTINGS.keySet().toArray(new String[0])[itemPosition]);
    }

    private BaseDecodeType getSpinnerDecodeType()
    {
        BaseDecodeType decodeType = DecodeType.ALL_SUPPORTED_TYPES;
        int itemPosition = decodeTypeSpinner.getSelectedItemPosition();
        if (itemPosition < DecodeType.getAllSupportedTypesArray().length)
        {
            decodeType = DecodeType.getAllSupportedTypesArray()[itemPosition];
        }
        return decodeType;
    }

    private void initControls(@NonNull View view)
    {

        //Buttons
        recognizeButton = view.findViewById(R.id.recognizeButton);
        switchCameraButton = view.findViewById(R.id.switchCameraButton);
//        flashlightButton = view.findViewById(R.id.flashlightButton);

        // Camera view
        cameraView = view.findViewById(R.id.cameraView);

        //Labels
        decodeTypeSpinnerLabel = view.findViewById(R.id.decodeTypeSpinnerLabel);
        cameraResolutionSpinnerLabel = view.findViewById(R.id.cameraResolutionSpinnerLabel);
        recognitionQualitySpinnerLabel = view.findViewById(R.id.recognitionQualitySpinnerLabel);

        //Spinners
        decodeTypeSpinner = view.findViewById(R.id.decodeTypeSpinner);
        cameraResolutionSpinner = view.findViewById(R.id.cameraResolutionSpinner);
        recognitionQualitySpinner = view.findViewById(R.id.recognitionQualitySpinner);

        //Checkbox
        saveToFileCheckBox = view.findViewById(R.id.saveToFileCheckBox);
        importContactCheckBox = view.findViewById(R.id.importContactCheckBox);

//        importContactCheckBox.setOnCheckedChangeListener((buttonView, isChecked)->
//        {
//            Log.d(TAG, "importContactCheckBox.setOnCheckedChangeListener START isCHecked = " + isChecked);
//
//            if (isChecked)
//            {
//                Log.d(TAG, "importContactCheckBox.setOnCheckedChangeListener isChecked");
//                decodeTypeSpinner.setVisibility(View.INVISIBLE);
//                decodeTypeSpinnerLabel.setVisibility(View.INVISIBLE);
//                recognitionQualitySpinner.setVisibility(View.INVISIBLE);
//                recognitionQualitySpinnerLabel.setVisibility(View.INVISIBLE);
//                cameraResolutionSpinner.setVisibility(View.INVISIBLE);
//                cameraResolutionSpinnerLabel.setVisibility(View.INVISIBLE);
//                saveToFileCheckBox.setVisibility(View.INVISIBLE);
//            }
//            else
//            {
//                Log.d(TAG, "importContactCheckBox.setOnCheckedChangeListener !isChecked");
//
//                decodeTypeSpinner.setVisibility(View.VISIBLE);
//                decodeTypeSpinnerLabel.setVisibility(View.VISIBLE);
//                recognitionQualitySpinner.setVisibility(View.VISIBLE);
//                recognitionQualitySpinnerLabel.setVisibility(View.VISIBLE);
//                cameraResolutionSpinner.setVisibility(View.VISIBLE);
//                cameraResolutionSpinnerLabel.setVisibility(View.VISIBLE);
//                saveToFileCheckBox.setVisibility(View.VISIBLE);
//            }
//        });
//
//        importContactCheckBox.setOnClickListener((View view) ->
//        {
//
//            boolean checked = ((CheckBox) view).isChecked();
//            Log.d(TAG, "importContactCheckBox.setOnClickListener START checked = " + checked);
//            if (checked)
//            {
//                Log.d(TAG, "importContactCheckBox.setOnClickListener isChecked");
//                decodeTypeSpinner.setVisibility(View.INVISIBLE);
//                decodeTypeSpinnerLabel.setVisibility(View.INVISIBLE);
//                recognitionQualitySpinner.setVisibility(View.INVISIBLE);
//                recognitionQualitySpinnerLabel.setVisibility(View.INVISIBLE);
//                cameraResolutionSpinner.setVisibility(View.INVISIBLE);
//                cameraResolutionSpinnerLabel.setVisibility(View.INVISIBLE);
//                saveToFileCheckBox.setVisibility(View.INVISIBLE);
//            }
//            else
//            {
//                Log.d(TAG, "importContactCheckBox.setOnClickListener !isChecked");
//
//                decodeTypeSpinner.setVisibility(View.VISIBLE);
//                decodeTypeSpinnerLabel.setVisibility(View.VISIBLE);
//                recognitionQualitySpinner.setVisibility(View.VISIBLE);
//                recognitionQualitySpinnerLabel.setVisibility(View.VISIBLE);
//                cameraResolutionSpinner.setVisibility(View.VISIBLE);
//                cameraResolutionSpinnerLabel.setVisibility(View.VISIBLE);
//                saveToFileCheckBox.setVisibility(View.VISIBLE);
//            }
//        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        loadLayout(view);
    }

    private enum RecognitionLayoutType
    {
        CONTACT_RECOGNITION, CUSTOM_RECOGNITION
    }

    private void loadLayout(@NonNull View view)
    {
        loadLayout(view, RecognitionLayoutType.CUSTOM_RECOGNITION);
    }

    private void loadLayout(@NonNull View view, RecognitionLayoutType recognitionLayoutType)
    {
        initControls(view);
        cameraService = new CameraService(cameraView);

        recognizeButton.setOnClickListener(v ->
        {
            try
            {
                cameraService.makePhoto(cameraService.getCameraResolutions()[cameraResolutionSpinner.getSelectedItemPosition()], onPhotoAvailableListener);
            } catch (CameraAccessException e)
            {
                e.printStackTrace();
            }
        });

        cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
        {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
            {
                cameraService.initCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height){}

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface){return false;}

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface){}
        });

        switchCameraButton.setOnClickListener(v ->
        {
            cameraService.switchCamera();
            fillCameraResolutionSpinner();
        });

        importContactCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> loadLayout(view, isChecked ? RecognitionLayoutType.CONTACT_RECOGNITION : RecognitionLayoutType.CUSTOM_RECOGNITION));
        switch (recognitionLayoutType)
        {
            case CONTACT_RECOGNITION:
                initContactRecognitionLayout(view);
                break;
            case CUSTOM_RECOGNITION:
            default:
                initCustomRecognitionLayout(view);
        }
    }

    private void initCustomRecognitionLayout(@NonNull View view)
    {
        decodeTypeSpinner.setVisibility(View.VISIBLE);
        decodeTypeSpinnerLabel.setVisibility(View.VISIBLE);
        recognitionQualitySpinner.setVisibility(View.VISIBLE);
        recognitionQualitySpinnerLabel.setVisibility(View.VISIBLE);
        cameraResolutionSpinner.setVisibility(View.VISIBLE);
        cameraResolutionSpinnerLabel.setVisibility(View.VISIBLE);
        saveToFileCheckBox.setVisibility(View.VISIBLE);

        List<String> spinnerArray = new ArrayList<>();
        for (SingleDecodeType decodeType : DecodeType.getAllSupportedTypesArray())
        {
            spinnerArray.add(decodeType.getTypeName());
        }
        spinnerArray.add(DecodeType.ALL_SUPPORTED_TYPES.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        decodeTypeSpinner.setAdapter(adapter);
        decodeTypeSpinner.setSelection(spinnerArray.size() - 1);

        spinnerArray = new ArrayList<>(QUALITY_SETTINGS.keySet());
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        recognitionQualitySpinner.setAdapter(adapter);
        recognitionQualitySpinner.setSelection(1);

        fillCameraResolutionSpinner();
    }

    private void initContactRecognitionLayout(@NonNull View view)
    {
        decodeTypeSpinner.setVisibility(View.INVISIBLE);
        decodeTypeSpinnerLabel.setVisibility(View.INVISIBLE);
        recognitionQualitySpinner.setVisibility(View.INVISIBLE);
        recognitionQualitySpinnerLabel.setVisibility(View.INVISIBLE);
        cameraResolutionSpinner.setVisibility(View.INVISIBLE);
        cameraResolutionSpinnerLabel.setVisibility(View.INVISIBLE);
        saveToFileCheckBox.setVisibility(View.INVISIBLE);
    }

    private void fillCameraResolutionSpinner()
    {
        try
        {
            List<String> spinnerArray = new ArrayList<>();
            for (Size size : cameraService.getCameraResolutions())
            {
                spinnerArray.add(size.getWidth() + "x" + size.getHeight());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            cameraResolutionSpinner.setAdapter(adapter);
            cameraResolutionSpinner.setSelection(spinnerArray.size() - 1);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public void onImportContactCheckboxChecked()
    {
        Log.d(TAG, "importContactCheckBox.setOnCheckedChangeListener isChecked");
        decodeTypeSpinner.setVisibility(View.INVISIBLE);
        decodeTypeSpinnerLabel.setVisibility(View.INVISIBLE);
        recognitionQualitySpinner.setVisibility(View.INVISIBLE);
        recognitionQualitySpinnerLabel.setVisibility(View.INVISIBLE);
        cameraResolutionSpinner.setVisibility(View.INVISIBLE);
        cameraResolutionSpinnerLabel.setVisibility(View.INVISIBLE);
        saveToFileCheckBox.setVisibility(View.INVISIBLE);
    }

    public void onImportContactCheckboxUnchecked()
    {
        Log.d(TAG, "importContactCheckBox.setOnCheckedChangeListener !isChecked");
        decodeTypeSpinner.setVisibility(View.VISIBLE);
        decodeTypeSpinnerLabel.setVisibility(View.VISIBLE);
        recognitionQualitySpinner.setVisibility(View.VISIBLE);
        recognitionQualitySpinnerLabel.setVisibility(View.VISIBLE);
        cameraResolutionSpinner.setVisibility(View.VISIBLE);
        cameraResolutionSpinnerLabel.setVisibility(View.VISIBLE);
        saveToFileCheckBox.setVisibility(View.VISIBLE);
    }

}