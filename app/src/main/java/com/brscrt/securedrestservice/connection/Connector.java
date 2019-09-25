package com.brscrt.securedrestservice.connection;

import com.brscrt.securedrestservice.util.IOUtil;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;

public class Connector implements Request {

    private static Connector theInstance = null;

    public static Connector getInstance() {
        if (theInstance == null) {
            theInstance = new Connector();
        }
        return theInstance;
    }

    @Override
    public String doGet(String urlString) {
        return null;
    }

    @Override
    public String doSSLGet(String urlString, SSLContext sslContext, boolean verifyAllHostname) {
        String result;

        HttpURLConnection urlConnection = null;
        try {
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            URL url = new URL(urlString);

            urlConnection = (HttpsURLConnection) url.openConnection();
            if (verifyAllHostname)
                ((HttpsURLConnection) urlConnection).setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                        //HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                        //return hv.verify("10.0.2.2", session);
                    }
                });
            urlConnection.setRequestMethod("GET");

            result = IOUtil.readFully(urlConnection.getInputStream());

        } catch (Exception ex) {
            result = ex.toString();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    @Override
    public String doPost(String urlString) {
        return null;
    }

    @Override
    public String doSSLPost(String urlString, SSLContext sslContext, boolean verifyAllHostname) {
        return null;
    }

}
