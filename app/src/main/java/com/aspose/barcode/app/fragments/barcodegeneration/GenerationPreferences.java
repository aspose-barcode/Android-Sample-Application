package com.aspose.barcode.app.fragments.barcodegeneration;

import com.aspose.barcode.app.contact.Contact;
import com.aspose.barcode.generation.BaseEncodeType;

import java.util.List;

public class GenerationPreferences
{
    private List<Contact> contacts;
    private BaseEncodeType encodeType;
    private String codeText;

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public BaseEncodeType getEncodeType() {
        return encodeType;
    }

    public void setEncodeType(BaseEncodeType encodeType) {
        this.encodeType = encodeType;
    }

    public String getCodeText() {
        return codeText;
    }

    public void setCodeText(String codeText) {
        this.codeText = codeText;
    }
}
