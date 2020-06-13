package com.harsh.dentalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    AppCompatAutoCompleteTextView patientName;
    ArrayList<Patient> patients;
    Patient selectedPatient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        patientName=findViewById(R.id.patient_name);

        DBHelper db=new DBHelper(this);
        patients=db.getPatients();

        SharedPreferences preferences=getSharedPreferences("myprefs",MODE_PRIVATE);
        long pat=preferences.getLong("pat",-1);
        if (pat!=-1){
            for (Patient d:patients)
                if (d.reg==pat){
                    selectedPatient=d;
                    patientName.setText(d.name);
                    break;
                }
        }
        final SuggestionAdapter<Patient> patAdapter=new SuggestionAdapter<>(patients);
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
    class SuggestionAdapter<T extends Person> extends BaseAdapter implements Filterable {
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
