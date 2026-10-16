-- Add a pre-computed, weighted tsvector column
ALTER TABLE support_tickets
    ADD COLUMN search_vector tsvector
        GENERATED ALWAYS AS (
            setweight(to_tsvector('english', coalesce(subject, '')), 'A') ||
            setweight(to_tsvector('english', coalesce(description, '')), 'B') ||
            setweight(to_tsvector('english', coalesce(resolution_notes, '')), 'C') ||
            setweight(to_tsvector('english', coalesce(tags, '')), 'D')
        ) STORED;

-- GIN index on the stored vector — makes @@ lookups fast at any data volume
CREATE INDEX idx_support_tickets_search_vector
    ON support_tickets USING GIN (search_vector);
