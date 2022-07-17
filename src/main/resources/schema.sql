DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS settings;

CREATE TABLE categories (
                       category_id BIGSERIAL NOT NULL,
                       name VARCHAR(50),

                       CONSTRAINT categories_pk PRIMARY KEY (category_id),
                       CONSTRAINT name_uq UNIQUE (name)
                       );

CREATE TABLE expenses (
    				   expense_id BIGSERIAL NOT NULL,
    				   date DATE NOT NULL,
    				   value NUMERIC(11,2) NOT NULL,
    				   is_regular BOOLEAN DEFAULT FALSE,
    				   category_id BIGINT NOT NULL,

    				   CONSTRAINT expenses_pk PRIMARY KEY (expense_id),
    				   CONSTRAINT expense_to_categories_fk FOREIGN KEY (category_id) REFERENCES categories (category_id)
    				   );

CREATE TABLE settings  (
                        settings_id SERIAL NOT NULL,
                        budget_start_date DATE NOT NULL,
                        budget_end_date DATE NOT NULL,
                        budget_date_validation BOOLEAN NOT NULL,

                        CONSTRAINT settings_pk PRIMARY KEY (settings_id)
                        );