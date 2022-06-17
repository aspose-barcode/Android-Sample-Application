package com.aspose.barcode.app.fragments.barcodegeneration;

import static com.aspose.barcode.app.contact.ContactsHelper.prepareContact;
import static com.aspose.barcode.app.contact.ContactsHelper.readContacts;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

//import com.aspose.barcode.component.BarCodeGeneratorView;
//import com.aspose.barcode.generation.BaseEncodeType;
//import com.aspose.barcode.generation.EncodeTypes;
//import com.example.cameraexample.MainActivity;
//import com.example.cameraexample.R;
//import com.example.cameraexample.contact.Contact;
//import com.example.cameraexample.contact.ContactSerializer;
//import com.example.cameraexample.contact.EditContactDialog;
//import com.example.cameraexample.contact.members.Member;
//import com.example.cameraexample.contact.members.MemberType;

import com.aspose.barcode.app.R;
import com.aspose.barcode.app.activities.MainActivity;
import com.aspose.barcode.app.backgroundprocess.BackgroundProcess;
import com.aspose.barcode.app.backgroundprocess.ProcessWaitingDialog;
import com.aspose.barcode.app.contact.Contact;
import com.aspose.barcode.app.contact.ContactSerializer;
import com.aspose.barcode.app.contact.EditContactDialog;
import com.aspose.barcode.app.fragments.barcoderecognition.RecognitionPreferences;
import com.aspose.barcode.app.fragments.barcoderecognition.RecognitionViewModel;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.barcoderecognition.QualitySettings;
import com.aspose.barcode.component.BarcodeGeneratorView;
import com.aspose.barcode.generation.BaseEncodeType;
import com.aspose.barcode.generation.EncodeTypes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenerationFragment extends Fragment
{
    //Controls
    private Spinner encodeTypeSpinner;
    private EditText barcodeEditText;
    private BarcodeGeneratorView barCodeGeneratorView;
    private Button generateButton;
    private Button exportContactButton;

    private GenerationPreferences generationPreferences;


    private GenerationViewModel model;

    private void initControls(@NonNull View view)
    {
        encodeTypeSpinner = view.findViewById(R.id.encodeTypeSpinner);
        barcodeEditText = view.findViewById(R.id.barcodeEditText);

        generateButton = view.findViewById(R.id.generateButton);
        exportContactButton = view.findViewById(R.id.export_contact_button);

        barCodeGeneratorView = view.findViewById(R.id.barcodeGeneratorView);
    }

    public GenerationFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this).get(GenerationViewModel.class);
        LiveData<GenerationPreferences> data = model.getData();
        if(data.getValue() != null)
            this.generationPreferences = data.getValue();
        data.observe(this, generationPreferences -> this.generationPreferences = generationPreferences);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_generation, container, false);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            alertContactPermissions(view);
            return;
        }

        loadLayout(view);
    }

    private void loadLayout(@NonNull View view)
    {
        initControls(view);
        if(generationPreferences == null) {

            generationPreferences = new GenerationPreferences();
            generationPreferences.setEncodeType(EncodeTypes.CODE_39_STANDARD);
            generationPreferences.setCodeText("0123456789");

            model.setGenerationPreferences(generationPreferences);
        }

        generateButton.setOnClickListener(v -> generateBarcodeForEnteredData());
        exportContactButton.setOnClickListener(v -> runGenerationContactBarcodeDialog(view.getContext()));
        barcodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {generationPreferences.setCodeText(s.toString());}

            @Override
            public void afterTextChanged(Editable s) { }
        });
        encodeTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                generationPreferences.setEncodeType(EncodeTypes.getAllEncodeTypes()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}

        });
        initLayout(view);
    }

    private void initLayout(@NonNull View view)
    {
        if(generationPreferences.getCodeText() != null && generationPreferences.getCodeText().isEmpty())
            barcodeEditText.setText(generationPreferences.getCodeText());

        List<String> encodeTypesSpinnerArray = new ArrayList<String>();
        for (BaseEncodeType encodeType : EncodeTypes.getAllEncodeTypes())
            encodeTypesSpinnerArray.add(encodeType.getTypeName());

        ArrayAdapter<String> encodeTypesAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, encodeTypesSpinnerArray);
        encodeTypesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        encodeTypeSpinner.setAdapter(encodeTypesAdapter);

        int chosenEncodeType = EncodeTypes.CODE_39_STANDARD.getTypeIndex();
        if(generationPreferences.getEncodeType() != null)
            for(int i = 0; i < EncodeTypes.getAllEncodeTypes().length; i++)
                if(generationPreferences.getEncodeType().equals(EncodeTypes.getAllEncodeTypes()[i]))
                {
                    chosenEncodeType = i;
                    break;
                }
        encodeTypeSpinner.setSelection(chosenEncodeType);
        generateBarcodeForEnteredData();
    }

    private void runGenerationContactBarcodeDialog(@NonNull Context context)
    {
        LayoutInflater inflater = getLayoutInflater();
        View chooseContactLayout = inflater.inflate(R.layout.choose_contact_dialog_layout, null);

        AlertDialog.Builder editContactDialog = new AlertDialog.Builder(context);
        editContactDialog.setView(chooseContactLayout);

        Spinner contactsSpinner = chooseContactLayout.findViewById(R.id.contacts_spinner);
        editContactDialog.setPositiveButton("OK", (dialog, which) ->
        {
            int itemPosition = contactsSpinner.getSelectedItemPosition();
            Contact exportContact = generationPreferences.getContacts().get(itemPosition);
            generateBarcodeForContact(exportContact);
        });

        BackgroundProcess loadContactsProcess = new BackgroundProcess()
        {
            @Override
            protected void runProcess()
            {
                if(generationPreferences.getContacts() == null)
                    generationPreferences.setContacts(readContacts(context));
            }

            @Override
            public void processBackgroundResults()
            {
                ArrayAdapter<Contact> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, generationPreferences.getContacts());
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                contactsSpinner.setAdapter(adapter);
                contactsSpinner.setSelection(0);
                editContactDialog.create().show();
            }
        };
        ProcessWaitingDialog<BackgroundProcess> dialog = new ProcessWaitingDialog<>(context, loadContactsProcess, "Load contacts");
        dialog.show();
    }

    private void setBarCodeGeneratorViewData(String barcodeCodeText, String displayCodeText, BaseEncodeType encodeType)
    {
        barCodeGeneratorView.setCodeText(barcodeCodeText);
        barCodeGeneratorView.getCodeTextParameters().setTwoDDisplayText(displayCodeText);
        barCodeGeneratorView.setBarcodeType(encodeType);
        barCodeGeneratorView.setVisibility(View.VISIBLE);
    }

    private void generateBarcodeForEnteredData()
    {
        generationPreferences.setEncodeType(EncodeTypes.getAllEncodeTypes()[encodeTypeSpinner.getSelectedItemPosition()]);
        generationPreferences.setCodeText(barcodeEditText.getText().toString());
        setBarCodeGeneratorViewData(generationPreferences.getCodeText(), generationPreferences.getCodeText(), generationPreferences.getEncodeType());
    }

    private void generateBarcodeForContact(Contact exportContact)
    {
        EditContactDialog editContactDialog = new EditContactDialog(getContext(), exportContact);

        editContactDialog.setAfterButtonClickListener(pressedButton ->
        {
            switch (pressedButton)
            {
                case Ok:
                    setBarCodeGeneratorViewData(ContactSerializer.createXMLString(exportContact), prepareContact(exportContact), EncodeTypes.QR);
                case Cancel:
                default:
                    break;
            }
        });
        editContactDialog.show();
    }

    private void alertContactPermissions(View view)
    {
        AlertDialog.Builder messageAlertDialog = new AlertDialog.Builder(view.getContext());
        messageAlertDialog.setMessage("Read/Write contacts permission not granted!");
        messageAlertDialog.setPositiveButton("OK", (dialog, which) ->
        {
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            view.getContext().startActivity(intent);
        });
        messageAlertDialog.show();
    }


}