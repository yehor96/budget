-- create categories
INSERT INTO categories (name) VALUES ('Food');

-- create expenses
-- default expense id#1
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2022-07-29', 10.00, true, 1);
-- second expense id#2
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2022-07-28', 100.00, false, 1);
-- third expense id#3
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2022-07-27', 15.50, false, 1);

-- expenses modified during tests
-- updated expense id#4
INSERT INTO expenses (date, value, is_regular, category_id) VALUES ('2022-07-30', 5.00, true, 1);