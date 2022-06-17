package com.aspose.barcode.app.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.RequiresApi;

import com.aspose.barcode.app.contact.members.Member;
import com.aspose.barcode.app.contact.members.MemberType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ContactsHelper
{
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String prepareContact(Contact contact)
    {
        final StringBuilder generatingContact = new StringBuilder();
        generatingContact.append(contact.getGivenName()).append("\n");

        contact.getMembers().forEach(member ->
        {
            if (!member.getValue().isEmpty())
            {
                generatingContact.append(member.getValue()).append("\n");
            }
        });

        return generatingContact.toString();
    }

    public static List<Contact> readContacts(Context context)
    {
        Set<Contact> contacts = new TreeSet<>(Comparator.comparing(Contact::getGivenName));
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if (cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (name != null && Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Contact contact = new Contact(name);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext())
                    {
                        String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                        if (phone != null && !phone.isEmpty())
                        {
                            contact.getMembers().add(new Member(phone, MemberType.PHONE, Integer.toString(phoneType)));
                        }
                    }
                    pCur.close();


                    // get email and type
                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (emailCur.moveToNext())
                    {
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        String emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                        if (email != null && !email.isEmpty())
                        {
                            contact.getMembers().add(new Member(email, MemberType.MAIL, emailType));
                        }
                    }
                    emailCur.close();

//                    // Get note....... TODO
//                    String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
//                    String[] noteWhereParams = new String[]{id,
//                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
//                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
//                    if (noteCur.moveToFirst()) {
//                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
//                        System.out.println("Note " + note);
//                    }
//                    noteCur.close();

                    //Get Postal Address.... TODO
//                    Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,null, null, null, null);
//                    while(addrCur.moveToNext()) {
//                        String poBox = addrCur.getString(
//                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
//                        String street = addrCur.getString(
//                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
//                        String city = addrCur.getString(
//                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
//                        String state = addrCur.getString(
//                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
//                        String country = addrCur.getString(
//                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
//                        String type = addrCur.getString(
//                                addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
//
//                        if(poBox != null || street != null || city != null || state != null || country != null || type != null)
//                            contact.getMembers().add(new Member(poBox + "#" + street + "#"+ city + "#"+ state + "#"+ country + "#"+ type, MemberType.ADDRESS, ""));
//                        // Do something with these....
//
//                    }
//                    addrCur.close();

                    // Get Instant <span class="IL_AD" id="IL_AD12">Messenger</span>.........
                    String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] imWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                    Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, imWhere, imWhereParams, null);
                    if (imCur.moveToFirst())
                    {
                        String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                        String imType = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
                        if (imName != null && !imName.isEmpty())
                        {
                            contact.getMembers().add(new Member(imName, MemberType.IM, imType));
                        }
                    }
                    imCur.close();

                    // Get Organizations.........

                    String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] orgWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                    Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                            null, orgWhere, orgWhereParams, null);
                    if (orgCur.moveToFirst())
                    {
                        String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                        String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
                        if (orgName != null && !orgName.isEmpty())
                        {
                            contact.setOrganization(orgName);
                        }
                    }
                    orgCur.close();
                    contacts.add(contact);
                }
            }
        }
        cur.close();
        return new ArrayList<>(contacts);
    }

}
