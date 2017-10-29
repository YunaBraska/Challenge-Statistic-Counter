package com.springfrosch.challenge.statistic.model;

import java.io.Serializable;
import java.util.Date;

public class UploadRequest implements Serializable {

    private Long count;

    private Date timestamp;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UploadRequest uploadRequest = (UploadRequest) o;

        if (count != null ? !count.equals(uploadRequest.count) : uploadRequest.count != null) return false;
        return timestamp != null ? timestamp.equals(uploadRequest.timestamp) : uploadRequest.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = count != null ? count.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UploadRequest{" +
                ", count=" + count +
                ", timestamp=" + timestamp +
                '}';
    }
}
