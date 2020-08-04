package com.lifeknight.modbase.mod;

import com.lifeknight.modbase.variables.LifeKnightNumber;
import com.lifeknight.modbase.variables.LifeKnightObject;
import com.lifeknight.modbase.variables.LifeKnightString;

import java.text.SimpleDateFormat;

public class Person extends LifeKnightObject {
    private final LifeKnightString fullName;
    private final LifeKnightNumber.LifeKnightLong birthdate;
    public Person(String name, String group, String fullName, Long birthdate) {
        super(name, group);
        this.fullName = super.createString("Full Name", "Details", fullName);
        this.birthdate = (LifeKnightNumber.LifeKnightLong) super.createNumber("Birthdate", "Details", birthdate);
        this.birthdate.setiCustomDisplayString(objects -> new SimpleDateFormat("MM:dd:yy hh:mm:ss a").format(Person.this.birthdate.getValue()));
    }

    @Override
    public String getCustomDisplayString() {
        return fullName.getValue();
    }
}
