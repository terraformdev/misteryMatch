package com.misterymatch.app.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.braintreepayments.cardform.view.CardForm;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.R;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.SharedHelper;

public class TwoCheckoutPaymentActivity extends AppCompatActivity {
    private static final String TAG = "TwoCheckoutPaymentActiv";
    Button id_paynow;
    private WebView mWebView;
    CardForm cardForm;

    String amount, Id;
    private CustomDialog customDialog;
    private Context context;
    ImageView backArrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_two_checkout_payment);

        id_paynow = (Button) findViewById(R.id.id_paynow);

        Log.i(TAG, "onCreate: " + "Working");

        amount = getIntent().getExtras().getString("Amount");
        Id = getIntent().getExtras().getString("Id");
        Log.d("_D_AMT", "onCreate: " + amount);
        Log.d("_D_AMT", "onCreate: " + Id);

        customDialog = new CustomDialog(TwoCheckoutPaymentActivity.this);
        customDialog.setCancelable(false);

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> {
            Toast.makeText(TwoCheckoutPaymentActivity.this, getString(R.string.transaction_canceled), Toast.LENGTH_LONG).show();
            finish();
        });

        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Add card details")
                .setup(this);


        mWebView = findViewById(R.id.id_loadcheckoutwebview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("_D", "onPageStarted: " + url);
                if ((customDialog != null))
                    customDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("_D", "onPageFinished: " + url);
//                mWebView.loadUrl("javascript:Android.getIds(token);");
//                mWebView.loadUrl("javascript:Android.getIds(document.getElementById('token').value);");

                if (url.startsWith("http")) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    mWebView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('pre')[0].innerHTML);");
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }


        });
//        mWebView.loadUrl(token_script);

//        mWebView.loadData(token_script, "text/html", "UTF-8");

        id_paynow.setOnClickListener(v -> {

            try {
//                    testChargeAuth();

                if (cardForm.getCardNumber().isEmpty()) {
                    Toast.makeText(TwoCheckoutPaymentActivity.this, "please_enter_card_number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardForm.getExpirationMonth().isEmpty()) {
                    Toast.makeText(TwoCheckoutPaymentActivity.this, "please_enter_card_expiration_details", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cardForm.getCvv().isEmpty()) {
                    Toast.makeText(TwoCheckoutPaymentActivity.this, "please_enter_card_cvv", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isDigitsOnly(cardForm.getExpirationMonth()) || !TextUtils.isDigitsOnly(cardForm.getExpirationYear())) {
                    Toast.makeText(TwoCheckoutPaymentActivity.this, "please_enter_card_expiration_details", Toast.LENGTH_SHORT).show();
                    return;
                }

                String cardNumber = cardForm.getCardNumber();
                Integer cardMonth = Integer.parseInt(cardForm.getExpirationMonth());
                Integer cardYear = Integer.parseInt(cardForm.getExpirationYear());
                String cardCvv = cardForm.getCvv();


                Log.d("CARD", "CardDetails Number: " + cardNumber + "Month: " + cardMonth + " Year: " + cardYear + " Cvv " + cardCvv);


                String token_script = "<html>\n" +
                        "\n" +
                        "<form action=\"http://52.167.61.184/api/user/payment/flowcheck?mode=apii&id={request_id}\" method=\"POST\" id=\"myCCForm\">\n" +
                        "\n" +
                        "            <input id=\"token\" name=\"token\" type=\"hidden\" value=\"\">\n" +
                        "\n" +
                        "            <input type=\"hidden\" name=\"card_id\" value=\"2CHECKOUT\">\n" +
                        "\n" +
                        "             <input name=\"type\" type=\"hidden\" value=\"user\">\n" +
                        "\n" +
                        "             <input name=\"id\" type=\"hidden\" value=\"" + Id + "\">\n" +
                        "\n" +
                        "              <input name=\"mode\" type=\"hidden\" value=\"apii\">\n" +
                        "\n" +
                        "            <input type=\"hidden\" name=\"amount\" placeholder=\"Amount\" value=\"" + amount + "\">\n" +
                        "\n" +
                        "            <input type='hidden' id=\"ccNo\" value=\"" + cardNumber + "\" >\n" +
                        "\n" +
                        "            <input type=\"hidden\" id=\"expMonth\" value=\"" + cardMonth + "\">\n" +
                        "\n" +
                        "            <input type=\"hidden\" id=\"expYear\" value=\"" + cardYear + "\">\n" +
                        "\n" +
                        "            <input type='hidden'id=\"cvv\" value=\"" + cardCvv + "\" >\n" +
                        "            \n" +
                        "            <button type=\"button\" class=\"full-primary-btn sub fare-btn\">Add Money</button>\n" +
                        "\n" +
                        "      </form>\n" +
                        "\n" +
                        "\n" +
                        "<script type=\"text/javascript\" src=\"https://2checkout.com/checkout/api/2co.min.js\"></script>\n" +
                        "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>\n" +
                        "\n" +
                        "\n" +
                        "<script>\n" +
                        "                \n" +
                        "                var successCallback = function(data) {\n" +
                        "                    console.log(data);\n" +
                        "                    var myForm = document.getElementById('myCCForm');\n" +
                        "                    // alert(myForm);\n" +
                        "                    myForm.token.value = data.response.token.token;\n" +
                        "                    myForm.submit();\n" +
                        "                    // alert(myForm);\n" +
                        "                    };\n" +
                        "\n" +
                        "                              // Called when token creation fails.\n" +
                        "               var errorCallback = function(data)\n" +
                        "               {\n" +
                        "                  if (data.errorCode === 200) {\n" +
                        "                      tokenRequest();\n" +
                        "                  } else {\n" +
                        "                       alert(data.errorMsg);\n" +
                        "                  }\n" +
                        "               };\n" +
                        "\n" +
                        "               var tokenRequest = function() {                    \n" +
                        "                   var args = {\n" +
                        "                          sellerId: \"901396392\",\n" +
                        "                          publishableKey: \"CFAA732B-1567-48B3-83AE-CB358F9EA3F1\",\n" +
                        "                          ccNo: $(\"#ccNo\").val(),\n" +
                        "                          cvv: $(\"#cvv\").val(),\n" +
                        "                          expMonth: $(\"#expMonth\").val(),\n" +
                        "                          expYear: $(\"#expYear\").val()\n" +
                        "                       };\n" +
                        "                         // alert( $(\"#cvv\").val());\n" +
                        "\n" +
                        "                  TCO.requestToken(successCallback, errorCallback, args);\n" +
                        "               };\n" +
                        "\n" +
                        "               $(function() {\n" +
                        "\n" +
                        "                   TCO.loadPubKey('sandbox');\n" +
                        "\n" +
                        "                      $(document).on('click', '.sub', function() {                            \n" +
                        "                          var rest = tokenRequest(); Android.gePtToken(rest);\n" +
                        "                      });\n" +
                        "                    \n" +
                        "                    setTimeout(function(){ $(\".sub\").trigger('click');  }, 3000);    \n" +
                        "                      \n" +
                        "               });\n" +
                        "                \n" +
                        "                \n" +
                        "\n" +
                        "</script>\n" +
                        "\n" +
                        "\n" +
                        "</html>";


//                mWebView.loadData(token_script, "text/html", "UTF-8");
                mWebView.loadDataWithBaseURL(BuildConfig.BASE_URL,token_script, "text/html", "UTF-8",null);
//                    mWebView.loadUrl("javascript:Android.getIds(document.getElementById('token').value);");

                Log.d("_D", "testChargeAuth: " + token_script);


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void testChargeAuth() throws Exception {
        try {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("sid", "1817037");
            params.put("mode", "2CO");
            params.put("li_0_type", "product");
            params.put("li_0_name", "Example Product");
            params.put("li_0_price", "1.00");

            String form = submit(params);

            form = "<html>" + form + "</html>";

            mWebView.loadUrl(form);

            Log.d("_D", "testChargeAuth: " + form);
        } catch (Exception e) {
            String message = e.toString();
            Log.d("_D_C", "com.twocheckout.TwocheckoutException: " +
                    "Unauthorized :" + message);
        }
    }


    public static String submit(HashMap<String, String> args) {
        StringBuilder html = new StringBuilder();
        html.append("<form id=\"2checkout\" action=\"http://52.167.61.184/api/user/payment/checkout?mode=apii\" method=\"post\">\n");
        Iterator i$ = args.entrySet().iterator();

        while (i$.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) i$.next();
            html.append("<input type=\"hidden\" name=\"" + (String) entry.getKey() + "\" value=\"" + (String) entry.getValue() + "\"/>\n");
        }

        html.append("</form>\n");
        html.append("<script type=\"text/javascript\">document.getElementById('2checkout').submit();</script>");
        return html.toString();
    }

    public class CustomJavaScriptInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        CustomJavaScriptInterface(Context c) {
            mContext = c;
        }


        /**
         * retrieve the ids
         */
        @JavascriptInterface
        public void getIds(final String myIds) {

            //Do somethings with the Ids
            Log.d("_D_JS", "getIds: " + myIds);

        }

        @JavascriptInterface
        public void getToken(final String myIds) {

            //Do somethings with the Ids
            Log.d("_Dtoken", "getIds: " + myIds);

        }
    }

    private class MyJavaScriptInterface {
        @JavascriptInterface
        public void processHTML(String html) {

            if ((customDialog != null) && customDialog.isShowing())
                customDialog.dismiss();
            JSONObject obj = null;
            try {

                obj = new JSONObject(html);

                Log.d("My App", obj.toString());

            } catch (Throwable t) {
                Log.e("My App", "Could not parse malformed JSON: \"" + html + "\"");
            }

            String status = null;
            if (obj != null) {
                if (obj.has("message")) {
                    status = getString(R.string.transaction_successful);
                    Toast.makeText(TwoCheckoutPaymentActivity.this, status, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else if (obj.has("error")) {
                    status = getString(R.string.transaction_declined);
                    Toast.makeText(TwoCheckoutPaymentActivity.this, status, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void paythrough2Checkout(String token, String id, String amount) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("request_id", id);
            object.put("amount", amount);
            object.put("token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, "https://vitvit.online/api/user/payment/checkout", object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                utils.print("CancelRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();

            }
        }, error -> {
            if ((customDialog != null) && (customDialog.isShowing()))
                customDialog.dismiss();
            String json = null;
            String Message;
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {

                try {
                    JSONObject errorObj = new JSONObject(new String(response.data));

                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("message"));
                        } catch (Exception e) {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    } else if (response.statusCode == 401) {
                    } else if (response.statusCode == 422) {

//                            json = trimMessage(new String(response.data));
                        if (json != "" && json != null) {
                            displayMessage(json);
                        } else {
                            displayMessage(getString(R.string.please_try_again));
                        }
                    } else if (response.statusCode == 503) {
                        displayMessage(getString(R.string.server_down));
                    } else {
                        displayMessage(getString(R.string.please_try_again));
                    }

                } catch (Exception e) {
                    displayMessage(getString(R.string.something_went_wrong));
                }

            } else {
                if (error instanceof NoConnectionError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

//        MyApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } catch (Exception e) {
            try {
                Toast.makeText(context, "" + toastString, Toast.LENGTH_SHORT).show();
            } catch (Exception ee) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(TwoCheckoutPaymentActivity.this, getString(R.string.transaction_canceled), Toast.LENGTH_LONG).show();
        finish();
    }
}