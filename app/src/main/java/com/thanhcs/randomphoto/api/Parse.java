package com.thanhcs.randomphoto.api;

/**
 * Created by thanhcs94 on 6/10/2016.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Parse{
private static Reader reader=null;
        public static Reader getData(URL SERVER_URL) {
            try {
			/*DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(SERVER_URL);
			HttpResponse response = httpClient.execute(httpPost);
			StatusLine statusLine = response.getStatusLine();*/
                HttpURLConnection conn = (HttpURLConnection) SERVER_URL.openConnection();
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(30000);
                int code = conn.getResponseCode();
                if (code == 200)  {
                    InputStream in = conn.getInputStream();
                    reader = new InputStreamReader(in);
                }
                else {
                    //Log.wtf("errorloading:", "Server responded with status code: ");

                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reader;
        }
}
