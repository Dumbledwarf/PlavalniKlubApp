package com.example.splocmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AddPlavalecActivity extends AppCompatActivity {

    private TextView status;
    private EditText name;
    private EditText surname;
    private EditText date;
    private EditText groupID;
    private TextView mozneSkupine;

    private RequestQueue requestQueue;
    private String url = "https://sploc-webapp.azurewebsites.net/api/PlavalciApi";
    private ArrayList<String> skupineDataList = new ArrayList<>();
    private String zbirkaSkupin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plavalec);

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String requestURL = "https://sploc-webapp.azurewebsites.net/api/SkupineApi";
        JsonArrayRequest request = new JsonArrayRequest(requestURL, jsonArrayListener, errorListener);
        queue.add(request);


        name = (EditText) findViewById(R.id.teName);
        surname = (EditText) findViewById(R.id.teSurname);
        date = (EditText) findViewById(R.id.teDate);
        groupID = (EditText) findViewById(R.id.teSkupinaID);
        mozneSkupine = (TextView) findViewById(R.id.mozneSkupine);
        status = (TextView) findViewById(R.id.status);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){

            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String skupina = object.getString("skupinaID");

                    skupineDataList.add(skupina);

                } catch (JSONException e){
                    e.printStackTrace();
                    return;
                }
            }

            zbirkaSkupin = "";
            for (String skupina : skupineDataList){
                zbirkaSkupin += skupina + ", ";
            }
            zbirkaSkupin = zbirkaSkupin.substring(0, zbirkaSkupin.length()-2);

            mozneSkupine.setText("Mozne skupine: " + zbirkaSkupin);
        }

    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.d("REST error", error.getMessage());
        }
    };

    public void addPlavalec(View view){
        this.status.setText("Posting to " + url);
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("ime", name.getText());
            jsonBody.put("priimek", surname.getText());
            jsonBody.put("datumRojstva", date.getText());
            jsonBody.put("skupinaID", groupID.getText());

            final String mRequestBody = jsonBody.toString();

            //status.setText(mRequestBody);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    status.setText("Napaka pri vnosu podatkov, prosimo preverite podatke in ponovno poizkusite");
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }
            ) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        status.setText(responseString);
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }

            };

            requestQueue.add(stringRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}