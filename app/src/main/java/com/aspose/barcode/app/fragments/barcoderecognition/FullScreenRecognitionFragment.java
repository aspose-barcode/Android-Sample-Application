package com.aspose.barcode.app.fragments.barcoderecognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.os.Environment;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.aspose.barcode.app.R;
import com.aspose.barcode.app.backgroundprocess.OnPhotoAvailableListener;
import com.aspose.barcode.app.backgroundprocess.ProcessWaitingDialog;
import com.aspose.barcode.barcoderecognition.BaseDecodeType;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.barcoderecognition.QualitySettings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FullScreenRecognitionFragment extends Fragment
{
    private static final String TEMP_IMAGE_FILE_PATH = File.separator + "BarcodeExample" + File.separator;

    private RecognitionPreferences recognitionPreferences;

    //Controls
    private AppCompatImageButton recognizeButton;
    private AppCompatImageButton recognitionPreferencesButton;
    private AppCompatImageButton switchCameraButton;
    private CheckBox enableRecognizeContactModeButton;

    // Camera
    private RectangleView recognitionFocusView;
    private TextureView cameraView;
    private CameraService cameraService;

    private OnPhotoAvailableListener onPhotoAvailableListener;

    //ViewModel
    private RecognitionViewModel model;

    public FullScreenRecognitionFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this).get(RecognitionViewModel.class);
        LiveData<RecognitionPreferences> data = model.getData();
        if (data.getValue() != null)
        {
            this.recognitionPreferences = data.getValue();
        }
        data.observe(this, recognitionPreferences -> this.recognitionPreferences = recognitionPreferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_screen_recognition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        try
        {
            initControls(view);
            if (recognitionPreferences == null)
            {

                recognitionPreferences = new RecognitionPreferences();
                recognitionPreferences.setDecodeType(DecodeType.ALL_SUPPORTED_TYPES);
                recognitionPreferences.setQualitySettings(QualitySettings.getHighPerformance());
                recognitionPreferences.setResolution(getLowestResolution(cameraService.getCameraResolutions()));

                model.setRecognitionPreferences(recognitionPreferences);
            }
            loadLayout();
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    private void initControls(@NonNull View view)
    {
        //Buttons
        recognizeButton = view.findViewById(R.id.recognize_image_button);
        recognitionPreferencesButton = view.findViewById(R.id.recognition_preferences_image_button2);
        switchCameraButton = view.findViewById(R.id.switch_camera_image_button);
        enableRecognizeContactModeButton = view.findViewById(R.id.enable_recognize_contact_mode_checkBox);

        // Camera view
        cameraView = view.findViewById(R.id.camera_view);
        cameraService = new CameraService(cameraView);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive())
        {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
            {
                @Override
                public void onGlobalLayout()
                {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewWidth = view.getWidth();
                    int viewHeight = view.getHeight();
                    recognitionFocusView = new RectangleView(view.getContext(), viewWidth, viewHeight);
                    ((FrameLayout) view).addView(recognitionFocusView);
                    recognitionFocusView.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void loadLayout()
    {
        cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener()
        {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
            {
                cameraService.initCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
            {
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
            {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface)
            {
            }
        });

        recognizeButton.setOnClickListener(v -> cameraService.makePhoto(recognitionPreferences.getResolution(), onPhotoAvailableListener));
        switchCameraButton.setOnClickListener(v -> cameraService.switchCamera());

        recognitionPreferencesButton.setOnClickListener(v ->
        {
            LayoutInflater inflater = getLayoutInflater();
            RecognitionPreferencesDialog recognitionPreferencesDialog = new RecognitionPreferencesDialog(v.getContext(), inflater, recognitionPreferences)
            {
                @Override
                public BaseDecodeType[] getSupportedDecodeTypes()
                {
                    List<BaseDecodeType> decodeTypes = new ArrayList<>(Arrays.asList(DecodeType.getAllSupportedTypesArray()));
                    decodeTypes.add(DecodeType.ALL_SUPPORTED_TYPES);
                    return decodeTypes.toArray(new BaseDecodeType[0]);
                }

                @Override
                public AvailableQualitySettings[] getSupportedQualitySettings()
                {
                    return AvailableQualitySettings.values();
                }

                @Override
                public Size[] getSupportedResolutions()
                {
                    try
                    {
                        return cameraService.getCameraResolutions();
                    }
                    catch (CameraAccessException e)
                    {
                        e.printStackTrace();
                    }
                    return null;
                }
            };
            recognitionPreferencesDialog.create().show();
        });

        enableRecognizeContactModeButton.setOnCheckedChangeListener((buttonView, isChecked) ->
        {
            recognitionPreferences.setRecognitionTypeMode(isChecked ? RecognitionTypeMode.CONTACT_RECOGNITION_MODE : RecognitionTypeMode.CUSTOM_RECOGNITION_MODE);
            loadLayout();
        });

        switch (recognitionPreferences.getRecognitionTypeMode())
        {
            case CONTACT_RECOGNITION_MODE:
                loadContactRecognitionLayout();
                break;
            case CUSTOM_RECOGNITION_MODE:
                loadCustomRecognitionLayout();
            default:
        }
    }

    private void loadContactRecognitionLayout()
    {
        onPhotoAvailableListener = new PhotoAvailableListener()
        {
            @Override
            protected BarcodeRecognizer getBarcodeRecognizer(@NonNull Bitmap photoImage, @NonNull Context context)
            {
                Bitmap croppedImage = Bitmap.createBitmap(photoImage, (int) (photoImage.getWidth() * 0.1), (int) (photoImage.getHeight() * 0.25), (int) (photoImage.getWidth() * 0.8), (int) (photoImage.getHeight() * 0.5));
                return new ContactRecognizer(context, croppedImage);
            }
        };

        if (recognitionFocusView != null)
        {
            recognitionFocusView.setVisibility(View.VISIBLE);
        }
        recognitionPreferencesButton.setEnabled(false);
    }


    private void loadCustomRecognitionLayout()
    {
        onPhotoAvailableListener = new PhotoAvailableListener()
        {
            @Override
            protected BarcodeRecognizer getBarcodeRecognizer(@NonNull Bitmap photoImage, @NonNull Context context)
            {
                if (recognitionPreferences.isSaveToFile())
                {
                    saveImageToFile(photoImage);
                }

                BaseDecodeType decodeType = recognitionPreferences.getDecodeType();
                QualitySettings qualitySettings = recognitionPreferences.getQualitySettings();

                return new BarcodeRecognizer(context, photoImage, decodeType, qualitySettings);
            }
        };

        if (recognitionFocusView != null)
        {
            recognitionFocusView.setVisibility(View.INVISIBLE);
        }
        recognitionPreferencesButton.setEnabled(true);
    }

    private static Size getLowestResolution(Size[] resolutions)
    {
        Size lowestResolution = resolutions[0];

        for (Size resolution : resolutions)
        {
            if (resolution.getWidth() * resolution.getHeight() < lowestResolution.getWidth() * lowestResolution.getHeight())
            {
                lowestResolution = resolution;
            }
        }

        return lowestResolution;
    }

    private void saveImageToFile(Bitmap image)
    {
        try
        {
            File tempImageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + TEMP_IMAGE_FILE_PATH);
            if (!tempImageDirectory.exists())
            {
                tempImageDirectory.mkdir();
            }
            String filePath = tempImageDirectory.getAbsolutePath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg";
            OutputStream output = new FileOutputStream(filePath);
            image.compress(Bitmap.CompressFormat.JPEG, 100, output);
            Toast.makeText(getContext(), "Image saved to: " + filePath, Toast.LENGTH_SHORT).show();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Toast.makeText(getContext(), "Image saving failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private abstract class PhotoAvailableListener implements OnPhotoAvailableListener
    {
        protected abstract BarcodeRecognizer getBarcodeRecognizer(@NonNull Bitmap photoImage, @NonNull Context context);

        @Override
        public final void invoke(@NonNull Bitmap photoImage, @NonNull Context context)
        {
            ProcessWaitingDialog<BarcodeRecognizer> recognizeWaitingDialog = new ProcessWaitingDialog<>(context, getBarcodeRecognizer(photoImage, context), "Recognition");
            recognizeWaitingDialog.setOnDismissListener(dialog -> cameraService.initCamera());
            recognizeWaitingDialog.show();
        }
    }
}