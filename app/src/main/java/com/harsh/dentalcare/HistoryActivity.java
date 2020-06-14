package com.harsh.dentalcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.util.Pair;

import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {
    AppCompatAutoCompleteTextView patientName;
    ArrayList<Patient> patients;
    Patient selectedPatient;
    TableLayout historyTable;
    List<Map<String,Object>> treatments;
    List<Map<String,Object>> filtered=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        patientName=findViewById(R.id.patient_name);
        historyTable=findViewById(R.id.treatment_history_table);

        DBHelper db=new DBHelper(this);
        patients=db.getPatients();
        final List<Map<String,Object>> treatments=db.getTreatment();
        ArrayList<Pair<String,String>>titles=new ArrayList<>();
        Log.e("data",treatments.toString());
        titles.add(new Pair<>("Date","date"));
        titles.add(new Pair<>("Patient","pnm"));
        titles.add(new Pair<>("Doctor","dnm"));
        titles.add(new Pair<>("Tooth","tooth"));
        titles.add(new Pair<>("Diagnosis","diagnosis"));
        titles.add(new Pair<>("Treatment Plan","tplan"));
        titles.add(new Pair<>("Work done","workdone"));
        titles.add(new Pair<>("Advise","advise"));
        final TableAdapter adapter=new TableAdapter(this,titles,filtered,historyTable,R.layout.table_head_cell,R.layout.table_cell);

        SharedPreferences preferences=getSharedPreferences("myprefs",MODE_PRIVATE);
        long pat=preferences.getLong("pat",-1);
        if (pat!=-1){
            for (Patient d:patients)
                if (d.reg==pat){
                    selectedPatient=d;
                    patientName.setText(d.name);
                    break;
                }
            if (selectedPatient!=null)
            for (Map<String,Object>m:treatments)
                if (m.get("pnm").equals(selectedPatient.name)) {
                    filtered.add(m);
                }
            adapter.reload();
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
                filtered.clear();
                for (Map<String,Object>m:treatments)
                    if (m.get("pnm").equals(selectedPatient.name)){
                        filtered.add(m);
                    }
                adapter.reload();
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
