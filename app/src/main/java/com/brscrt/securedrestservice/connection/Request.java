package com.brscrt.securedrestservice.connection;

import javax.net.ssl.SSLContext;

public interface Request {
    String defaultSSLType = "TLS";
    String doGet(String urlString);
    String doSSLGet(String urlString, SSLContext sslContext, boolean verifyAllHostname);
    String doPost(String urlString);
    String doSSLPost(String urlString, SSLContext sslContext, boolean verifyAllHostname);
}
