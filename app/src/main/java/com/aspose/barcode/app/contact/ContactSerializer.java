package com.aspose.barcode.app.contact;

import android.util.Xml;

import com.aspose.barcode.app.contact.members.Member;
import com.aspose.barcode.app.contact.members.MemberType;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ContactSerializer
{
    @SuppressWarnings("null")
    public static String createXMLString(Contact contact)
    {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();

        try
        {
            //Start Document
//            xmlSerializer.startDocument("UTF-16", true);
//            xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            xmlSerializer.setOutput(writer);

            xmlSerializer.startTag("", "contact");
            xmlSerializer.attribute("", "Nickname", new String(contact.getGivenName().getBytes("ISO-8859-5"), "ISO-8859-5"));
            if (!contact.getName().isEmpty())
            {
                xmlSerializer.attribute("", "name", new String(contact.getName().getBytes("ISO-8859-5"), "ISO-8859-5"));
            }

            xmlSerializer.startTag("", "members");

            for (Member member : contact.getMembers())
            {
                member.serialize(xmlSerializer);
            }

            xmlSerializer.endTag("", "members");
            xmlSerializer.endTag("", "contact");

            xmlSerializer.endDocument();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return replaceSymbols(writer.toString());
    }

    private static void addMember(XmlSerializer xmlSerializer, String memberName, String value) throws IOException
    {
        if (value.isEmpty())
        {
            return;
        }
        xmlSerializer.startTag("", "member");
        xmlSerializer.attribute("", memberName, value);
        xmlSerializer.endTag("", "member");
    }

    public static Contact parseXmlcontact(String xmlContact)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(xmlContact.getBytes("UTF-8"));
            Document dom = builder.parse(is);

            Contact contact = new Contact(dom.getDocumentElement().getAttribute("Nickname"));
            NodeList members = dom.getDocumentElement().getFirstChild().getChildNodes();
            for (int i = 0; i < members.getLength(); i++)
            {
                Node memberNode = members.item(i);
                if (memberNode.hasAttributes())
                {
                    contact.getMembers().add(new Member(memberNode.getFirstChild().getNodeValue(), MemberType.valueOf(memberNode.getAttributes().getNamedItem("member_type").getNodeValue()), memberNode.getAttributes().getNamedItem("member_value_type").getNodeValue()));
                }
            }
            return contact;
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String replaceSymbols(String originalText)
    {
        String fixedText = "";
        for (int i = 0; i < originalText.length() - 7; i++)
        {
            char ch = originalText.charAt(i);
            String charCandidat = originalText.substring(i, i + 7);
            if ((charCandidat.charAt(0) == '&') && (charCandidat.charAt(1) == '#') && (charCandidat.charAt(6) == ';'))
            {
                ch = Character.toChars(Integer.parseInt(charCandidat.substring(2, 6)))[0];
                i += 6;
            }
            fixedText += ch;
        }

        fixedText += originalText.substring(originalText.length() - 7);

        return fixedText;
    }
}
