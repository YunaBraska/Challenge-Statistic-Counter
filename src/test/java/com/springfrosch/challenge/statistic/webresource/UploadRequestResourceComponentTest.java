package com.springfrosch.challenge.statistic.webresource;

import com.springfrosch.challenge.statistic.utils.ApiUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LongSummaryStatistics;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
public class UploadRequestResourceComponentTest {

    @Autowired
    private ApiUtil apiUtil;

    @Test
    public void upload_withCurrentTime_ShouldBeSuccessful() throws Exception {
        apiUtil.upload(2L, 0, status().isCreated());

        LongSummaryStatistics statistics = apiUtil.statistics();
        assertThat(statistics.getSum(), is(2L));
        assertThat(statistics.getCount(), is(1L));
    }

    @Test
    public void upload_WithDataThanOneMinute_ShouldNotUpload() throws Exception {
        apiUtil.upload(2L, 90, status().isNoContent());

        assertThat(apiUtil.statistics().getCount(), is(0L));
    }

    @Test
    public void statistics_WithMultiple_ShouldGiveStatistic() throws Exception {
        apiUtil.upload(4L, 10, status().isCreated());
        apiUtil.upload(2L, 0, status().isCreated());
        apiUtil.upload(16L, 30, status().isCreated());
        apiUtil.upload(8L, 20, status().isCreated());
        apiUtil.upload(88L, 50, status().isCreated());
        apiUtil.upload(32L, 40, status().isCreated());

        LongSummaryStatistics lastStatistics = apiUtil.statistics();
        assertThat(lastStatistics.getCount(), is(6L));
        assertThat(lastStatistics.getSum(), is(150L));
        assertThat(lastStatistics.getMin(), is(2L));
        assertThat(lastStatistics.getMax(), is(88L));
        assertThat(lastStatistics.getAverage(), is(25.00D));
    }

    @Test
    public void statistics_ShouldNotIncludeOldData() throws Exception {
        apiUtil.upload(2L, 0, status().isCreated());
        apiUtil.upload(4L, 10, status().isCreated());
        apiUtil.upload(8L, 20, status().isCreated());
        apiUtil.upload(16L, 30, status().isCreated());
        apiUtil.upload(88L, 61, status().isNoContent());
        apiUtil.upload(32, 60, status().isCreated());

        Thread.sleep(500); //Incase the test is too fast

        LongSummaryStatistics lastStatistics = apiUtil.statistics();
        assertThat(lastStatistics.getCount(), is(4L));
        assertThat(lastStatistics.getSum(), is(30L));
        assertThat(lastStatistics.getMin(), is(2L));
        assertThat(lastStatistics.getMax(), is(16L));
        assertThat(lastStatistics.getAverage(), is(7.50D));
    }

//    private ResultActions apiUtil.upload(long count, int ageInSeconds, ResultMatcher httpStatus) throws Exception {
//        return mvc.perform(post("/api/upload")
//                .content(prepareRequest(count, ageInSeconds))
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(httpStatus);
//    }
//
//    private String prepareRequest(long count, int ageInSeconds) throws JsonProcessingException {
//        UploadRequest uploadRequest = new UploadRequest();
//        uploadRequest.setCount(count);
//        uploadRequest.setTimestamp(new DateTime(UTC).minusSeconds(ageInSeconds).toDate());
//        return mapper.writeValueAsString(uploadRequest);
//    }
//
//    private LongSummaryStatistics getLastStatistics() throws Exception {
//        return mapper.readValue(mvc.perform(get("/api/statistics")).andReturn().getResponse().getContentAsString(), LongSummaryStatistics.class);
//    }


}