DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS categories;

CREATE TABLE expenses (
    				   expense_id BIGSERIAL NOT NULL,
    				   date DATE NOT NULL,
    				   value NUMERIC(11,2) NOT NULL,
    				   is_regular BOOLEAN DEFAULT FALSE,

    				   CONSTRAINT expenses_pk PRIMARY KEY (expense_id)
    				   );
CREATE TABLE categories (
                       category_id BIGSERIAL NOT NULL,
                       name VARCHAR(50),

                        CONSTRAINT categories_pk PRIMARY KEY (category_id),
                        CONSTRAINT name_uq UNIQUE (name)
                       );