package com.harsh.dentalcare;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    DBHelper(@Nullable Context context){
        super(context, "dental.db", null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table patient(reg integer primary key,name text,gender char(1),age tinyint,mobile text,email text,address text,ref text)");
        db.execSQL("create table doctor(reg integer primary key,name text,gender char(1),age tinyint,mobile text,email text,address text)");
        db.execSQL("create table treatment(reciept integer primary key,preg integer,dreg integer,date integer,tooth tinyint,diagnosis text,advise text,tplan text,workdone text,estimate text,received text)");
        db.execSQL("create table payment(reciept integer primary key,recieve text)");
        db.execSQL("create table chairs(cid integer primary key not null,name text)");
        db.execSQL("create table appointment(date integer primary key not null,pid integer,pnm text,did integer,dnm text,cid integer,cnm text)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    void deleteChair(long id){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("delete from chairs where cid="+id);
    }
    void addChair(String s) {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("insert into chairs(name) values('"+s+"')");
    }
    void addAppointment(long date,long pid,String pname,long did,String dname,long cid,String cname){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("create table if not exists appointment(date integer primary key not null,pid integer,pnm text,did integer,dnm text,cid integer,cnm text)");
        db.execSQL("insert into appointment values("+date+","+pid+",'"+pname+"',"+did+",'"+dname+"',"+cid+",'"+cname+"')");
    }
    List<Map<String,Object>> getAppointment(){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("create table if not exists appointment(date integer primary key not null,pid integer,pnm text,did integer,dnm text,cid integer,cnm text)");
        Cursor cursor=db.rawQuery("select * from appointment",null);
        ArrayList<Map<String,Object>>aps=new ArrayList<>();
        int dt=cursor.getColumnIndex("date");
        int pi=cursor.getColumnIndex("pid");
        int pn=cursor.getColumnIndex("pnm");
        int di=cursor.getColumnIndex("did");
        int dn=cursor.getColumnIndex("dnm");
        int ci=cursor.getColumnIndex("cid");
        int cn=cursor.getColumnIndex("cnm");
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Calendar calendar=Calendar.getInstance();
        if (cursor.moveToFirst())
            while(!cursor.isAfterLast()){
                Map<String,Object>ro=new HashMap<>();
                calendar.setTimeInMillis(cursor.getLong(dt));
                ro.put("date",dateFormat.format(calendar.getTime()));
                ro.put("pid",cursor.getLong(pi));
                ro.put("pname",cursor.getString(pn));
                ro.put("did",cursor.getLong(di));
                ro.put("dname",cursor.getString(dn));
                ro.put("cid",cursor.getLong(ci));
                ro.put("cname",cursor.getString(cn));
                aps.add(ro);
                cursor.moveToNext();
            }
        cursor.close();
        return aps;
    }
    HashMap<Integer, String> getChairs(){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("create table if not exists chairs(cid integer primary key not null,name text)");
        Cursor cursor=db.rawQuery("select * from chairs",null);
        HashMap<Integer,String>chairs=new HashMap<>();
        if(cursor.getCount()==0){
            db.execSQL("insert into chairs values(1,'chair 1')");
            chairs.put(1,"chair 1");
        }else{
            int cid=cursor.getColumnIndex("cid");
            int name=cursor.getColumnIndex("name");
            if (cursor.moveToFirst())
            while(!cursor.isAfterLast()){
                int id=cursor.getInt(cid);
                String cnm=cursor.getString(name);
                chairs.put(id,cnm);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return chairs;
    }
    void addPatient(long reg,String name,String gndr,String age,String mob,String em,String addr,String ref){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("insert into patient values("+reg+",'"+name+"','"+gndr.charAt(0)+"',"+Integer.parseInt(age)+",'"+mob+"','"+em+"','"+addr+"','"+ref+"')");
    }
    ArrayList<Long> getPatientRegs(){
        ArrayList<Long>regs=new ArrayList<>();
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select reg from patient",null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                regs.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return regs;
    }
    ArrayList<Patient> getPatients(){
        ArrayList<Patient>list=new ArrayList<>();
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from patient",null);
        int regi=cursor.getColumnIndex("reg");
        int nm=cursor.getColumnIndex("name");
        int agei=cursor.getColumnIndex("age");
        int gnd=cursor.getColumnIndex("gender");
        int mo=cursor.getColumnIndex("mobile");
        int em=cursor.getColumnIndex("email");
        int ad=cursor.getColumnIndex("address");
        int refi=cursor.getColumnIndex("ref");
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Patient p=new Patient();
                p.reg=cursor.getLong(regi);
                p.name=cursor.getString(nm);
                p.age=cursor.getShort(agei);
                p.gender=cursor.getString(gnd).charAt(0);
                p.mobile=cursor.getString(mo);
                p.email=cursor.getString(em);
                p.address=cursor.getString(ad);
                p.ref=cursor.getString(refi);
                list.add(p);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }
    void addDoctor(long reg,String name,String gndr,String age,String mob,String em,String addr){
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("insert into doctor values("+reg+",'"+name+"','"+gndr.charAt(0)+"',"+Integer.parseInt(age)+",'"+mob+"','"+em+"','"+addr+"')");
    }
    ArrayList<Long> getDoctorRegs(){
        ArrayList<Long>regs=new ArrayList<>();
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select reg from doctor",null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                regs.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return regs;
    }
    ArrayList<Doctor> getDoctors(){
        ArrayList<Doctor>list=new ArrayList<>();
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor=db.rawQuery("select * from doctor",null);
        int regi=cursor.getColumnIndex("reg");
        int nm=cursor.getColumnIndex("name");
        int agei=cursor.getColumnIndex("age");
        int gnd=cursor.getColumnIndex("gender");
        int mo=cursor.getColumnIndex("mobile");
        int em=cursor.getColumnIndex("email");
        int ad=cursor.getColumnIndex("address");
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                Doctor p=new Doctor();
                p.reg=cursor.getLong(regi);
                p.name=cursor.getString(nm);
                p.age=cursor.getShort(agei);
                p.gender=cursor.getString(gnd).charAt(0);
                p.mobile=cursor.getString(mo);
                p.email=cursor.getString(em);
                p.address=cursor.getString(ad);
                list.add(p);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }

    void addTreatment(long pid,long did,long date,ArrayList<Treatment>data){
        SQLiteDatabase db=getWritableDatabase();
        for (Treatment t:data)
            db.execSQL("insert into treatment values("+t.reciept+","+pid+","+did+","+date+","+t.tooth+",'"+t.disgnosis+"','"+t.advise+"','"+t.tplan+"','"+t.workdone+"','"+t.estimate+"','"+t.recieved+"')");
    }
    ArrayList<HashMap<String,Object>> getTreatment(){
        ArrayList<HashMap<String,Object>>list=new ArrayList<>();
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("create table if not exists treatment(reciept integer primary key,preg integer,dreg integer,date integer,tooth tinyint,diagnosis text,advise text,tplan text,workdone text,estimate text,received text)");
        Cursor cursor=db.rawQuery("select treatment.*,patient.name as pname,doctor.name as dname from (treatment left join patient on treatment.preg=patient.reg) left join doctor on treatment.dreg=doctor.reg",null);
        int reciept=cursor.getColumnIndex("reciept");
        int preg=cursor.getColumnIndex("preg");
        int dreg=cursor.getColumnIndex("dreg");
        int date=cursor.getColumnIndex("date");
        int tooth=cursor.getColumnIndex("tooth");
        int diagnosis=cursor.getColumnIndex("diagnosis");
        int advise=cursor.getColumnIndex("advise");
        int tplan=cursor.getColumnIndex("tplan");
        int workdone=cursor.getColumnIndex("workdone");
        int estimate=cursor.getColumnIndex("estimate");
        int received=cursor.getColumnIndex("received");
        int pnm=cursor.getColumnIndex("pname");
        int dnm=cursor.getColumnIndex("dname");
        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                HashMap<String,Object>row=new HashMap<>();
                row.put("reciept",cursor.getLong(reciept));
                row.put("preg",cursor.getLong(preg));
                row.put("dreg",cursor.getLong(dreg));
                row.put("pnm",cursor.getString(pnm));
                row.put("dnm",cursor.getString(dnm));
                row.put("tooth",cursor.getInt(tooth));
                row.put("date",cursor.getLong(date));
                row.put("diagnosis",cursor.getString(diagnosis));
                row.put("workdone",cursor.getString(workdone));
                row.put("tplan",cursor.getString(tplan));
                row.put("advise",cursor.getString(advise));
                row.put("estimate",Float.parseFloat(cursor.getString(estimate)));
                row.put("received",Float.parseFloat(cursor.getString(received)));
                list.add(row);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return list;
    }
    ArrayList<Long> getReciepts() {
        ArrayList<Long>rec=new ArrayList<>();
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("create table if not exists treatment(reciept integer primary key,preg integer,dreg integer,date integer,tooth tinyint,diagnosis text,advise text,tplan text,workdone text,estimate text,received text)");
        db.execSQL("create table if not exists payment(reciept integer primary key,recieve text)");
        Cursor cursor=db.rawQuery("select reciept from treatment",null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                rec.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        cursor=db.rawQuery("select reciept from payment",null);
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                rec.add(cursor.getLong(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return rec;
    }
}