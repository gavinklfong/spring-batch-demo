USE demo;

-- DROP TABLE demo.people IF EXISTS;

CREATE TABLE people  (
    person_id BIGINT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(20),
    last_name VARCHAR(20),
    PRIMARY KEY (person_id)
);


CREATE TABLE stock_price_history (
    ticker VARCHAR(30),
    `date` date,
    open decimal(14,2),
    close decimal(14,2),
    high decimal(14,2),
    low decimal(14,2),
    volume bigint,
    PRIMARY KEY (ticker, `date`)
);

CREATE TABLE stock_price_sma (
    ticker VARCHAR(30),
    `date` date,
    value_10 decimal(14,2),
    value_12 decimal(14,2),
    value_20 decimal(14,2),
    value_26 decimal(14,2),
    value_50 decimal(14,2),
    value_100 decimal(14,2),
    value_200 decimal(14,2),
    PRIMARY KEY (ticker, `date`)
);

CREATE TABLE stock_price_ema (
    ticker VARCHAR(30),
    `date` date,
    value_10 decimal(14,2),
    value_12 decimal(14,2),
    value_20 decimal(14,2),
    value_26 decimal(14,2),
    value_50 decimal(14,2),
    value_100 decimal(14,2),
    value_200 decimal(14,2),
    PRIMARY KEY (ticker, `date`)
);

CREATE TABLE stock_price_macd (
    ticker VARCHAR(30),
    `date` date,
    value decimal(14,2),
    sma_9 decimal(14,2),
    ema_9 decimal(14,2),
    PRIMARY KEY (ticker, `date`)
);

CREATE TABLE stock_price_mfi (
    ticker VARCHAR(30),
    `date` date,
    value_10 decimal(14,2),
    value_12 decimal(14,2),
    value_20 decimal(14,2),
    value_26 decimal(14,2),
    value_50 decimal(14,2),
    value_100 decimal(14,2),
    value_200 decimal(14,2),
    PRIMARY KEY (ticker, `date`)
);