package com.harsh.dentalcare;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.util.Pair;

import java.util.List;
import java.util.Map;

public class TableAdapter{
    LayoutInflater inflater;
    List<Pair<String,String>>heads;
    List<Map<String,Object>>data;
    TableLayout table;
    int headerback,cellback;
    Context ctx;
    TableAdapter(Context context, List<Pair<String, String>> titles, List<Map<String, Object>> data, TableLayout tableLayout, int headerResource, int cellResource){
        inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ctx=context;
        heads=titles;
        this.data=data;
        table=tableLayout;
        headerback=headerResource;
        cellback=cellResource;
        reload();
    }
    public int rows() {
        return data.size()<1?0:data.get(0).size();
    }
    public int columns() {
        return heads.size();
    }
    Map<String,?> rowData(int position) {
        return data.get(position);
    }
    void reload(){
        table.removeAllViews();
        TableRow row=new TableRow(ctx);
        for(Pair<String,String> s:heads){
            TextView txt= (TextView) inflater.inflate(headerback,null,false);
            txt.setText(s.first);
            row.addView(txt);
        }
        table.addView(row);
        for (int i=0;i<data.size();i++){
            row=new TableRow(ctx);
            for(Pair<String,String> s:heads){
                TextView txt= (TextView) inflater.inflate(cellback,null,false);
                txt.setText(""+data.get(i).get(s.second));
                row.addView(txt);
            }
            table.addView(row);
        }
    }
}
