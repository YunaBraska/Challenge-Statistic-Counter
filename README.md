# Statistic Counter [Coding Challenge]

## Purpose
Develop a restful API that generates statistics data uploads. The main use case is to calculate real time statistics of data uploads to our stitching service in the last 60 seconds. Users can batch upload data. To accomplish this, two API endpoints need to be implemented. One is responsible for the input of the data, the other to retrieve the relevant statistics.

## Usage

##### POST /upload
Every time a users uploads a batch of data (count = batch), this endpoint will be called.
```json
{
"count": 3,
"timestamp": 12890212 
}
```
* count = Number of uploaded in this batch
* timestamp = time of upload in UTC
* Returns 201 in case of success
* Returns 204 if timestamp is older than 60 seconds

##### GET /statistics
This is the main endpoint of this tasks. Since the API has to be scalable, it is important that it execute in constant time and memory O(1). The endpoint returns the statistics of uploads in the last 60 seconds.
Returns
```json
{
  "sum": 3,
   "avg": 1.0,
   "max": 2,
   "min": 1,
   "count: 2
}
```
* sum = Total amount of uploaded data
* avg = Average amount of uploaded data per batch 
* max = Maximum of uploaded data per batch
* min = Minimum amount of data per batch

## Requirements
* The API is required to run in constant time and space to be scalable. Other requirements that are obvious, but also listed here explicitly:
* The API has to be threadsafe
* The API has to function properly over a longer period of time
* The project should be easily buildable (gradle or maven are preferred)
* The API should be able to deal with time discrepancy, since timing issues can always occur.
* Do not use any database including only-memory databases.
* The endpoints have to execute in constant time and memory (O(1))
* API has to be fully tested including unit tests and end to end tests.