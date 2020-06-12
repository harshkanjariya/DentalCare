package com.harsh.dentalcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ChairsActivity extends AppCompatActivity {
    RecyclerView chairList;
    HashMap<Integer,String>chairs=new HashMap<>();
    DBHelper db;
    ChairsAdapter adapter;
    Button addChair;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chairs);

        chairList=findViewById(R.id.chair_list);
        addChair=findViewById(R.id.add_chair_btn);
        db=new DBHelper(this);
        chairs=db.getChairs();

        adapter=new ChairsAdapter();
        chairList.setLayoutManager(new LinearLayoutManager(this));
        chairList.setAdapter(adapter);
        addChair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ed=findViewById(R.id.add_chair_name);
                ImageButton btn=findViewById(R.id.add_chair_ok);
                addChair.setVisibility(View.GONE);
                ed.setVisibility(View.VISIBLE);
                btn.setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.add_chair_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                EditText ed=findViewById(R.id.add_chair_name);
                String s=ed.getText().toString();
                db.addChair(s);
                ed.setVisibility(View.GONE);
                addChair.setVisibility(View.VISIBLE);
                chairs=db.getChairs();
                adapter.notifyChanges();
            }
        });
    }
    class ChairsAdapter extends RecyclerView.Adapter<ChairsAdapter.ChairViewHolder> {
        LayoutInflater inflater;
        ArrayList<Integer>index;
        ChairsAdapter(){
            inflater= (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            index=new ArrayList<>(chairs.keySet());
        }
        @NonNull
        @Override
        public ChairViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v=inflater.inflate(R.layout.single_chair,parent,false);
            return new ChairViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ChairViewHolder holder, final int position) {
            holder.name.setText(chairs.get(index.get(position)));
            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteChair(index.get(position));
                    notifyDataSetChanged();
                }
            });
        }
        @Override
        public int getItemCount() {
            return index.size();
        }
        void notifyChanges() {
            index=new ArrayList<>(chairs.keySet());
            notifyDataSetChanged();
        }
        class ChairViewHolder extends RecyclerView.ViewHolder{
            ImageButton close;
            TextView name;
            public ChairViewHolder(@NonNull View itemView) {
                super(itemView);
                close=itemView.findViewById(R.id.close_button);
                name=itemView.findViewById(R.id.name);
            }
        }
    }
}
