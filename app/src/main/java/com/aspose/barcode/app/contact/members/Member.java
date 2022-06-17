package com.aspose.barcode.app.contact.members;

import android.provider.ContactsContract;
import android.util.Xml;

import com.aspose.barcode.app.contact.ContactSerializer;

import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public class Member
{
    private String value;
    private MemberType memberType;
    private String memberValueType;

    public Member(String value, MemberType memberType, String memberValueType)
    {
        this.value = value;
        this.memberType = memberType;
        this.memberValueType = memberValueType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public MemberType getMemberType() {
        return memberType;
    }

    public void setMemberType(MemberType memberType) {
        this.memberType = memberType;
    }

    public String getMemberValueType() {
        return memberValueType;
    }

    public void setMemberValueType(String memberValueType) {
        this.memberValueType = memberValueType;
    }

    public String toString()
    {
        return memberType + "/" + getDisplayMemberValueTypeName() + ":" + value;
    }

    private String getDisplayMemberValueTypeName()
    {
        String displayMemberValueTypeName = "";
        switch (Integer.parseInt(memberValueType))
        {
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                displayMemberValueTypeName = "WORK";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                displayMemberValueTypeName = "MOBILE";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                displayMemberValueTypeName = "HOME";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                displayMemberValueTypeName = "OTHER";
                break;
            default:
                throw new IllegalArgumentException();
        }
        return displayMemberValueTypeName;
    }

    public String toXmlString() throws IOException
    {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        xmlSerializer.startTag("", "member");
            xmlSerializer.attribute("", "member_type", memberType.toString());
            xmlSerializer.attribute("", "member_value_type", memberValueType);
                xmlSerializer.text(value);
        xmlSerializer.endTag("", "member");

        return ContactSerializer.replaceSymbols(writer.toString());
    }

    public static String toXmlString(Member member) throws IOException
    {
        return member.toXmlString();
    }

    public void serialize(XmlSerializer xmlSerializer) throws IOException
    {
        xmlSerializer.startTag("", "member");
        xmlSerializer.attribute("", "member_type", memberType.toString());
        xmlSerializer.attribute("", "member_value_type", memberValueType);
        xmlSerializer.text(value);
        xmlSerializer.endTag("", "member");
    }
}