
package com.solari.supportintelligence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.solari.supportintelligence.entity.SupportTicket;

import java.util.Optional;

public interface SupportTicketRepository
        extends JpaRepository<SupportTicket, Long> {

    Optional<SupportTicket> findByTicketId(String ticketId);

    boolean existsByTicketId(String ticketId);
}
