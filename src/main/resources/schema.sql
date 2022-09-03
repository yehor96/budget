DROP TABLE IF EXISTS expenses_to_tags;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS row_regular_expected_expenses;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS users;

CREATE TABLE categories (
                       category_id BIGSERIAL NOT NULL,
                       name VARCHAR(50),

                       CONSTRAINT categories_pk PRIMARY KEY (category_id),
                       CONSTRAINT categories_name_uq UNIQUE (name)
                       );

CREATE TABLE row_regular_expected_expenses (
                        row_regular_expected_expenses_id BIGSERIAL NOT NULL,
                        category_id BIGINT NOT NULL,
                        days_1_to_7 NUMERIC(11,2) NOT NULL,
                        days_8_to_14 NUMERIC(11,2) NOT NULL,
                        days_15_to_21 NUMERIC(11,2) NOT NULL,
                        days_22_to_31 NUMERIC(11,2) NOT NULL,

                        CONSTRAINT row_regular_expected_expenses_pk PRIMARY KEY (row_regular_expected_expenses_id),
                        CONSTRAINT row_regular_expected_expenses_to_categories_fk FOREIGN KEY (category_id) REFERENCES categories (category_id)
                        );

CREATE TABLE tags (
                       tag_id BIGSERIAL NOT NULL,
                       name VARCHAR(50),

                       CONSTRAINT tags_pk PRIMARY KEY (tag_id),
                       CONSTRAINT tags_name_uq UNIQUE (name)
                       );

CREATE TABLE expenses (
    				   expense_id BIGSERIAL NOT NULL,
    				   date DATE NOT NULL,
    				   value NUMERIC(11,2) NOT NULL,
    				   is_regular BOOLEAN DEFAULT FALSE,
    				   category_id BIGINT NOT NULL,

    				   CONSTRAINT expenses_pk PRIMARY KEY (expense_id),
    				   CONSTRAINT expenses_to_categories_fk FOREIGN KEY (category_id) REFERENCES categories (category_id)
    				   );

CREATE TABLE expenses_to_tags (
                       expense_id BIGINT NOT NULL,
                       tag_id BIGINT NOT NULL,

                       CONSTRAINT expenses_to_tags_pk PRIMARY KEY (expense_id, tag_id),
                       CONSTRAINT expenses_to_tags_to_expenses_fk FOREIGN KEY (expense_id) REFERENCES expenses (expense_id),
                       CONSTRAINT expenses_to_tags_to_tags_fk FOREIGN KEY (tag_id) REFERENCES tags (tag_id)
                       );

CREATE TABLE settings  (
                        settings_id SERIAL NOT NULL,
                        budget_start_date DATE NOT NULL,
                        budget_end_date DATE NOT NULL,
                        budget_date_validation BOOLEAN NOT NULL,

                        CONSTRAINT settings_pk PRIMARY KEY (settings_id)
                        );

CREATE TABLE users      (
                        user_id SERIAL NOT NULL,
                        username VARCHAR(50) NOT NULL,
                        password VARCHAR(500) NOT NULL,

                        CONSTRAINT users_pk PRIMARY KEY (user_id),
                        CONSTRAINT users_username_uq UNIQUE (username)
                        );