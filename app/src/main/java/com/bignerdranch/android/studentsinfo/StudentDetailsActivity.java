package com.bignerdranch.android.studentsinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by admin on 27-10-2016.
 */
public class StudentDetailsActivity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

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
                .into(photo);

    }
}
