package com.example.mike.testinstagramlogin;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AuthConfiguration {
    private static final String ACCESS_TOKEN_URI_TAG = "access_token_uri";
    private static final String AUTHENTICATION_URI_TAG = "authentication_uri";
    private static final String CLIENT_ID_TAG = "client_id";
    private static final String CLIENT_SECRET_TAG = "client_secret";
    private static final String GRANT_TYPE_TAG = "grant_type";
    private static final String REDIRECT_URI_TAG = "redirect_uri";
    private static final String RESPONSE_TYPE_TAG = "response_type";
    private static final String SELF_INFORMATION_URI_TAG = "self_information_uri";
    private static String access_token_uri;
    private static String authentication_uri;
    private static String client_id;
    private static String client_secret;
    private static AuthConfiguration config;
    private static String grant_type;
    private static String redirect_uri;
    private static String response_type;
    private static String self_information_uri;

    protected AuthConfiguration(Context context) throws ParserConfigurationException, IOException, SAXException {
        Document parse = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(readTextFile(context.getResources().openRawResource(R.raw.config)))));
        int i = 0;
        redirect_uri = parse.getElementsByTagName("redirect_uri").item(i).getFirstChild().getNodeValue();
        client_id = parse.getElementsByTagName("client_id").item(i).getFirstChild().getNodeValue();
        authentication_uri = parse.getElementsByTagName("authentication_uri").item(i).getFirstChild().getNodeValue();
        response_type = parse.getElementsByTagName("response_type").item(i).getFirstChild().getNodeValue();
        access_token_uri = parse.getElementsByTagName("access_token_uri").item(i).getFirstChild().getNodeValue();
        client_secret = parse.getElementsByTagName("client_secret").item(i).getFirstChild().getNodeValue();
        grant_type = parse.getElementsByTagName("grant_type").item(i).getFirstChild().getNodeValue();
        self_information_uri = parse.getElementsByTagName("self_information_uri").item(i).getFirstChild().getNodeValue();
    }

    public String getRedirectURI() {
        return redirect_uri;
    }

    public String getClientID() {
        return client_id;
    }

    public String getAuthenticationURI() {
        return authentication_uri;
    }

    public String getResponseType() {
        return response_type;
    }

    public String getAccessTokenURI() {
        return access_token_uri;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public String getGrantType() {
        return grant_type;
    }

    public String getSelfInformationURI() {
        return self_information_uri;
    }

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            try {
                int read = inputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            } catch (IOException unused) {
                return byteArrayOutputStream.toString();
            }
        }
        byteArrayOutputStream.close();
        inputStream.close();
    }

    public static AuthConfiguration getInstance(Context context) throws IOException, SAXException, ParserConfigurationException {
        if (config == null) {
            config = new AuthConfiguration(context);
        }
        return config;
    }
}
