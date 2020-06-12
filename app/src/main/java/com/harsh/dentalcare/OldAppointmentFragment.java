package com.harsh.dentalcare;

import android.content.Context;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OldAppointmentFragment extends Fragment {
    TableLayout appointmentTable;
    List<Map<String, Object>> appointments;
    Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        context=getContext();
        View view=inflater.inflate(R.layout.fragment_old_appointment, container, false);

        appointmentTable=view.findViewById(R.id.appointment_table);
        DBHelper db=new DBHelper(context);
        appointments=db.getAppointment();

        ArrayList<Pair<String,String>> titles=new ArrayList<>();
        titles.add(new Pair<>("Date and time","date"));
        titles.add(new Pair<>("Patient","pname"));
        titles.add(new Pair<>("Doctor","dname"));
        titles.add(new Pair<>("Chair","cname"));
        TableAdapter tableAdapter=new TableAdapter(context,titles,appointments,appointmentTable,R.layout.table_head_cell,R.layout.table_cell);

        return view;
    }
}
