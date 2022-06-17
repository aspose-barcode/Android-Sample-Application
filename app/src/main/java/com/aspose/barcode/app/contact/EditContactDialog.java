package com.aspose.barcode.app.contact;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.aspose.barcode.app.assist.CommonAssist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditContactDialog extends AlertDialog.Builder
{
    private final Contact editableContact;
    private AfterButtonClickListener listener;

    public EditContactDialog(Context context, Contact editableContact)
    {
        super(context);
        this.editableContact = editableContact;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public AlertDialog show()
    {
        this.setTitle("Edit contact");
        EditText contactNameEditText = new EditText(getContext());
        contactNameEditText.setText(editableContact.getGivenName());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contactNameEditText.setLayoutParams(lp);
        this.setView(contactNameEditText);

        List<String> members = new ArrayList<>();
        editableContact.getMembers().forEach(member -> members.add(member.toString()));


        boolean[] checkedMembers = new boolean[members.size()];
        Arrays.fill(checkedMembers, true);

        this.setMultiChoiceItems(members.toArray(new String[0]), checkedMembers, (dialog, which, isChecked) ->{});

        this.setPositiveButton("OK", (dialog, which) ->
        {
            if (!editableContact.getGivenName().equals(contactNameEditText.getText().toString()))
            {
                editableContact.setGivenName(contactNameEditText.getText().toString());
            }
            for (int i = 0; i < members.size(); i++)
            {
                if (!checkedMembers[i])
                {
                    editableContact.getMembers().remove(i);
                    i--;
                }
            }
            if (listener != null)
            {
                listener.afterButtonClick(PressedButton.Ok);
            }
        });

        this.setNegativeButton("Cancel", (dialog, which) ->
        {
            if (listener != null)
            {
                listener.afterButtonClick(PressedButton.Cancel);
            }
        });

        return super.show();
    }

    public void setAfterButtonClickListener(AfterButtonClickListener listener)
    {
        this.listener = listener;
    }
}

