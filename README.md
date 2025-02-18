Spring Boot service for companies dividends with CI/CD automated deployment to Google Cloud Run.

run this in your postres container:

CREATE TABLE dividend_events (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL, -- 'dueDate' or 'distributionDate'
    event_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_event UNIQUE (symbol, type, event_date)
);


CREATE TABLE device_subscription (
    device_token   VARCHAR(255) NOT NULL,
    company_symbol VARCHAR(50)  NOT NULL,
    PRIMARY KEY (device_token, company_symbol)
);
