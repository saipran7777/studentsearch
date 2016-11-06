package com.bignerdranch.android.studentsinfo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class StudentsActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    private AutoCompleteTextView findStudent;
    ArrayList<String> studentSuggestion = new ArrayList<>(25);
    ArrayAdapter<String> adapter;
    int flag = 0;//0 if by name 1 if roll no
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        listView = (ListView) findViewById(R.id.list_view_suggestion);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                studentSuggestion);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                goToDetails(name);
            }
        });

        //Set adapter to AutoCompleteTextView
        findStudent = (AutoCompleteTextView) findViewById(R.id.search_view);
//        findStudent.setThreshold(2);
        findStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (studentSuggestion.size() > 5) {
                    studentSuggestion.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("afterTextChange", s.toString());
                if (flag == 0 && s.length() > 2) doMySearchByName(s.toString());
                else if(flag == 0) {
                    studentSuggestion.clear();
                    adapter.notifyDataSetChanged();}
            }
        });
        findStudent.setOnItemClickListener(this);
        findStudent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                                  @Override
                                                  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                                                      if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                                          final Editable selection = findStudent.getText();

                                                          findStudent.setText("");
                                                          InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                          imm.hideSoftInputFromWindow(findStudent.getWindowToken(), 0);

                                                          if (flag == 0) doMySearchByName(selection.toString());
                                                          else if (flag == 1) doMySearchByRoll(selection.toString());
                                                          return true;
                                                      }

                                                      return false;
                                                  }
                                              }

        );


        final TextView searchMode = (TextView) findViewById(R.id.search_mode);

        Switch searchSwitch = (Switch) findViewById(R.id.search_switch);
        searchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    flag = 1;
                    searchMode.setText("Search by roll number");
                } else {
                    // The toggle is disabled
                    flag = 0;
                    searchMode.setText("Search by student name");
                }
            }
        });
        searchSwitch.setTextOff("Roll");
        searchSwitch.setTextOn("Name");


    }

    public void doMySearchByName(final String query) {

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("studentlist")
                .appendPath("getresultbyname.php")
                .appendQueryParameter("name", query);

        String url = builder.build().toString();
        Log.d("searchUrl", url);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    Log.d("JsonResponseQuery", query);
                    int i;
                    String studName, studRoll, hostel, roomNo, photo;

                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        studName = jsonObject.getString("fullname");
                        studRoll = jsonObject.getString("username");
                        hostel = jsonObject.getString("hostel");
                        roomNo = jsonObject.getString("roomno");
                        photo = jsonObject.getString("url");
                        if (!studentSuggestion.contains(studName))
                            studentSuggestion.add(studName);//+", "+studRoll
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    studentSuggestion.clear();
                    adapter.notifyDataSetChanged();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error response " + error, Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(jsonObjReq);
    }

    public void doMySearchByRoll(String query) {

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("studentlist")
                .appendPath("getresultbyroll.php")
                .appendQueryParameter("rollno", query);

        String url = builder.build().toString();
        Log.d("Url", url);
        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {


                try {

                    JSONArray jsonArray = new JSONArray(response);
                    Log.d("JsonResponse", response);
                    JSONObject jsonObject;
                    int i;
                    String studName = "Student name", studRoll = "Student no", hostel = "hostel name", roomNo = "roll no", photo = "photo";

                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        studName = jsonObject.getString("fullname");
                        studRoll = jsonObject.getString("username");
                        hostel = jsonObject.getString("hostel");
                        roomNo = jsonObject.getString("roomno");
                        photo = jsonObject.getString("url");

                    }
                    Intent intent = new Intent(getApplicationContext(), StudentDetailsActivity.class);
                    intent.putExtra("studName", studName);
                    intent.putExtra("studRoll", studRoll);
                    intent.putExtra("hostel", hostel);
                    intent.putExtra("roomNo", roomNo);
                    intent.putExtra("photo", photo);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No result found!\n" + e, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error response " + error, Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(jsonObjReq);
    }

    public void goToDetails(String query) {

        final RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("https")//https://students.iitm.ac.in/studentsapp/map/get_location.php?
                .authority("students.iitm.ac.in")
                .appendPath("studentsapp")
                .appendPath("studentlist")
                .appendPath("getresultbyname.php")
                .appendQueryParameter("name", query);

        String url = builder.build().toString();
        Log.d("searchUrl", url);

        // Request a string response from the provided URL.
        StringRequest jsonObjReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject;
                    Log.d("JsonResponse", response);
                    int i;
                    String studName = "name appears here",
                            studRoll = "roll no appears here",
                            hostel = "hostel", roomNo = "room no", photo = "https://photos.iitm.ac.in//byroll.php?roll=wrongSyntax";

                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        studName = jsonObject.getString("fullname");
                        studRoll = jsonObject.getString("username");
                        hostel = jsonObject.getString("hostel");
                        roomNo = jsonObject.getString("roomno");
                        photo = jsonObject.getString("url");

                    }
                    Intent intent = new Intent(getApplicationContext(), StudentDetailsActivity.class);
                    intent.putExtra("studName", studName);
                    intent.putExtra("studRoll", studRoll);
                    intent.putExtra("hostel", hostel);
                    intent.putExtra("roomNo", roomNo);
                    intent.putExtra("photo", photo);
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "No result found!\n" + e, Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "error response " + error, Toast.LENGTH_SHORT).show();

            }
        });
        queue.add(jsonObjReq);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

}

