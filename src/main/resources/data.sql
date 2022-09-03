INSERT INTO users (username, password) VALUES ('admin', '$2a$12$W2FfaT3M9CNfdnpjWxMELeFwytxm0.IXWP.VE6UeNH2qvC3lqogvW');

INSERT INTO categories (name) VALUES ('Food');
INSERT INTO categories (name) VALUES ('Meds');

INSERT INTO row_regular_expected_expenses (category_id, days_1_to_7, days_8_to_14, days_15_to_21, days_22_to_31) VALUES (1, 50, 100, 20, 10);
INSERT INTO row_regular_expected_expenses (category_id, days_1_to_7, days_8_to_14, days_15_to_21, days_22_to_31) VALUES (2, 1, 5, 200, 30);