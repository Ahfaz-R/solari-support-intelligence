CREATE TABLE support_tickets (
    id BIGSERIAL PRIMARY KEY,
    ticket_id VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(100) NOT NULL,
    subject VARCHAR(500) NOT NULL,
    description TEXT NOT NULL,
    resolution_notes TEXT NOT NULL,
    tags TEXT,
    created_at DATE NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE INDEX idx_support_tickets_category
    ON support_tickets(category);

CREATE INDEX idx_support_tickets_created_at
    ON support_tickets(created_at);
