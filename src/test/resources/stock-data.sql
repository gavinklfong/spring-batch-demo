DELETE FROM stock_price_history;
INSERT INTO stock_price_history (ticker, date, open, close, high, low, volume)
VALUES ('APPL', '2024-10-15', 10, 10, 10, 10, 2000),
 ('APPL', '2024-10-14', 10, 9, 10, 10, 2000),
 ('APPL', '2024-10-13', 10, 8, 10, 10, 2000),
 ('APPL', '2024-10-12', 10, 7, 10, 10, 2000),
 ('APPL', '2024-10-11', 10, 6, 10, 10, 2000),
 ('APPL', '2024-10-10', 10, 5, 10, 10, 2000);

DELETE FROM stock_price_sma;
INSERT INTO stock_price_sma (ticker, date, value_10, value_12, value_20, value_26, value_50, value_100, value_200)
VALUES ('APPL', '2024-10-15', 10, 12, 20, 26, 50, 100, 200);

DELETE FROM stock_price_ema;
INSERT INTO stock_price_ema (ticker, date, value_10, value_12, value_20, value_26, value_50, value_100, value_200)
VALUES ('APPL', '2024-10-15', 10, 12, 20, 26, 50, 100, 200),
('APPL', '2024-10-14', 11, 12, 21, 26, 51, 100, 200),
('APPL', '2024-10-13', 12, 12, 22, 26, 52, 100, 200),
('APPL', '2024-10-12', 13, 12, 23, 26, 53, 100, 200);

DELETE FROM stock_price_macd;
INSERT INTO stock_price_macd (ticker, `date`, value, sma_9, ema_9)
VALUES ('APPL', '2024-10-15', 10, 12, 20),
('APPL', '2024-10-14', 11, 12, 21),
('APPL', '2024-10-13', 12, 12, 22),
('APPL', '2024-10-12', 13, 12, 23);
