package com.harsh.dentalcare;

import androidx.annotation.NonNull;

public class Person{
    String name,mobile,email,address;
    int age;
    long reg;
    char gender;
    @NonNull
    @Override
    public String toString() {
        return name;
    }
    public boolean starts(String prefix){
        boolean result=false;
        result|=(""+reg).startsWith(prefix);
        result|=name.startsWith(prefix);
        result|=mobile.startsWith(prefix);
        result|=email.startsWith(prefix);
        result|=address.startsWith(prefix);
        return result;
    }
}
