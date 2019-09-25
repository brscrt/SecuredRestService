package com.brscrt.securedrestservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class KeyStoreFactory {

    private static KeyStoreFactory theInstance = null;

    public static KeyStoreFactory getInstance() {
        if (theInstance == null) {
            theInstance = new KeyStoreFactory();
        }
        return theInstance;
    }

    public KeyManager[] getKeyManagers(KeyStoreType keyStoreType, InputStream inputStream, String password) {
        KeyStore clientStore = loadKeyStore(keyStoreType, inputStream, password);

        KeyManagerFactory kmf = null;
        try {
            kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, password.toCharArray());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return kmf.getKeyManagers();
    }

    public TrustManager[] getTrustManagers(KeyStoreType keyStoreType, InputStream inputStream, String password) {
        KeyStore trustStore;
        if(keyStoreType == KeyStoreType.X_509)
            trustStore = entryKeyStoreX509(inputStream);
        else
            trustStore = loadKeyStore(keyStoreType, inputStream, password);

        TrustManagerFactory tmf = null;
        try {
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return tmf.getTrustManagers();
    }

    private KeyStore loadKeyStore(KeyStoreType keyStoreType, InputStream inputStream, String password) {
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType.toString());
            keyStore.load(inputStream, password.toCharArray());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    private KeyStore entryKeyStoreX509(InputStream inputStream) {
        KeyStore trustStore = null;
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(KeyStoreType.X_509.toString());
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            trustStore.setCertificateEntry("ca", certificate);
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trustStore;
    }

    public X509TrustManager getAcceptedAllTrustManager() {
        X509TrustManager acceptAll = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        return acceptAll;
    }
}
