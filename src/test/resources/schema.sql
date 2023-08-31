CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS messages (
    id varchar(50) DEFAULT uuid_generate_v4()::text,
    text VARCHAR(500),
    CONSTRAINT id_messages PRIMARY KEY (id)
    );