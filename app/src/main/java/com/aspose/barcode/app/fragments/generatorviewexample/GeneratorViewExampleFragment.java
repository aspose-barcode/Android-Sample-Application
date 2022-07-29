package com.aspose.barcode.app.fragments.generatorviewexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aspose.barcode.app.R;
import com.aspose.barcode.component.BarcodeGeneratorView;

public class GeneratorViewExampleFragment extends Fragment
{
    private BarcodeGeneratorView barCodeGeneratorView;

    private ExampleFragmentViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    private void initControls(@NonNull View view)
    {
        barCodeGeneratorView = view.findViewById(R.id.barcodeGeneratorView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_generator_view_example, container, false);
    }
}
