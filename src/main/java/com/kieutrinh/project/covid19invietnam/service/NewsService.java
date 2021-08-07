package com.kieutrinh.project.covid19invietnam.service;

import com.kieutrinh.project.covid19invietnam.model.News;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {
    private String url ="https://baomoi.com/phong-chong-dich-covid-19/top/328.epi";
    private List<News> listNews= new ArrayList<>();

    public List<News> getListNews() {
        return listNews;
    }

    @PostConstruct
    @Scheduled(cron = "* 0 * * * *")
    public void getNews() throws IOException {
        List<News> listNewsUpdating = new ArrayList<>();
        Document document = Jsoup.connect(url).get();
        Elements allNews = document.select("div.bm_E");

        for (Element eachNews:allNews) {
            Element a = eachNews.select("div.bm_N").select("a").get(0);
            News news = new News();

            news.setUrl(a.attr("href"));
            news.setTitle(a.attr("title"));
            listNewsUpdating.add(news);
        }
        this.listNews = listNewsUpdating;
    }
}
