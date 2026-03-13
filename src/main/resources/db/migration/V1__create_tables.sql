CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    password VARCHAR NOT NULL
);

CREATE TABLE expense (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC NOT NULL,
    category VARCHAR NOT NULL,
    date DATE NOT NULL,
    description VARCHAR,
    sub_category VARCHAR NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_expense_user FOREIGN KEY (user_id) REFERENCES users(id)
);