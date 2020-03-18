package com.misterymatch.app.activity;

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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.braintreepayments.cardform.view.CardForm;
import com.google.firebase.database.core.utilities.Utilities;
import com.misterymatch.app.BuildConfig;
import com.misterymatch.app.R;
import com.misterymatch.app.utils.CustomDialog;
import com.misterymatch.app.utils.SharedHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TwoCheckoutFlowPaymentActivity extends AppCompatActivity {

    Button id_paynow;
    CardForm cardForm;
    String Url = "", tips = "0";
    ImageView backArrow;
    String token_script;
    Utilities utils = new Utilities();
    double amount;
    int Id;
    private WebView mWebView;
    private CustomDialog customDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_checkout_flow_payment);
        context = this;
        initView();
    }

    public void initView() {
        id_paynow = (Button) findViewById(R.id.id_paynow);

        amount = getIntent().getExtras().getDouble("AMOUNT", 0.0);
        Id = getIntent().getExtras().getInt("ID", 0);
        tips = getIntent().getExtras().getString("tips");
        Log.d("_D_AMT", "onCreate: " + amount);

        backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TwoCheckoutFlowPaymentActivity.this, getString(R.string.transaction_canceled), Toast.LENGTH_LONG).show();
                finish();
            }
        });

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);

        cardForm = (CardForm) findViewById(R.id.card_form);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Add card details")
                .setup(this);


        mWebView = (WebView) findViewById(R.id.id_loadcheckoutwebview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
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

                if (url.startsWith("http")) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    mWebView.loadUrl("javascript:window.HTMLOUT.processHTML(document.getElementsByTagName('pre')[0].innerHTML);");
                    Url = url;
                }

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
            }
        });

        id_paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (cardForm.getCardNumber().isEmpty()) {
                        Toast.makeText(TwoCheckoutFlowPaymentActivity.this, "please_enter_card_number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (cardForm.getExpirationMonth().isEmpty()) {
                        Toast.makeText(TwoCheckoutFlowPaymentActivity.this, "please_enter_card_expiration_details", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (cardForm.getCvv().isEmpty()) {
                        Toast.makeText(TwoCheckoutFlowPaymentActivity.this, "please_enter_card_cvv", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!TextUtils.isDigitsOnly(cardForm.getExpirationMonth()) || !TextUtils.isDigitsOnly(cardForm.getExpirationYear())) {
                        Toast.makeText(TwoCheckoutFlowPaymentActivity.this, "please_enter_card_expiration_details", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String cardNumber = cardForm.getCardNumber();
                    Integer cardMonth = Integer.parseInt(cardForm.getExpirationMonth());
                    Integer cardYear = Integer.parseInt(cardForm.getExpirationYear());
                    String cardCvv = cardForm.getCvv();


                    Log.d("CARD", "CardDetails Number: " + cardNumber + "Month: " + cardMonth + " Year: " + cardYear + " Cvv " + cardCvv);

                    String base_url = BuildConfig.BASE_URL;

                    token_script = "<html>\n" +
                            "\n" +
                            "<form action=\"" + base_url + "card/add\" method=\"POST\" id=\"myCCForm\">\n" +
                            "\n" +
                            "            <input id=\"token\" name=\"token\" type=\"hidden\" value=\"\">\n" +
                            "\n" +
                            "            <input type=\"hidden\" name=\"card_id\" value=\"2CHECKOUT\">\n" +
                            "\n" +
                            "             <input name=\"type\" type=\"hidden\" value=\"user\">\n" +
                            "\n" +
                            "              <input name=\"mode\" type=\"hidden\" value=\"apii\">\n" +
                            "\n" +
                            "              <input name=\"plan_id\" type=\"hidden\" value=\""+Id+"\">\n" +
                            "\n" +
                            "              <input name=\"id\" type=\"hidden\" value=\""+
                            SharedHelper.getKey(TwoCheckoutFlowPaymentActivity.this,"user_id")+"\">\n" +
                            "\n" +
                            "            <input type=\"hidden\" name=\"amount\" placeholder=\"Amount\" value=\"" + amount + "\">\n" +
                            "\n" +
                            "            <input type=\"hidden\" name=\"request_id\"  value=\"" + Id + "\">\n" +
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
                            "                          publishableKey: \"5DD75985-2376-4867-A5E9-22E756B5EEF6\",\n" +
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
                            "                          var rest = tokenRequest(); Android.getToken(rest);\n" +
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

                    mWebView.loadData(token_script, "text/html", "UTF-8");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void paythrough2Checkout(String token, String id, String amount, String tips) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("customer_id", 29);
            object.put("amount", String.format("%.2f", Double.parseDouble(amount)));
            object.put("token", token);
            //object.put("tips",String.format("%.2f", Double.parseDouble(tips)));

        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST,
                BuildConfig.BASE_URL + "api/user/payment/checkout", object, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                utils.print("CancelRequestResponse", response.toString());
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();

                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
//                currentStatus = "";
                finish();

            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

        int socketTimeout = 120000;//120 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
//        ProfnowApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(TwoCheckoutFlowPaymentActivity.this, getString(R.string.transaction_canceled), Toast.LENGTH_LONG).show();
        finish();
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
            if (obj!=null){
                if (obj.has("message")){
                    status = getString(R.string.transaction_successful);
                    Toast.makeText(TwoCheckoutFlowPaymentActivity.this, status, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else if (obj.has("error")){
                    status = getString(R.string.transaction_declined);
                    Toast.makeText(TwoCheckoutFlowPaymentActivity.this, status, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
