package com.aspose.barcode.app.assist;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommonAssist
{
    public static boolean isEmpty(String s)
    {
        return s == null || s.length() < 1;
    }

    public static boolean isNotEmpty(String s)
    {
        return s != null && s.length() > 0;
    }

    public static String readFileFromAssets(Context context, String filename) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));

        // do reading, usually loop until end of file reading
        StringBuilder sb = new StringBuilder();
        String l = reader.readLine();
        while (l != null)
        {
            sb.append(l); // process line
            l = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
}
