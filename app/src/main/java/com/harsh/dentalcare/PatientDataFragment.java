package com.harsh.dentalcare;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PatientDataFragment extends Fragment {
    Context context;
    TableLayout tableLayout;
    ArrayList<Patient>patients;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_patient_data, container, false);
        context=getContext();
        tableLayout=view.findViewById(R.id.patient_data_table);
        final EditText search=view.findViewById(R.id.patient_search);

        DBHelper db=new DBHelper(context);
        patients=db.getPatients();
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String text=search.getText().toString();
                filterTable(text);
            }
        });
        filterTable("");
        return view;
    }
    private void filterTable(String text) {
        TableRow row;
        TextView txt;
        tableLayout.removeAllViews();
        row=new TableRow(context);
        txt=new TextView(context);
        txt.setText("Reg No.");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        txt=new TextView(context);
        txt.setText("Name");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        txt=new TextView(context);
        txt.setText("Age");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        txt=new TextView(context);
        txt.setText("Gender");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        txt=new TextView(context);
        txt.setText("Mobile");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        txt=new TextView(context);
        txt.setText("Address");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        txt=new TextView(context);
        txt.setText("Email");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        txt=new TextView(context);
        txt.setText("Reffered");
        txt.setPadding(10,10,10,10);
        txt.setTextColor(Color.WHITE);
        txt.setBackground(context.getDrawable(R.drawable.table_header_cell));
        row.addView(txt);
        tableLayout.addView(row);
        for(Patient p:patients){
            if(!text.isEmpty() && !p.starts(text))continue;
            row=new TableRow(context);
            txt=new TextView(context);
            txt.setText(""+p.reg);
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            txt=new TextView(context);
            txt.setText(p.name);
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            txt=new TextView(context);
            txt.setText(""+p.age);
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            txt=new TextView(context);
            txt.setText(p.gender=='F'?"Female":"Male");
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            txt=new TextView(context);
            txt.setText(p.mobile);
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            txt=new TextView(context);
            txt.setText(p.address);
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            txt=new TextView(context);
            txt.setText(p.email);
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            txt=new TextView(context);
            txt.setText(p.ref);
            txt.setPadding(10,10,10,10);
            txt.setBackground(context.getDrawable(R.drawable.table_cell));
            row.addView(txt);
            tableLayout.addView(row);
        }
    }
}
