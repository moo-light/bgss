WITH nearest_30_minutes AS (
    SELECT date_trunc('minute', LOCALTIMESTAMP ) + interval '30 second' * floor(EXTRACT(minute FROM LOCALTIMESTAMP ) / 30.0) AS timestamp
),
time_intervals AS (
    SELECT generate_series(
        (SELECT timestamp - interval '300 second' FROM nearest_30_minutes),
        (SELECT timestamp FROM nearest_30_minutes),
        interval '30 second'
    ) AS second_timestamp ORDER BY second_timestamp DESC
),
times AS (
    SELECT t1.second_timestamp - interval '30 second' AS leftT, t1.second_timestamp AS rightT
    FROM time_intervals t1
)
SELECT
    tm.*,
	 (SELECT COUNT(*) FROM live_rates lr
     WHERE lr.timestamp >= tm.leftT ) AS rowcount
FROM
    times tm
Right join live_rates lr on lr.timestamp >= leftt and lr.timestamp <=rightt
ORDER BY
    tm.leftT desc,timestamp desc

