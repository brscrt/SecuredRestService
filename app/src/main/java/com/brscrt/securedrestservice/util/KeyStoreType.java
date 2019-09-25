package com.brscrt.securedrestservice.util;

public enum KeyStoreType {

    PKCS12("PKCS12"),
    X_509("X.509");

    private final String type;

    KeyStoreType(String type){
        this.type = type;
    }

    public String toString(){
        return type;
    }
}
