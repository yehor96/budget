DROP TABLE IF EXISTS expenses;
CREATE TABLE expenses (
    				   expense_id BIGSERIAL NOT NULL,
    				   date DATE NOT NULL,
    				   value INTEGER,
    				   is_regular BOOLEAN DEFAULT FALSE,

    				   CONSTRAINT expenses_pk PRIMARY KEY (expense_id),
    				   CONSTRAINT date_uq UNIQUE (date)
    				   );