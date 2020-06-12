package com.harsh.dentalcare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class HomeActivity extends AppCompatActivity {
    ConstraintLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        layout=findViewById(R.id.homeLayout);
    }
    public void addoctor(View view){
        Intent intent=new Intent(this,AddDoctor.class);
        startActivityForResult(intent,4);
    }
    public void openhistory(View view){
        Intent intent=new Intent(this,HistoryActivity.class);
        startActivity(intent);
    }
    public void treatment(View view){
        Intent intent=new Intent(this,TreatmentActivity.class);
        startActivity(intent);
    }
    public void addpatient(View view){
        Intent intent=new Intent(this,AddPatient.class);
        startActivityForResult(intent,4);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==4 && resultCode==RESULT_OK){
            String msg=data.getData().toString();
            Snackbar.make(layout,msg, BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }
    public void appointments(View view) {
        Intent intent=new Intent(this,AppointmentActivity.class);
        startActivity(intent);
    }
    public void opentables(View view) {
        Intent intent=new Intent(this,TableActivity.class);
        startActivity(intent);
    }
}
