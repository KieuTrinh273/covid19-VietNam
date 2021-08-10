package com.kieutrinh.project.covid19invietnam.controller;

import com.kieutrinh.project.covid19invietnam.service.ProvinceService;
import com.kieutrinh.project.covid19invietnam.service.vnDailyReportService;
import com.kieutrinh.project.covid19invietnam.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
public class HomeController {

    private final vnDailyReportService dataDailyService;
    private final NewsService newsService;
    private final ProvinceService provinceService;

    @Autowired
    public HomeController(vnDailyReportService dataDailyService, NewsService newsService, ProvinceService provinceService) {
        this.dataDailyService = dataDailyService;
        this.newsService = newsService;
        this.provinceService = provinceService;
    }


    @RequestMapping
    @GetMapping(path = "/")
    public String getHome(Model model) throws IOException {
        model.addAttribute("news", newsService.getListNews());
        model.addAttribute("data", dataDailyService.updateCurrentData());
        model.addAttribute("provincesData", provinceService.getProvinceData());
        model.addAttribute("deltaCaseTotal", provinceService.getDeltaCaseTotal());

        return "home";
    }
}
