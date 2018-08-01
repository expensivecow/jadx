package com.example.mike.testinstagramlogin;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

public class MainActivity extends AppCompatActivity {
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
    }

    public void authorizeLogin(View view) throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(createAuthURL())));
    }

    private String createAuthURL() throws ParserConfigurationException, SAXException, IOException {
        String authenticationURI = AuthConfiguration.getInstance(this).getAuthenticationURI();
        String clientID = AuthConfiguration.getInstance(this).getClientID();
        String redirectURI = AuthConfiguration.getInstance(this).getRedirectURI();
        String responseType = AuthConfiguration.getInstance(this).getResponseType();
        Builder buildUpon = Uri.parse(authenticationURI).buildUpon();
        buildUpon.appendQueryParameter("client_id", clientID);
        buildUpon.appendQueryParameter("redirect_uri", redirectURI);
        buildUpon.appendQueryParameter("response_type", responseType);
        buildUpon.appendQueryParameter("scope", "follower_list");
        return buildUpon.build().toString();
    }
}
