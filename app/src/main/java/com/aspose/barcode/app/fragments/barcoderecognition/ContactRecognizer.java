package com.aspose.barcode.app.fragments.barcoderecognition;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;


import com.aspose.barcode.app.assist.CommonAssist;
import com.aspose.barcode.app.contact.Contact;
import com.aspose.barcode.app.contact.ContactSerializer;
import com.aspose.barcode.app.contact.EditContactDialog;
import com.aspose.barcode.app.contact.members.Member;
import com.aspose.barcode.barcoderecognition.BarCodeResult;
import com.aspose.barcode.barcoderecognition.DecodeType;
import com.aspose.barcode.barcoderecognition.QualitySettings;

import java.util.ArrayList;

public class ContactRecognizer extends BarcodeRecognizer
{
    public ContactRecognizer(Context context, Bitmap barcodeImage)
    {
        super(context, barcodeImage, DecodeType.QR, QualitySettings.getHighPerformance());
    }

    @Override
    public void processBackgroundResults()
    {
        String message = "Recognition aborted!";
        if (results != null)
        {
            message = "Not recognized!";
            if(results.length > 0)
            {
                Contact newContact = ContactSerializer.parseXmlcontact(results[0].getCodeText());

                EditContactDialog editContactDialog = new EditContactDialog(context, newContact);
                editContactDialog.setAfterButtonClickListener(pressedButton ->
                {
                    switch (pressedButton)
                    {
                        case Ok:
                            if (newContact != null && CommonAssist.isNotEmpty(newContact.getGivenName()))
                            {
                                addContact(newContact);
                            }
                            break;
                        case Cancel:
                        default:
                            break;
                    }
                });
                editContactDialog.show();
                return;
            }
        }
        showMessageDialog(message);
    }

    private void addContact(Contact phoneContact)
    {
        ArrayList<ContentProviderOperation> contact = new ArrayList<ContentProviderOperation>();
        contact.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, phoneContact.getGivenName())
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, phoneContact.getName())
                .build());

        for (Member member : phoneContact.getMembers())
        {
            switch (member.getMemberType())
            {
                case PHONE:
                    contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, member.getValue())
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, Integer.parseInt(member.getMemberValueType()))
                            .build());
                    break;
                case MAIL:
                    contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Email.DATA, member.getValue())
                            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, member.getMemberValueType())
                            .build());
                    break;
                case ADDRESS:
                    contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.POBOX, parseAddress(member.getValue(), ContactsContract.CommonDataKinds.StructuredPostal.POBOX))
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET, parseAddress(member.getValue(), ContactsContract.CommonDataKinds.StructuredPostal.STREET))
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.CITY, parseAddress(member.getValue(), ContactsContract.CommonDataKinds.StructuredPostal.CITY))
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.REGION, parseAddress(member.getValue(), ContactsContract.CommonDataKinds.StructuredPostal.REGION))
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, parseAddress(member.getValue(), ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY))
                            .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, parseAddress(member.getValue(), ContactsContract.CommonDataKinds.StructuredPostal.TYPE))
                            .withValue(ContactsContract.CommonDataKinds.Im.TYPE, member.getMemberValueType())
                            .build());
                    break;
                case IM:
                    contact.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Im.DATA, member.getValue())
                            .withValue(ContactsContract.CommonDataKinds.Im.TYPE, member.getMemberValueType())
                            .build());
                    break;
                case OTHER:
                default:
                    throw new IllegalArgumentException();
            }
        }

        try
        {
            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, contact);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static String parseAddress(String addressString, String type)
    {
        String[] parsedAddress = addressString.split("#");
        String addressPart = "";
        switch (type)
        {

            case ContactsContract.CommonDataKinds.StructuredPostal.POBOX:
                addressPart = parsedAddress[0];
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.STREET:
                addressPart = parsedAddress[1];
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.CITY:
                addressPart = parsedAddress[2];
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.REGION:
                addressPart = parsedAddress[3];
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY:
                addressPart = parsedAddress[4];
                break;
            case ContactsContract.CommonDataKinds.StructuredPostal.TYPE:
                addressPart = parsedAddress[5];
                break;
        }
        return addressPart;
    }
}
