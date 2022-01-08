package com.example.splocmobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private TextView plavalci;
    private String url = "https://sploc-webapp.azurewebsites.net/api/PlavalciApi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        plavalci = (TextView) findViewById(R.id.plavalci);
    }

    public  void prikaziPlavalce(View view){
        if (view != null){
            JsonArrayRequest request = new JsonArrayRequest(url, jsonArrayListener, errorListener)
            {
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError
                {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ApiKey", "Password123");
                    return params;
                }
            };
            requestQueue.add(request);
        }
    }

    public static final String EXTRA_MESSAGE = "com.example.splocmobileapp.MESSAGE";

    public void addPlavalecActivity (View view) {
        Intent intent = new Intent(this,AddPlavalecActivity.class);
        String message = "Dodaj plavalca v seznam.";
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    private Response.Listener<JSONArray> jsonArrayListener = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response){
            ArrayList<String> data = new ArrayList<>();

            for (int i = 0; i < response.length(); i++){
                try {
                    JSONObject object =response.getJSONObject(i);
                    String name = object.getString("ime");
                    String surname = object.getString("priimek");
                    String groupID = object.getString("skupinaID");

                    data.add("Ime in priimek: " + name + " " + surname + ", Skupina: " + groupID);

                } catch (JSONException  e){
                    e.printStackTrace();
                    return;

                }
            }

            plavalci.setText("");


            for (String row: data){
                String currentText = plavalci.getText().toString();
                plavalci.setText(currentText + "\n\n" + row);
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if(error instanceof AuthFailureError){
                plavalci.setText("Nepravilno API geslo");
            }else{
                Log.d("REST error", error.toString());
            }

        }
    };


}