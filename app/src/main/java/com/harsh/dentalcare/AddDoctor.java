package com.harsh.dentalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AddDoctor extends AppCompatActivity {
    TextInputEditText registrationInput,nameInput,ageInput;
    TextInputEditText mobileInput,emailInput,adderssInput;
    RadioGroup genderInput;
    MaterialButton savebtn;
    ArrayList<Long>doctorRegs=new ArrayList<>();
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);

        Toolbar toolbar=findViewById(R.id.doctor_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.back_arrow));

        registrationInput=findViewById(R.id.registration_number_input);
        nameInput=findViewById(R.id.doctor_name_input);
        ageInput=findViewById(R.id.doctor_age_input);
        genderInput=findViewById(R.id.gender_input);
        mobileInput=findViewById(R.id.mobile_number_input);
        emailInput=findViewById(R.id.email_input);
        adderssInput=findViewById(R.id.address_input);
        savebtn=findViewById(R.id.save_button);

        final SharedPreferences preferences=getSharedPreferences("myprefs",MODE_PRIVATE);
        String lastreg=preferences.getString("lastdreg",null);
        if (lastreg!=null){
            int regno= Integer.parseInt(lastreg);
            registrationInput.setText(""+(regno+1));
            nameInput.requestFocus();
        }
        final DBHelper dbHelper=new DBHelper(this);
        doctorRegs=dbHelper.getDoctorRegs();
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reg=registrationInput.getText().toString();
                if (doctorRegs.contains(reg)) {
                    ((TextInputLayout) findViewById(R.id.registration_number_input_layout)).setError("Registration Number Already Exists!");
                    registrationInput.requestFocus();
                    return;
                }
                String nm=nameInput.getText().toString();
                if (nm.isEmpty()) {
                    ((TextInputLayout) findViewById(R.id.doctor_name_input_layout)).setError("Enter Name");
                    nameInput.requestFocus();
                    return;
                }
                String age=ageInput.getText().toString();
                String gndr=((RadioButton)findViewById(genderInput.getCheckedRadioButtonId())).getText().toString();
                String mob=mobileInput.getText().toString();
                String em=emailInput.getText().toString();
                String ad=adderssInput.getText().toString();
                dbHelper.addDoctor(Long.parseLong(reg),nm,gndr,age,mob,em,ad);
                preferences.edit().putString("lastdreg",reg).apply();
                Intent intent=new Intent();
                intent.setData(Uri.parse("New Doctor Data Saved!"));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
