package com.bignerdranch.android.studentsinfo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class nameFragment extends Fragment {

    ListView lvSuggestion;
    ArrayAdapter<String> adapter;
    ArrayList<String> listSuggestion = new ArrayList<>(25);
    EditText etSearch;
    ProgressBar progressSearch;
    Context context;
    FrameLayout frameLayout;

    public nameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_name, container, false);
        context = view.getContext();
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_layout_name);
        progressSearch = (ProgressBar) view.findViewById(R.id.pb_search);
        etSearch = (EditText) view.findViewById(R.id.et_search_name);
        lvSuggestion = (ListView) view.findViewById(R.id.lv_suggestion);
        adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_list_item_1,
                listSuggestion);
        lvSuggestion.setAdapter(adapter);

        lvSuggestion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) parent.getItemAtPosition(position);
                goToDetails(name);
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (listSuggestion.size() > 5) {
                    listSuggestion.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) showSuggestion(s.toString());
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    final Editable selection = etSearch.getText();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                    showSuggestion(selection.toString());
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    private void showSuggestion(String query) {

        progressSearch.setVisibility(View.VISIBLE);
        final RequestQueue queue = Volley.newRequestQueue(context);

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
                    int i;
                    String studName;

                    for (i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        studName = jsonObject.getString("fullname");
                        if (!listSuggestion.contains(studName))
                            listSuggestion.add(studName);//+", "+studRoll
                    }
                    adapter.notifyDataSetChanged();
                    progressSearch.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error loading results, try again later...", Toast.LENGTH_SHORT).show();
                    listSuggestion.clear();
                    adapter.notifyDataSetChanged();
                    progressSearch.setVisibility(View.GONE);

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(context, "Couldn't connect to server.", Toast.LENGTH_SHORT).show();
                listSuggestion.clear();
                adapter.notifyDataSetChanged();
                progressSearch.setVisibility(View.GONE);
            }
        });

        queue.add(jsonObjReq);
    }

    private void goToDetails(String query) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Getting data...");
        pDialog.show();
        pDialog.setCancelable(false);

        final RequestQueue queue = Volley.newRequestQueue(context);

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
                    String studName = "Name appears here",
                            studRoll = "Roll number appears here",
                            hostel = "Hostel",
                            roomNo = "room number",
                            photo = "https://photos.iitm.ac.in//byroll.php?roll=wrongSyntax";

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        studName = jsonObject.getString("fullname");
                        studRoll = jsonObject.getString("username");
                        hostel = jsonObject.getString("hostel");
                        roomNo = jsonObject.getString("roomno");
                        photo = jsonObject.getString("url");
                    }

                    Intent intent = new Intent(context, StudentDetailsActivity.class);
                    intent.putExtra("studName", studName);
                    intent.putExtra("studRoll", studRoll);
                    intent.putExtra("hostel", hostel);
                    intent.putExtra("roomNo", roomNo);
                    intent.putExtra("photo", photo);
                    pDialog.dismiss();
                    startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                    pDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(frameLayout, "Error parsing data, try again later...", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                pDialog.dismiss();
                Snackbar snackbar = Snackbar
                        .make(frameLayout, "Couldn't connect to the server.", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
        queue.add(jsonObjReq);
    }
}
