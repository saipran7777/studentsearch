package com.bignerdranch.android.studentsinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by admin on 27-10-2016.
 */
public class StudentDetailsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("Student Info");
        toolbar.setTitleTextColor(getResources().getColor(R.color.cardview_light_background));

        TextView name = (TextView) findViewById(R.id.student_name);
        TextView rollno = (TextView) findViewById(R.id.student_roll_no);
        TextView address = (TextView) findViewById(R.id.student_address);
        TextView mail = (TextView) findViewById(R.id.student_email_id);
        ImageView photo = (ImageView) findViewById(R.id.student_photo);

        Intent intent = getIntent();
        name.setText(intent.getStringExtra("studName"));
        String roll = intent.getStringExtra("studRoll");
        rollno.setText(roll);
        roll = roll.toLowerCase();
        address.setText(intent.getStringExtra("hostel") + ", " + intent.getStringExtra("roomNo"));
        String smail = roll + "@smail.iitm.ac.in";
        mail.setText(smail);
        Picasso.with(this)
                .load(intent.getStringExtra("photo"))
                .placeholder(R.drawable.ic_person_black_24dp)
                .into(photo);

    }  //Setting up back button

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
