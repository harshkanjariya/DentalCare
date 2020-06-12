package com.harsh.dentalcare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Date;

public class AddTreatmentActivity extends AppCompatActivity {
    TableLayout table;
    LinearLayout imgtable;
    long pid,did;
    ArrayList<Treatment>treatments=new ArrayList<>();
    ArrayList<Long>reciepts;
    long lastrec;
    ArrayList<EditText>recieptViews=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_treatment);

        Intent intent=getIntent();
        pid= intent.getLongExtra("patient",-1);
        did= intent.getLongExtra("doctor",-1);
        imgtable=findViewById(R.id.imgtable);
        table=findViewById(R.id.teethTable);

        final SharedPreferences preferences=getSharedPreferences("myprefs",MODE_PRIVATE);
        lastrec=preferences.getLong("lastrec",0);
        setupImages();
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AddTreatmentActivity.this)
                        .setTitle(null)
                        .setMessage("Are you sure you want to cancel ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No",null);
                builder.create().show();
            }
        });
        final DBHelper db=new DBHelper(this);
        reciepts=db.getReciepts();
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(AddTreatmentActivity.this)
                        .setTitle(null)
                        .setMessage("Are you sure you want to Save ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which){
                                long newrec=0;
                                for (Treatment t:treatments)
                                    if (reciepts.contains(t.reciept)){
                                        Snackbar.make(findViewById(R.id.addtreatment_layout), "Reciept No " + t.reciept + " already exists", BaseTransientBottomBar.LENGTH_SHORT).show();
                                        return;
                                    }else if (newrec<t.reciept)
                                        newrec=t.reciept;
                                preferences.edit().putLong("lastrec",newrec).apply();
                                db.addTreatment(pid,did,new Date().getTime(),treatments);
                                finish();
                            }
                        })
                        .setNegativeButton("No",null);
                builder.create().show();
            }
        });
    }
    public static float pixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    void setupImages(){
        ImageView img;
        LinearLayout row;
        CheckBox check;

        row=new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        for (int i=0;i<8;i++){
            int resid=getResources().getIdentifier("@drawable/t1"+(i+1),null,getPackageName());
            img=new ImageView(this);
            img.setImageDrawable(getDrawable(resid));
            img.setAdjustViewBounds(true);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight=1;
            img.setLayoutParams(params);
            row.addView(img);
        }
        imgtable.addView(row);
        row=new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        for (int i=0;i<8;i++){
            check=new CheckBox(this);
            check.setOnCheckedChangeListener(new TeethSelect());
            check.setText("1"+(8-i));
            check.setPadding(-10,0,10,0);
            check.setBackground(getDrawable(R.drawable.table_cell));
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight=1;
            check.setLayoutParams(params);
            row.addView(check);
        }
        imgtable.addView(row);

        row=new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        for (int i=0;i<8;i++){
            int resid=getResources().getIdentifier("@drawable/t2"+(i+1),null,getPackageName());
            img=new ImageView(this);
            img.setImageDrawable(getDrawable(resid));
            img.setAdjustViewBounds(true);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight=1;
            img.setLayoutParams(params);
            row.addView(img);
        }
        imgtable.addView(row);
        row=new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        for (int i=0;i<8;i++){
            check=new CheckBox(this);
            check.setOnCheckedChangeListener(new TeethSelect());
            check.setText("2"+(8-i));
            check.setPadding(-10,0,10,0);
            check.setBackground(getDrawable(R.drawable.table_cell));
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight=1;
            check.setLayoutParams(params);
            row.addView(check);
        }
        imgtable.addView(row);
    }
    class TeethSelect implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            String s=buttonView.getText().toString();
            if (isChecked){
                Treatment t=new Treatment();
                t.tooth= Short.parseShort(s);
                treatments.add(t);
            }else{
                Treatment found=null;
                for (Treatment t:treatments)
                    if (t.tooth==Short.parseShort(s)){
                        found=t;
                        break;
                    }
                if (found!=null)
                    treatments.remove(found);
            }
            filterTable();
        }
    }
    class ImageAdapter extends BaseAdapter{
        int []ids={R.drawable.t11,R.drawable.t12,
                R.drawable.t13,R.drawable.t14,
                R.drawable.t15,R.drawable.t16,
                R.drawable.t17,R.drawable.t18,
                R.drawable.t21,R.drawable.t22,
                R.drawable.t23,R.drawable.t24,
                R.drawable.t25,R.drawable.t26,
                R.drawable.t27,R.drawable.t28};
        @Override
        public int getCount() {return ids.length;}
        @Override
        public Object getItem(int position) {return null;}
        @Override
        public long getItemId(int position) {return 0;}
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView img=new ImageView(AddTreatmentActivity.this);
            img.setImageDrawable(getDrawable(ids[position]));
            img.setAdjustViewBounds(true);
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            return img;
        }
    }
    void filterTable(){
        TableRow row;
        TextView txt;
        EditText edt;
        table.removeAllViews();
        recieptViews.clear();

        row=new TableRow(this);
        String []headers={"Tooth","Diagnosis","Advise","Treatment Plan","Work Done","Estimate","Received","Balance","Reciept No."};
        for (int i=0;i<headers.length;i++){
            txt=new TextView(this);
            txt.setText(headers[i]);
            txt.setPadding(10,10,10,10);
            txt.setTextColor(Color.WHITE);
            txt.setBackground(getDrawable(R.drawable.table_header_cell));
            row.addView(txt);
        }
        table.addView(row);

        TableRow.LayoutParams params;
        for(final Treatment t:treatments){
            row=new TableRow(this);
            txt=new TextView(this);
            txt.setText(""+t.tooth);
            txt.setTextColor(Color.BLACK);
            txt.setTextSize(18);
            txt.setPadding(10,10,10,10);
            txt.setBackground(getDrawable(R.drawable.table_cell));
            row.addView(txt);
            edt=new EditText(this);
            edt.setText(t.disgnosis);
            edt.addTextChangedListener(new EditConnect(t,"disgnosis"));
            edt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            edt.setInputType(InputType.TYPE_CLASS_TEXT);
            params=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            edt.setLayoutParams(params);
            edt.setPadding(10,10,10,10);
            edt.setBackground(getDrawable(R.drawable.table_cell));
            row.addView(edt);
            edt=new EditText(this);
            edt.setText(t.advise);
            edt.addTextChangedListener(new EditConnect(t,"advise"));
            edt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            edt.setInputType(InputType.TYPE_CLASS_TEXT);
            params=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            edt.setLayoutParams(params);
            edt.setPadding(10,10,10,10);
            edt.setBackground(getDrawable(R.drawable.table_cell));
            row.addView(edt);

            edt=new EditText(this);
            edt.setText(t.tplan);
            edt.addTextChangedListener(new EditConnect(t,"tplan"));
            edt.setInputType(InputType.TYPE_CLASS_TEXT);
            edt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            params=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            edt.setLayoutParams(params);
            edt.setPadding(10,10,10,10);
            edt.setBackground(getDrawable(R.drawable.table_cell));
            row.addView(edt);

            edt=new EditText(this);
            edt.setText(t.workdone);
            edt.addTextChangedListener(new EditConnect(t,"workdone"));
            params=new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            edt.setLayoutParams(params);
            edt.setInputType(InputType.TYPE_CLASS_TEXT);
            edt.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            edt.setPadding(10,10,10,10);
            edt.setBackground(getDrawable(R.drawable.table_cell));
            row.addView(edt);

            final EditText estm=new EditText(this);
            estm.setText(String.format(t.estimate%1==0?"%.0f":"%.2f",t.estimate));
            estm.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            estm.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            estm.setPadding(10,10,10,10);
            estm.setBackground(getDrawable(R.drawable.table_cell));
            row.addView(estm);
            final EditText rcv=new EditText(this);
            rcv.setText(String.format(t.recieved%1==0?"%.0f":"%.2f",t.recieved));
            rcv.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            rcv.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            rcv.setPadding(10,10,10,10);
            rcv.setBackground(getDrawable(R.drawable.table_cell));
            rcv.setFilters(new InputMinMaxFilter[]{new InputMinMaxFilter(0, t.estimate)});
            row.addView(rcv);
            final TextView bal=new TextView(this);
            float dif=t.estimate-t.recieved;
            bal.setText(String.format(dif%1==0?"%.0f":"%.2f",dif));
            bal.setTextSize(18);
            bal.setTextColor(Color.BLACK);
            bal.setPadding(10,10,10,10);
            bal.setBackground(getDrawable(R.drawable.table_cell));
            row.addView(bal);
            estm.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    String str=estm.getText().toString();
                    if (!str.isEmpty()){
                        t.estimate=Float.parseFloat(estm.getText().toString());
                        float dif=t.estimate-t.recieved;
                        bal.setText(String.format(dif%1==0?"%.0f":"%.2f",dif));
                        rcv.setFilters(new InputMinMaxFilter[]{new InputMinMaxFilter(0, t.estimate)});
                    }
                }
            });
            edt=new EditText(this);
            recieptViews.add(edt);
            edt.setImeOptions(treatments.indexOf(t)==treatments.size()-1?EditorInfo.IME_ACTION_DONE:EditorInfo.IME_ACTION_NEXT);
            edt.setInputType(InputType.TYPE_CLASS_NUMBER);
            edt.addTextChangedListener(new EditConnect(t,"reciept"));
            edt.setPadding(10,10,10,10);
            edt.setBackground(getDrawable(R.drawable.table_cell));
            edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Log.e("id",""+EditorInfo.IME_ACTION_NEXT);
                    if (actionId==EditorInfo.IME_ACTION_NEXT){
                        if (reciepts.contains(t.reciept)) {
                            Snackbar.make(findViewById(R.id.addtreatment_layout), "Reciept No " + t.reciept + " already exists", BaseTransientBottomBar.LENGTH_SHORT).show();
                            return true;
                        }
                    }
                    return false;
                }
            });
            row.addView(edt);
            rcv.addTextChangedListener(new TextWatcher() {
                boolean before=false;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    before=s.toString().isEmpty();
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    String str=rcv.getText().toString();
                    if(str.isEmpty())str="0";
                    t.recieved=Float.parseFloat(str);
                    if (str.equals("0"))loadReciepts();
                    else if (before){
                        before=false;
                        loadReciepts();
                    }
                    float dif=t.estimate-t.recieved;
                    bal.setText(String.format(dif%1==0?"%.0f":"%.2f",dif));
                }
            });
            table.addView(row);
        }
    }
    void loadReciepts(){
        final long[] resno = {lastrec};
        for (int i=0;i<treatments.size();i++){
            EditText ed=recieptViews.get(i);
            if (ed!=null){
                float r=treatments.get(i).recieved;
                if (r>0){
                    resno[0]++;
                    while(reciepts.contains(resno[0]))
                        resno[0]++;
                    treatments.get(i).reciept=resno[0];
                    ed.setText(""+resno[0]);
                }else{
                    ed.setText("");
                }
            }
        }
    }
    static class InputMinMaxFilter implements InputFilter{
        private double min,max;
        InputMinMaxFilter(double min,double max){
            this.min=min;
            this.max=max;
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try{
                int input= Integer.parseInt(dest.toString()+source.toString());
                if (isLinear(min,input,max))
                    return null;
            }catch (Exception ignored){}
            return "";
        }
        private boolean isLinear(double a,double b,double c){
            return b>=a && c>=b;
        }
    }
    static class EditConnect implements TextWatcher{
        String type;
        Treatment t;
        EditConnect(Treatment t, String tp){
            this.t=t;
            type=tp;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count){}
        @Override
        public void afterTextChanged(Editable s){
            switch (type) {
                case "tplan":
                    t.tplan = s.toString();
                    break;
                case "diagnosis":
                    t.disgnosis = s.toString();
                    break;
                case "workdone":
                    t.workdone = s.toString();
                    break;
                case "advise":
                    t.advise = s.toString();
                    break;
                case "reciept":
                    if(!s.toString().isEmpty())
                    t.reciept=Long.parseLong(s.toString());
                    break;
            }
        }
    }
}
