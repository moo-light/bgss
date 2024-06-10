WITH
    myconstants (username) as (
        values
            ('user')
    )

DELETE FROM refreshtoken
WHERE
    id IN (
        SELECT
            r.id
        FROM
            refreshtoken r
            JOIN users u ON r.user_id = u.id
        WHERE
            u.username = username
    );