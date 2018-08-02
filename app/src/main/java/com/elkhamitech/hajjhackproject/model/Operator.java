package com.elkhamitech.hajjhackproject.model;

public class Operator {

    private String operator_id;
    private String name;
    private String national_id;
    private String phone_number;
    private String sub_zone_id;

    public Operator() {
    }

    public String getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(String operator_id) {
        this.operator_id = operator_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSub_zone_id() {
        return sub_zone_id;
    }

    public void setSub_zone_id(String sub_zone_id) {
        this.sub_zone_id = sub_zone_id;
    }
}
