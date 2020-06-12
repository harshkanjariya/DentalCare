package com.harsh.dentalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class TreatmentActivity extends AppCompatActivity {
    AppCompatAutoCompleteTextView patientName,doctorName;
    MaterialButton nextbtn;
    ArrayList<Doctor>doctors;
    ArrayList<Patient>patients;
    Doctor selectedDoctor;
    Patient selectedPatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);

        patientName=findViewById(R.id.treatment_patient_name);
        doctorName=findViewById(R.id.treatment_doctor_name);
        nextbtn=findViewById(R.id.treatment_next_btn);

        DBHelper db=new DBHelper(this);
        doctors=db.getDoctors();
        patients=db.getPatients();

        SharedPreferences preferences=getSharedPreferences("myprefs",MODE_PRIVATE);
        long doc=preferences.getLong("doc",-1);
        if (doc!=-1){
            for (Doctor d:doctors)
                if (d.reg==doc){
                    selectedDoctor=d;
                    doctorName.setText(d.name);
                    break;
                }
        }
        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if (patientName.getText().toString().isEmpty() ||
                doctorName.getText().toString().isEmpty() ||
                selectedPatient==null || selectedDoctor==null){
                    return;
                }
                Intent intent=new Intent(TreatmentActivity.this,AddTreatmentActivity.class);
                intent.putExtra("patient",selectedPatient.reg);
                intent.putExtra("doctor",selectedDoctor.reg);
                startActivity(intent);
            }
        });
        final SuggestionAdapter<Doctor>docAdapter=new SuggestionAdapter<>(doctors);
        doctorName.setAdapter(docAdapter);
        doctorName.setThreshold(1);
        doctorName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDoctor=docAdapter.list.get(position);
                SharedPreferences preferences=getSharedPreferences("myprefs",MODE_PRIVATE);
                preferences.edit().putLong("doc",selectedDoctor.reg).apply();
            }
        });
        final SuggestionAdapter<Patient>patAdapter=new SuggestionAdapter<>(patients);
        patientName.setAdapter(patAdapter);
        patientName.setThreshold(1);
        patientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPatient=patAdapter.list.get(position);
                SharedPreferences preferences=getSharedPreferences("myprefs",MODE_PRIVATE);
                preferences.edit().putLong("pat",selectedPatient.reg).apply();
            }
        });
    }
    class SuggestionAdapter<T extends Person> extends BaseAdapter implements Filterable{
        LayoutInflater inflater;
        ArrayList<T>originalList;
        ArrayList<T>list;
        SuggestionAdapter(ArrayList<T>list){
            inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            this.originalList=list;
        }
        @Override
        public int getCount() {return list.size(); }
        @Override
        public Object getItem(int position) {return list.get(position);}
        @Override
        public long getItemId(int position) {return position;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView=inflater.inflate(R.layout.single_name_layout,parent,false);
            TextView t=convertView.findViewById(R.id.name);
            t.setText(list.get(position).name);
            return convertView;
        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint){
                    ArrayList<T>temp=new ArrayList<>();
                    if (constraint==null){
                        temp=new ArrayList<>(originalList);
                    }else {
                        for (T t : originalList)
                            if (t.name.startsWith(constraint.toString()))
                                temp.add(t);
                    }
                    FilterResults results=new FilterResults();
                    results.values=temp;
                    results.count=temp.size();
                    return results;
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results){
                    ArrayList<T>res= (ArrayList<T>) results.values;
                    list=res;
                    if(results.count>0){
                        notifyDataSetChanged();
                    }else{
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
    }
}
