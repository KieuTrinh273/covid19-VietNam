package com.kieutrinh.project.covid19invietnam.service;

import com.kieutrinh.project.covid19invietnam.model.vnDailyReport;
import com.kieutrinh.project.covid19invietnam.repository.vnDailyReportRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;


@Service
public class vnDailyReportService {

    String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";
    private String url = "https://ncov.moh.gov.vn/web/guest/trang-chu";

    @Autowired
    private final vnDailyReportRepository repository;

    public vnDailyReportService(vnDailyReportRepository repository) {
        this.repository = repository;
    }


    @PostConstruct
    @Scheduled(cron = "* 1 * * * *")
    public vnDailyReport updateCurrentData() throws IOException {
        Document document = SSLHelper.getConnection(url).userAgent(USER_AGENT).get();
        Element currentData = document.select("div.mt-5").get(0);

        vnDailyReport data = repository.findByDate(LocalDate.now()).orElse(new vnDailyReport());

        Elements spans = currentData.select("span");
        data.setTotalConfirmed(Long.parseLong(spans.get(0).text().replace(".", "")));
        data.setTotalRecovered(Long.parseLong(spans.get(2).text().replace(".", "")));
        data.setTotalDeaths(Long.parseLong(spans.get(3).text().replace(".", "")));
        data.setDate();
        repository.save(data);
        return data;
    }

}
