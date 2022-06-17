package com.aspose.barcode.app.fragments.collaboration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.aspose.barcode.app.R;

public class CollaborationFragment extends Fragment
{
    private EditText view_1;
    private EditText view_2;
    private EditText view_3;
    private EditText view_4;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collaboration, container, false);
    }
}
