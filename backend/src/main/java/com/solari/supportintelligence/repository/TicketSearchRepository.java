
package com.solari.supportintelligence.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import com.solari.supportintelligence.entity.SupportTicket;

import java.util.List;

public interface TicketSearchRepository extends Repository<SupportTicket, Long> {

    @Query(value = """
            SELECT
                t.ticket_id,
                t.category,
                t.subject,
                t.description,
                t.resolution_notes,
                t.tags,
                t.created_at,
                t.status,
                ts_rank_cd(
                    t.search_vector,
                    websearch_to_tsquery('english', :query)
                ) AS score
            FROM support_tickets t
            WHERE t.search_vector @@ websearch_to_tsquery('english', :query)
            ORDER BY score DESC, t.created_at DESC
            LIMIT :maxResults
            """, nativeQuery = true)
    List<Object[]> search(
            @Param("query") String query,
            @Param("maxResults") int maxResults);
}
