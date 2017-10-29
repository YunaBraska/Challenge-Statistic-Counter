package com.springfrosch.challenge.statistic.utils;

//import com.mifmif.common.regex.Generex;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springfrosch.challenge.statistic.model.UploadRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.LongSummaryStatistics;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.joda.time.DateTimeZone.UTC;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
public class ApiUtil {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    public static ExecutorService executeAmountOfStoreRunners(Long amount, Runnable lambda) {
        ExecutorService pool = Executors.newFixedThreadPool(16);
        for (Long i = 0L; i < amount; i++) {
            pool.submit(lambda);
        }
        pool.shutdown();
        return pool;
    }

    public ResultActions upload(long count) throws Exception {
        return upload(count, 0, status().isCreated());
    }

    public ResultActions upload(long count, int ageInSeconds, ResultMatcher httpStatus) throws Exception {
        return mvc.perform(post("/api/upload")
                .content(prepareRequest(count, ageInSeconds))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(httpStatus);
    }

    private String prepareRequest(long count, int ageInSeconds) throws JsonProcessingException {
        UploadRequest uploadRequest = new UploadRequest();
        uploadRequest.setCount(count);
        uploadRequest.setTimestamp(new DateTime(UTC).minusSeconds(ageInSeconds).toDate());
        return mapper.writeValueAsString(uploadRequest);
    }

    public LongSummaryStatistics statistics() throws Exception {
        return mapper.readValue(mvc.perform(get("/api/statistics")).andReturn().getResponse().getContentAsString(), LongSummaryStatistics.class);
    }
}
