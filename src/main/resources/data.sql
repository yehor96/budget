INSERT INTO categories (name) VALUES ('Food');
INSERT INTO categories (name) VALUES ('Clothes');

INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-05', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-06', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-12', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-13', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-17', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-18', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-25', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-06-26', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-05', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-06', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-12', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-13', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-17', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-18', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-25', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-07-26', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-05', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-06', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-12', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-13', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-17', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-18', 10, true, 2);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-25', 100, true, 1);
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2023-08-26', 10, true, 2);

INSERT INTO income_sources (name, value, currency, accrual_day) VALUES ('Yehor salary', 2000, 'USD', 15);
INSERT INTO income_sources (name, value, currency, accrual_day) VALUES ('Tania salary', 50000, 'UAH', 31);

INSERT INTO storage_records (date, stored_in_total) VALUES ('2023-08-20', 15000);
INSERT INTO storage_items (storage_record_id, currency, value, name) VALUES (1, 'USD', 10000, 'Tania safe');
INSERT INTO storage_items (storage_record_id, currency, value, name) VALUES (1, 'USD', 5000, 'Yehor safe');

INSERT INTO balance_records (date, total_expected_expenses_days_1_7, total_expected_expenses_days_8_14,
                            total_expected_expenses_days_15_21, total_expected_expenses_days_22_31)
                            VALUES ('2023-08-20', 110.00, 110.00, 110.00, 73.34);
INSERT INTO balance_items (balance_record_id, cash, card, item_name) VALUES (1, 1000.00, 5000.00, 'Yehor');
INSERT INTO balance_items (balance_record_id, cash, card, item_name) VALUES (1, 500.00, 15000.00, 'Tania');