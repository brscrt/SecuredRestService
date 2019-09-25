package com.brscrt.securedrestservice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.brscrt.securedrestservice.connection.Connector;
import com.brscrt.securedrestservice.util.KeyStoreFactory;
import com.brscrt.securedrestservice.util.KeyStoreType;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class MainActivity extends AppCompatActivity {

    private Switch switchUseClientCertificate, switchAcceptAllCertificate;
    private RadioButton radioButtonP12;
    private Button buttonCallService;
    private TextView textViewResult;
    private RadioGroup radioGroupTrustManagerLoadAs;
    private static int customerId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadViews();
        setActions();
    }

    private void loadViews() {
        switchUseClientCertificate = findViewById(R.id.switchUseClientCertificate);
        switchAcceptAllCertificate = findViewById(R.id.switchAcceptAllCertificate);
        radioButtonP12 = findViewById(R.id.radioButtonP12);
        buttonCallService = findViewById(R.id.buttonCallService);
        textViewResult = findViewById(R.id.textViewResult);
        radioGroupTrustManagerLoadAs = findViewById(R.id.radioGroupTrustManagerLoadAs);
    }

    private void setActions() {
        switchAcceptAllCertificate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < radioGroupTrustManagerLoadAs.getChildCount(); i++) {
                    radioGroupTrustManagerLoadAs.getChildAt(i).setEnabled(!isChecked);
                }
            }
        });
        buttonCallService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    doRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (KeyManagementException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void doRequest() throws IOException, NoSuchAlgorithmException, KeyManagementException {
        KeyStoreFactory keyStoreFactory = KeyStoreFactory.getInstance();
        KeyManager[] keyManagers = null;
        String connectURL = getString(R.string.url_server) + "/hello";

        if (switchUseClientCertificate.isChecked()) {
            keyManagers = keyStoreFactory.getKeyManagers(KeyStoreType.PKCS12, getAssets().open(getString(R.string.client_cert_file)), getString(R.string.cert_password));
            connectURL = getString(R.string.url_server_client_certificated) + "/customer/" + customerId;
            customerId++;
        }
        TrustManager[] trustManagers;
        if (switchAcceptAllCertificate.isChecked()) {
            trustManagers = new TrustManager[]{keyStoreFactory.getAcceptedAllTrustManager()};
        } else if (!radioButtonP12.isChecked())
            trustManagers = keyStoreFactory.getTrustManagers(KeyStoreType.X_509, getAssets().open(getString(R.string.server_cert_file_crt)), getString(R.string.cert_password));
        else
            trustManagers = keyStoreFactory.getTrustManagers(KeyStoreType.PKCS12, getAssets().open(getString(R.string.server_cert_file_p12)), getString(R.string.cert_password));

        SSLContext sslContext = SSLContext.getInstance(Connector.defaultSSLType);
        sslContext.init(keyManagers, trustManagers, new SecureRandom());

        new Connect(connectURL, sslContext).execute();
    }

    private void updateOutput(String text) {
        textViewResult.setText(text);
    }

    private class Connect extends AsyncTask<String, Void, String> {
        String result, url;
        SSLContext sslContext;

        public Connect(String urlString, SSLContext sslContext) {
            url = urlString;
            this.sslContext = sslContext;
        }

        @Override
        protected String doInBackground(String... params) {
            result = Connector.getInstance().doSSLGet(url, sslContext, true);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            updateOutput(this.result);
        }

    }

}
