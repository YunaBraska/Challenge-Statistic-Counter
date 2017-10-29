package com.springfrosch.challenge.statistic.webresource;

import com.springfrosch.challenge.statistic.logic.StatisticService;
import com.springfrosch.challenge.statistic.model.UploadRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.LongSummaryStatistics;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class StatisticResource {

    private StatisticService statisticService;

    @Autowired
    public StatisticResource(final StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @RequestMapping(value = "/api/upload", method = POST)
    public void cacheAndCount(@RequestBody final UploadRequest uploadRequest, HttpServletResponse response) {
        if(statisticService.upload(uploadRequest)){
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    @RequestMapping(value = "api/statistics", method = GET)
    public LongSummaryStatistics statistics() {
        return statisticService.statistics();
    }
}
