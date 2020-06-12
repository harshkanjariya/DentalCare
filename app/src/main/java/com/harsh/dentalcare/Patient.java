package com.harsh.dentalcare;

public class Patient extends Person{
    String ref;
    @Override
    public boolean starts(String prefix) {
        return super.starts(prefix)|ref.startsWith(prefix);
    }
}