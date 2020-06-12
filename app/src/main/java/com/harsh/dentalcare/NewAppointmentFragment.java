package com.harsh.dentalcare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class NewAppointmentFragment extends Fragment {
    private AppCompatAutoCompleteTextView patientName,doctorName;
    MaterialButton addbtn;
    ArrayList<Doctor> doctors;
    ArrayList<Patient>patients;
    private Doctor selectedDoctor;
    private Patient selectedPatient;
    AppCompatSpinner chairselect;
    private HashMap<Integer,String> chairs=new HashMap<>();
    ImageButton editChairs;
    private DBHelper db;
    private ChairAdapter adapter;
    private int selectedChair;
    private Context context;
    private TextView dateTxt,timeTxt;
    private Calendar calendar;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm a");
    RecyclerView timelistview;
    String selectedTime="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        context=getContext();
        View view=inflater.inflate(R.layout.fragment_new_appointment, container, false);

        patientName=view.findViewById(R.id.treatment_patient_name);
        doctorName=view.findViewById(R.id.treatment_doctor_name);
        editChairs=view.findViewById(R.id.edit_chairs);
        addbtn=view.findViewById(R.id.add_appointment_button);
        chairselect=view.findViewById(R.id.chair_select);
        dateTxt=view.findViewById(R.id.date_txt);
        timeTxt=view.findViewById(R.id.time_txt);
        timelistview=view.findViewById(R.id.pretime_list);

        db=new DBHelper(context);
        doctors=db.getDoctors();
        patients=db.getPatients();
        chairs=db.getChairs();
        adapter=new ChairAdapter();
        calendar=Calendar.getInstance();
        chairselect.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        timelistview.setLayoutManager(new GridLayoutManager(context,2));
        ArrayList<String>times=new ArrayList<>();
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY,8);
        cal.set(Calendar.MINUTE,0);
        for (int i=0;i<26;i++,cal.add(Calendar.MINUTE,30))
            times.add(timeFormat.format(cal.getTime()));
        timelistview.setAdapter(new TimeAdapter(times));

        final SharedPreferences preferences=context.getSharedPreferences("myprefs",Context.MODE_PRIVATE);
        long doc=preferences.getLong("doc",-1);
        if (doc!=-1){
            for (Doctor d:doctors)
                if(d.reg==doc){
                    selectedDoctor=d;
                    doctorName.setText(d.name);
                    break;
                }
        }
        dateTxt.setText(dateFormat.format(calendar.getTime()));
        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog=new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR,year);
                        calendar.set(Calendar.MONTH,month);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        dateTxt.setText(dateFormat.format(calendar.getTime()));
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                dialog.show();
            }
        });
        timeTxt.setText(timeFormat.format(calendar.getTime()));
        timeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog dialog=new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        timeTxt.setText(timeFormat.format(calendar.getTime()));
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                dialog.show();
            }
        });
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AlertDialog.Builder builder=new AlertDialog.Builder(context)
                        .setTitle("Set Appointment ?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.addAppointment(calendar.getTimeInMillis(),
                                        selectedPatient.reg,selectedPatient.name,
                                        selectedDoctor.reg,selectedDoctor.name,
                                        selectedChair,chairs.get(selectedChair));
                                Toast.makeText(context,"Appointment is set.",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel",null);
                View view=LayoutInflater.from(context).inflate(R.layout.appointment_dialog,null,false);
                ((TextView)view.findViewById(R.id.patient)).setText(selectedPatient.name);
                ((TextView)view.findViewById(R.id.doctor)).setText(selectedDoctor.name);
                ((TextView)view.findViewById(R.id.chair)).setText(chairs.get(selectedChair));
                ((TextView)view.findViewById(R.id.date)).setText(dateFormat.format(calendar.getTime()));
                ((TextView)view.findViewById(R.id.time)).setText(timeFormat.format(calendar.getTime()));
                builder.setView(view);
                builder.create().show();
            }
        });
        chairselect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedChair=adapter.index.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        final SuggestionAdapter<Doctor> docAdapter= new SuggestionAdapter<>(doctors);
        doctorName.setAdapter(docAdapter);
        doctorName.setThreshold(1);
        doctorName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDoctor=docAdapter.list.get(position);
                preferences.edit().putLong("doc",selectedDoctor.reg).apply();
            }
        });
        final SuggestionAdapter<Patient> patAdapter= new SuggestionAdapter<>(patients);
        patientName.setAdapter(patAdapter);
        patientName.setThreshold(1);
        patientName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TypedValue typedValue=new TypedValue();
                context.getTheme().resolveAttribute(R.attr.colorPrimary,typedValue,true);
                int primary=typedValue.data;
                addbtn.getBackground().setTint(primary);
                selectedPatient=patAdapter.list.get(position);
                preferences.edit().putLong("pat",selectedPatient.reg).apply();
            }
        });
        editChairs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ChairsActivity.class);
                startActivityForResult(intent,4);
            }
        });
        return view;
    }
    class ChairAdapter extends BaseAdapter {
        @Override
        public int getCount() {return index.size();}
        @Override
        public Object getItem(int position) {return index.get(position);}
        @Override
        public long getItemId(int position) {return position;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(android.R.layout.simple_dropdown_item_1line,parent,false);
            TextView txt=convertView.findViewById(android.R.id.text1);
            txt.setText(chairs.get(index.get(position)));
            return convertView;
        }
        ArrayList<Integer>index=new ArrayList<>();
        @Override
        public void notifyDataSetChanged() {
            index=new ArrayList<>(chairs.keySet());
            super.notifyDataSetChanged();
        }
    }
    class SuggestionAdapter<T extends Person> extends BaseAdapter implements Filterable {
        LayoutInflater inflater;
        ArrayList<T>originalList;
        ArrayList<T>list;
        SuggestionAdapter(ArrayList<T>list){
            inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    class TimeAdapter extends RecyclerView.Adapter<TimeAdapter.TimeViewHolder>{
        ArrayList<String>times;
        TimeAdapter(ArrayList<String>times){
            this.times=times;
        }
        RadioButton last=null;
        @NonNull
        @Override
        public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view=inflater.inflate(R.layout.time_radio,parent,false);
            return new TimeViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull final TimeViewHolder holder, final int position) {
            holder.btn.setText(times.get(position));
            holder.btn.setChecked(times.get(position).equals(selectedTime));
            holder.btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked){
                        selectedTime=buttonView.getText().toString();
                        timeTxt.setText(selectedTime);
                        try {
                            Date date=timeFormat.parse(selectedTime);
                            calendar.set(Calendar.HOUR_OF_DAY,date.getHours());
                            calendar.set(Calendar.MINUTE,date.getMinutes());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (last!=null)
                            last.setChecked(false);
                        last=holder.btn;
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            return times.size();
        }
        class TimeViewHolder extends RecyclerView.ViewHolder{
            RadioButton btn;
            TimeViewHolder(@NonNull View itemView) {
                super(itemView);
                btn=itemView.findViewById(R.id.radiobtn);
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==4){
            chairs=db.getChairs();
            adapter.notifyDataSetChanged();
        }
    }
}
