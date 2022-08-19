DROP TABLE IF EXISTS expenses_to_tags;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS settings;
DROP TABLE IF EXISTS tags;

CREATE TABLE categories (
                       category_id BIGSERIAL NOT NULL,
                       name VARCHAR(50),

                       CONSTRAINT categories_pk PRIMARY KEY (category_id),
                       CONSTRAINT categories_name_uq UNIQUE (name)
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
    				   CONSTRAINT expense_to_categories_fk FOREIGN KEY (category_id) REFERENCES categories (category_id)
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