package com.kieutrinh.project.covid19invietnam.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
@Table
@Data
public class ProvinceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    @JsonFormat(pattern = "hh:mm dd/MM/yyyy")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Column
    private String name;
    @Column
    private long numOfCase;

    @Transient
    private final SimpleDateFormat sdfIn = new SimpleDateFormat("hh:mm dd/MM/yyyy") ;
    private final SimpleDateFormat sdfOut = new SimpleDateFormat("yyyy/MM/dd hh:mm") ;
    public void setDate(String date) {
        try {
            this.date = sdfIn.parse(date);
        } catch (ParseException e) {
            System.out.println(e);
        }
    }

    public String getDate() {
        return sdfIn.format(this.date);
    }
}
