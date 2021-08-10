package com.kieutrinh.project.covid19invietnam.service;

import com.kieutrinh.project.covid19invietnam.model.Province;
import com.kieutrinh.project.covid19invietnam.model.vnDailyReport;
import com.kieutrinh.project.covid19invietnam.repository.ProvinceRepository;
import com.kieutrinh.project.covid19invietnam.repository.vnDailyReportRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Stream;

@Service
public class ProvinceService {

    private final ProvinceRepository repository;
    private final vnDailyReportRepository reportRepository;

    String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";
    private String url = "https://ncov.moh.gov.vn/web/guest/trang-chu";

    private long deltaCaseTotal=0;

    @Autowired
    public ProvinceService(ProvinceRepository repository, vnDailyReportRepository reportRepository) {
        this.repository = repository;
        this.reportRepository = reportRepository;
    }

    @PostConstruct
    public void getCurrentDetail() throws IOException {
        Document document = SSLHelper.getConnection(url).userAgent(USER_AGENT).get();

        Stream<Element> elementStream = document.select("tr").stream()
                .filter(element -> element.attr("style")
                        .equalsIgnoreCase("font-weight: 600"));
        elementStream.forEach(element -> {
            Elements cols = element.select("td");
            Province province = repository.findByName(cols.get(0).text()).orElse(new Province());
            province.setName(cols.get(0).text());
            province.setCaseTotal(Long.parseLong(cols.get(1).text().replace(".", "")));
            province.setCaseToday(Long.parseLong(cols.get(2).text().replace(".", "")));
            province.setDeathTotal(Long.parseLong(cols.get(3).text().replace(".", "")));
            repository.save(province);
        });

        repository.findAll().forEach(province -> {
            deltaCaseTotal+=province.getCaseToday();
        });
         updateDataYesterday();
    }

    public void updateDataYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        vnDailyReport yesterdayData = reportRepository.findByDate(yesterday).orElse(new vnDailyReport());
        yesterdayData.setDate(yesterday);
        yesterdayData.setTotalConfirmed(reportRepository.getCurrentNumOfCase()-deltaCaseTotal);
        reportRepository.save(yesterdayData);
    }

    public String getDeltaCaseTotal() {
        if (deltaCaseTotal>0){
            return "+" + deltaCaseTotal +" cases";
        } else if(deltaCaseTotal<0){
            return "-" + deltaCaseTotal +" cases";
        } else{
            return "Unchanged from yesterday!";
        }
    }
    public Iterable<Province> getProvinceData(){
        return repository.findAll(Sort.by(Sort.Direction.DESC,"caseTotal"));
    }
}
