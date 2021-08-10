package com.kieutrinh.project.covid19invietnam.service;

import com.kieutrinh.project.covid19invietnam.model.ProvinceData;
import com.kieutrinh.project.covid19invietnam.repository.ProvinceDataRepository;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ProvinceDataService {
    private final ProvinceDataRepository repository;

    @Autowired
    public ProvinceDataService(ProvinceDataRepository repository) {
        this.repository = repository;
    }

    private String url = "https://ncov.moh.gov.vn/vi/web/guest/dong-thoi-gian?p_p_id=com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_nf7Qy5mlPXqs&p_p_lifecycle=0&p_p_state=normal&p_p_mode=view&_com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_nf7Qy5mlPXqs_delta=10&p_r_p_resetCur=false&_com_liferay_asset_publisher_web_portlet_AssetPublisherPortlet_INSTANCE_nf7Qy5mlPXqs_cur=1";
    private String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0";

    @PostConstruct
    public void getData() throws IOException {
        final SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd/MM/yyyy");
        String regex = "div.timeline-content>p:matches((\\p{L}+\\s){1,4}\\([0-9]+\\.*[0-9]*\\s*\\))";
        Pattern pattern = Pattern.compile("([A-Z]+[a-z]+\\s){1,4}\\([0-9]+\\.*[0-9]*\\s*\\)*");


        // get all pages contains timeline
        int cur = Integer.parseInt(url.substring(url.indexOf("cur=")).replace("cur=", ""));
        while (SSLHelper.getConnection(url).userAgent(USER_AGENT).get()
                .select("ul.lfr-pagination-buttons>li").get(1)
                .select("a").attr("href").contains("cur=")) {

            //processing the data of each timeline
            Document document = SSLHelper.getConnection(url).userAgent(USER_AGENT).get();
            Elements timelines = document.select("div.timeline-detail");
            for (Element timeline : timelines) {

                //h3 contains information about time (hh:mm dd/MM/yyyy)
                String time = timeline.select("div.timeline-head>h3").text();
                Elements paragraphs = timeline.select(regex);

                //get data about case of each province
                paragraphs.forEach(paragraph -> {
                    String string = covertToString(paragraph.text());
                    Matcher matcher = pattern.matcher(string);

                    //each province in each timeline
                    while (matcher.find()) {
                        String[] nameAndCase = matcher.group().replace("(", "-")
                                .replace(")", "").split("-");
                        ProvinceData byDateAndName = null;
                        try {
                            byDateAndName = repository.findByDateAndName(sdf.parse(time), nameAndCase[0].trim());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(byDateAndName!=null){
                            continue;
                        }
                        ProvinceData data = new ProvinceData();
                        data.setDate(time);
                        data.setName(nameAndCase[0].trim());
                        data.setNumOfCase(Long.parseLong(nameAndCase[1].trim().replace(".", "")));
                        repository.save(data);
                    }
                });
            }

            cur++;
            url = url.substring(0, url.indexOf("cur=")) + "cur=" + cur;
        }
    }

    public String covertToString(String value) {
        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("")
                    .replace('đ', 'd')
                    .replace('Đ', 'D');
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
