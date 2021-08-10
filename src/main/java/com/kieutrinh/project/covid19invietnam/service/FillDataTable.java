package com.kieutrinh.project.covid19invietnam.service;

import com.kieutrinh.project.covid19invietnam.model.News;
import com.kieutrinh.project.covid19invietnam.model.vnDailyReport;
import com.kieutrinh.project.covid19invietnam.repository.vnDailyReportRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
//Fill data table
public class FillDataTable {
    private String urlConfirmedCaseData ="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private String urlDeathData = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private String urlRecoveredData = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    private final vnDailyReportRepository repository;

    public FillDataTable(vnDailyReportRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void collectAllData() throws IOException, URISyntaxException, InterruptedException {
        //fill data table (22/01/2020-2 days before current day)
        collectData(urlConfirmedCaseData,"confirmedCase");
        collectData(urlDeathData,"death");
        collectData(urlRecoveredData,"recovered");
    }

    public void collectData(String url, String attr) throws IOException, URISyntaxException, InterruptedException {
        HttpClient clien = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> response = clien.send(request, HttpResponse.BodyHandlers.ofString());

        //read data from file CSV (Viet Nam only)
        StringReader csvBodyReader = new StringReader(response.body());
        CSVParser records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        CSVRecord vnRecord = null;
        for (CSVRecord record: records) {
            if(record.get("Country/Region").equalsIgnoreCase("Vietnam")){
                vnRecord = record;
            }
        }
        //from 22/01/2020
        LocalDate date = LocalDate.of(2020,01,21);
        for (int i = 4; i < vnRecord.size(); i++) {
            date = date.plusDays(1);
            vnDailyReport dailyData = repository.findByDate(date).orElse(new vnDailyReport());
            dailyData.setDate(date);

            switch (attr){
                case "confirmedCase":
                    dailyData.setTotalConfirmed(Long.parseLong(vnRecord.get(i)));
                    break;
                case "death":
                    dailyData.setTotalDeaths(Long.parseLong(vnRecord.get(i)));
                    break;
                case "recovered":
                    dailyData.setTotalRecovered(Long.parseLong(vnRecord.get(i)));
                    break;
            }
            repository.save(dailyData);
        }
    }
}
