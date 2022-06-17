package com.aspose.barcode.app.contact;

import androidx.annotation.NonNull;


import com.aspose.barcode.app.contact.members.Member;
import com.aspose.barcode.app.contact.members.MemberType;

import java.util.ArrayList;
import java.util.List;

public class Contact
{
    private String name;
    private String givenName;
    private String organization;
    private List<Member> members;

    public Contact(String givenName, String name, String organization, List<Member> members)
    {
        this.givenName = givenName;
        this.name = name;
        this.organization = organization;
        this.members = members;
    }
    public Contact(String givenName)
    {
        this(givenName, "", "", new ArrayList<>());
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getGivenName()
    {
        return givenName;
    }

    public void setGivenName(String givenName)
    {
        this.givenName = givenName;
    }

    public String getOrganization()
    {
        return organization;
    }

    public void setOrganization(String organization)
    {
        this.organization = organization;
    }

    public List<Member> getMembers()
    {
        return members;
    }

    public void setMembers(List<Member> members)
    {
        this.members = members;
    }

    @NonNull
    public String toString()
    {
        StringBuilder stringMember = new StringBuilder();
        members.forEach(member -> {if(member.getMemberType() == MemberType.PHONE) stringMember.append(member.getValue());});
        if(stringMember.length() == 0)members.forEach(member -> {if(member.getMemberType() == MemberType.MAIL) stringMember.append(member.getValue());});
        else if(stringMember.length() == 0)members.forEach(member -> {if(member.getMemberType() == MemberType.IM) stringMember.append(member.getValue());});

        return givenName + "\n" + stringMember.toString();
    }

}
