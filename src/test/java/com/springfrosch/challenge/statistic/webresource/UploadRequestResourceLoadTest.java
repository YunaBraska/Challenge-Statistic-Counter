package com.springfrosch.challenge.statistic.webresource;

import com.springfrosch.challenge.statistic.Application;
import com.springfrosch.challenge.statistic.utils.ApiUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LongSummaryStatistics;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@DirtiesContext
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ContextConfiguration(classes = {Application.class})
@AutoConfigureMockMvc
public class UploadRequestResourceLoadTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadRequestResourceLoadTest.class);
    private final Long AMOUNT_OF_EXECUTIONS = 100000L;

    @Autowired
    private ApiUtil apiUtil;

    @Test
    public void checkIfValuesRemainCorrect() throws Exception {
        LOGGER.debug("Starting test with [{}] parallel executions", AMOUNT_OF_EXECUTIONS);
        Long startTime = System.currentTimeMillis();
        Long batchSize = 5L;

        ExecutorService pool = ApiUtil.executeAmountOfStoreRunners(AMOUNT_OF_EXECUTIONS, () -> {
            try {
                apiUtil.upload(batchSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        pool.awaitTermination(30, SECONDS);

        Long endTimeMs = (System.currentTimeMillis() - startTime);
        Long requestPerSecond = AMOUNT_OF_EXECUTIONS / (endTimeMs / 1000);
        LOGGER.debug("Ended test with [{}] parallel executions in [{}]s", AMOUNT_OF_EXECUTIONS, endTimeMs / 1000);
        LOGGER.debug("[{}]requests/s", requestPerSecond);

        LongSummaryStatistics lastStatistics = apiUtil.statistics();
        assertThat(lastStatistics.getSum(), is((batchSize * AMOUNT_OF_EXECUTIONS)));
        assertThat(lastStatistics.getMin(), is(batchSize));

        LOGGER.warn("Asserts after this text depends on the current machine and are not covering any functionality!", AMOUNT_OF_EXECUTIONS);
        assertThat(requestPerSecond.intValue(), is(greaterThan(12000)));
        assertThat(endTimeMs.intValue(), is(lessThan(10000)));
    }
}