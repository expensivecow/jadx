package com.example.mike.testinstagramlogin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.android.volley.toolbox.Volley;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.SAXException;

public class RedirectedActivity extends AppCompatActivity {
    private static final String TAG = "RedirectedActivity";
    private static String mAccessToken;
    private TextView userInfo;

    public class Hello2 {
        public boolean BooleanFunction(int i) {
            return i == 100;
        }

        Hello2() {
            System.out.println("This is a second test");
        }

        public void TestFunction2() {
            System.out.println("Hello world2");
        }
    }

    public class Hello {
        public int TestFunction10(int i) {
            int i2 = 255;
            return i > 0 ? 0 : i > i2 ? i2 : i;
        }

        public void TestFunction3(int i) {
            for (int i2 = 0; i2 < 10; i2++) {
                int i3 = 1;
                if (i == i3) {
                    i = i3;
                }
            }
        }

        public int TestFunction4(int i) {
            int i2 = 1;
            return i == i2 ? i2 : i == 0 ? 2 : 3;
        }

        public void TestFunction6(int i) {
            Object obj = 1;
        }

        public int TestFunction8() {
            return 1;
        }

        public void nothingFunction() {
        }

        Hello() {
            System.out.println("This is a test");
        }

        public void TestFunction1(int i, int i2) {
            int i3 = 1;
            if (i == i3 && i2 == 0 && i == i3 && i2 != i3) {
                System.out.println("Woohoo");
            }
            if (i == i3 || i2 == i3) {
                System.out.println("Wow");
            }
            if (i == 0 && i2 == i3) {
                System.out.println("Hello");
            }
        }

        public void TestFunction5(int i) {
            Hello2 hello2 = new Hello2();
            if (i == 1) {
                boolean BooleanFunction = hello2.BooleanFunction(i);
            }
        }

        public boolean TestFunction7(String str) {
            return "Hello World".equals(str) && "Hi World".equals(str);
        }

        public void TestFunction9(int i) {
            Hello2 hello2 = new Hello2();
            if (i == 1) {
                boolean BooleanFunction = hello2.BooleanFunction(i);
            }
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_redirected);
        this.userInfo = (TextView) findViewById(R.id.UserInfo);
        try {
            requestAccessToken();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SAXException e2) {
            e2.printStackTrace();
        } catch (ParserConfigurationException e3) {
            e3.printStackTrace();
        } catch (IOException e4) {
            e4.printStackTrace();
        }
    }

    private void requestAccessToken() throws IOException, ParserConfigurationException, SAXException {
        URL url = new URL(getIntent().getDataString());
        final String clientID = AuthConfiguration.getInstance(this).getClientID();
        final String clientSecret = AuthConfiguration.getInstance(this).getClientSecret();
        final String grantType = AuthConfiguration.getInstance(this).getGrantType();
        final String redirectURI = AuthConfiguration.getInstance(this).getRedirectURI();
        final String str = (String) splitQuery(url).get("code");
        final String accessTokenURI = AuthConfiguration.getInstance(this).getAccessTokenURI();
        if (!(clientID == null || clientSecret == null || grantType == null || redirectURI == null)) {
            Log.i("RedirectedActivity", "Hello this is a test");
        }
        new Thread() {
            public void run() {
                Log.i("RedirectedActivity", "Getting access token");
                try {
                    URL url = new URL(accessTokenURI);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Opening Token URL ");
                    stringBuilder.append(url.toString());
                    Log.i("RedirectedActivity", stringBuilder.toString());
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    boolean z = true;
                    httpURLConnection.setDoInput(z);
                    httpURLConnection.setDoOutput(z);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream());
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("client_id=");
                    stringBuilder.append(clientID);
                    stringBuilder.append("&client_secret=");
                    stringBuilder.append(clientSecret);
                    stringBuilder.append("&grant_type=");
                    stringBuilder.append(grantType);
                    stringBuilder.append("&redirect_uri=");
                    stringBuilder.append(redirectURI);
                    stringBuilder.append("&code=");
                    stringBuilder.append(str);
                    outputStreamWriter.write(stringBuilder.toString());
                    outputStreamWriter.flush();
                    String access$000 = RedirectedActivity.this.streamToString(httpURLConnection.getInputStream());
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("response ");
                    stringBuilder.append(access$000);
                    Log.i("RedirectedActivity", stringBuilder.toString());
                    RedirectedActivity.mAccessToken = ((JSONObject) new JSONTokener(access$000).nextValue()).getString("access_token");
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Got access token: ");
                    stringBuilder2.append(RedirectedActivity.mAccessToken);
                    Log.i("RedirectedActivity", stringBuilder2.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> linkedHashMap = new LinkedHashMap();
        String[] split = url.getQuery().split("&");
        int i = 0;
        int length = split.length;
        for (int i2 = i; i2 < length; i2++) {
            String str = split[i2];
            int indexOf = str.indexOf("=");
            linkedHashMap.put(URLDecoder.decode(str.substring(i, indexOf), "UTF-8"), URLDecoder.decode(str.substring(indexOf + 1), "UTF-8"));
        }
        return linkedHashMap;
    }

    public void getSelfInformation(View view) throws ParserConfigurationException, SAXException, IOException {
        Volley.newRequestQueue(this);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(AuthConfiguration.getInstance(this).getSelfInformationURI());
        stringBuilder.append("?access_token=");
        stringBuilder.append(mAccessToken);
        final String stringBuilder2 = stringBuilder.toString();
        System.out.println(stringBuilder2);
        new Thread() {
            public void run() {
                Log.i("RedirectedActivity", "Getting self information");
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(stringBuilder2).openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setDoInput(true);
                    RedirectedActivity.this.userInfo.setText(RedirectedActivity.this.streamToString(httpURLConnection.getInputStream()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private String streamToString(InputStream inputStream) throws IOException {
        String str = "";
        if (inputStream == null) {
            return str;
        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine);
            }
            bufferedReader.close();
            return stringBuilder.toString();
        } finally {
            inputStream.close();
        }
    }
}
