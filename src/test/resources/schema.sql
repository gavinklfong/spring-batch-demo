CREATE TABLE stock_price_history  (
    ticker VARCHAR(30),
    `date` date,
    open decimal(14,2),
    close decimal(14,2),
    high decimal(14,2),
    low decimal(14,2),
    volume bigint,
    PRIMARY KEY (ticker, `date`)
);

