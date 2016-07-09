package com.thanhcs.randomphoto.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * Created by Toan_Kul on 11/27/2014.
 */
public class Check_Connection {
    private Context _context;
    public static ArrayList<String> arrString;

    public Check_Connection(Context context){
        this._context = context;
        arrString = new ArrayList<String>();
    }

    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                {
                    String temp =  "Type name : "+info[i].getTypeName()+"\nType : "+info[i].getType()
                            +"\nReason : "+info[i].getReason();
                    arrString.add(temp);//get infor

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }

        }
        return false;
    }
}
