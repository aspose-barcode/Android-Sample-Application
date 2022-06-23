package com.aspose.barcode.app.fragments.barcoderecognition;

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
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.aspose.barcode.app.activities.MainActivity;
import com.aspose.barcode.app.backgroundprocess.OnPhotoAvailableListener;

import java.nio.ByteBuffer;
import java.util.Collections;

public class CameraService
{
    private TextureView cameraView;
    private CameraDevice mCameraDevice = null;
    private int cameraId = 0;
    private OnPhotoAvailableListener onPhotoAvailableListener;

    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener = reader ->
    {
        Context context = cameraView.getContext();
        if (context == null)
        {
            throw new NullPointerException();
        }
        Image image = reader.acquireLatestImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        Bitmap barcodeImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
        if (onPhotoAvailableListener != null)
        {
            onPhotoAvailableListener.invoke(barcodeImage, context);
        }
    };

    public CameraService(@NonNull TextureView cameraView)
    {
        this.cameraView = cameraView;
    }

    private void setOnPhotoAvailableListener(OnPhotoAvailableListener onPhotoAvailableListener)
    {
        this.onPhotoAvailableListener = onPhotoAvailableListener;
    }

    public void makePhoto(Size resolution, OnPhotoAvailableListener listener)
    {
        setOnPhotoAvailableListener(listener);
        try
        {
            ImageReader imageReader = ImageReader.newInstance(resolution.getWidth(), resolution.getHeight(), ImageFormat.JPEG, 1);
            imageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);

            final CaptureRequest.Builder captureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90);//TODO
            captureBuilder.addTarget(imageReader.getSurface());

            mCameraDevice.createCaptureSession(Collections.singletonList(imageReader.getSurface()), new CameraCaptureSession.StateCallback()
            {
                @Override
                public void onConfigured(CameraCaptureSession session)
                {
                    try
                    {
                        session.stopRepeating();
                        session.abortCaptures();
                        session.capture(captureBuilder.build(), null, null);
                    }
                    catch (CameraAccessException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session)
                {
                }
            }, null);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    public int switchCamera()
    {
        cameraId = cameraId == 0 ? 1 : 0;
        mCameraDevice.close();
        initCamera();

        return cameraId;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initCamera()
    {
        if (ActivityCompat.checkSelfPermission(cameraView.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            AlertDialog.Builder messageAlertDialog = new AlertDialog.Builder(cameraView.getContext());
            messageAlertDialog.setMessage("Camera permission not granted!");
            messageAlertDialog.setPositiveButton("OK", (dialog, which) ->
            {
                Intent intent = new Intent(cameraView.getContext(), MainActivity.class);
                cameraView.getContext().startActivity(intent);
            });
            messageAlertDialog.show();
        } else
        {
            try
            {
                CameraManager cameraManager = ((CameraManager) cameraView.getContext().getSystemService(Context.CAMERA_SERVICE));
                cameraManager.openCamera(cameraManager.getCameraIdList()[cameraId], mCameraCallback, null);
            }
            catch (CameraAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback()
    {
        @Override
        public void onOpened(CameraDevice camera)
        {
            mCameraDevice = camera;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera)
        {
            mCameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error)
        {
        }
    };

    private void createCameraPreviewSession()
    {
        SurfaceTexture texture = cameraView.getSurfaceTexture();
        Surface surface = new Surface(texture);

        try
        {
            final CaptureRequest.Builder builder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.set(CaptureRequest.CONTROL_EFFECT_MODE, CaptureRequest.CONTROL_EFFECT_MODE_SEPIA);

            builder.addTarget(surface);

            mCameraDevice.createCaptureSession(Collections.singletonList(surface),
                    new CameraCaptureSession.StateCallback()
                    {

                        @Override
                        public void onConfigured(CameraCaptureSession session)
                        {
                            try
                            {
                                session.setRepeatingRequest(builder.build(), null, null);
                            }
                            catch (CameraAccessException e)
                            {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession session)
                        {
                        }
                    }, null);
        }
        catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }

    public Size[] getCameraResolutions() throws CameraAccessException
    {
        CameraManager cameraManager = ((CameraManager) cameraView.getContext().getSystemService(Context.CAMERA_SERVICE));
        StreamConfigurationMap configurationMap = cameraManager.getCameraCharacteristics(String.valueOf(cameraId)).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        return configurationMap.getOutputSizes(ImageFormat.JPEG);
    }
}