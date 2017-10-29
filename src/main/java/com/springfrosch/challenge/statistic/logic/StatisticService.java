package com.springfrosch.challenge.statistic.logic;

import com.springfrosch.challenge.statistic.model.UploadRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static org.joda.time.DateTimeZone.UTC;

@Service
public class StatisticService {

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ConcurrentSkipListMap<Long, LongAdder> storage = new ConcurrentSkipListMap<>();

    @Autowired
    public StatisticService() {
    }

    public boolean upload(UploadRequest request) {
        if (request.getTimestamp() == null || request.getCount() == null || request.getTimestamp().before(new DateTime(UTC).minusSeconds(61).toDate())) {
            return false;
        }

        lock.readLock().lock();
        try {
            storage.computeIfAbsent(request.getTimestamp().getTime(), v -> new LongAdder()).add(request.getCount());
        } finally {
            lock.readLock().unlock();
        }
        return true;
    }

    public LongSummaryStatistics statistics() {
        return getStatisticValuesFromLastMinute().stream()
                .mapToLong(LongAdder::longValue)
                .summaryStatistics();
    }

    private Collection<LongAdder> getStatisticValuesFromLastMinute() {
        lock.writeLock().lock();
        try {
            Long lastMinute = new DateTime(UTC).minusSeconds(60).toDate().getTime();
            ConcurrentNavigableMap<Long, LongAdder> tailLastMinute = storage.tailMap(lastMinute, true);
            //FIXME: add this if the old data has to be removed
            //  storage.clear();
            //  storage = new ConcurrentSkipListMap<>(tailLastMinute);
            return tailLastMinute.values();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
