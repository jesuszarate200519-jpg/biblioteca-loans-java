package com.tecsup.library.integration;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tecsup.library.model.Book;
import com.tecsup.library.model.Member;
import com.tecsup.library.repo.mem.InMemoryBookRepository;
import com.tecsup.library.repo.mem.InMemoryLoanRepository;
import com.tecsup.library.repo.mem.InMemoryMemberRepository;
import com.tecsup.library.service.DomainException;
import com.tecsup.library.service.LoanService;
import com.tecsup.library.util.ClockProvider;

class LoanFlowIT {

    InMemoryBookRepository bookRepo;
    InMemoryMemberRepository memberRepo;
    InMemoryLoanRepository loanRepo;
    LoanService service;

    @BeforeEach
    void setup() {
        // Fijamos el reloj para pruebas deterministas (2025-11-01)
        Clock fixed = Clock.fixed(LocalDate.of(2025, 11, 1)
                .atStartOfDay(ZoneId.systemDefault()).toInstant(), ZoneId.systemDefault());
        ClockProvider.setFixedClock(fixed);

        bookRepo = new InMemoryBookRepository();
        memberRepo = new InMemoryMemberRepository();
        loanRepo = new InMemoryLoanRepository();

        // Semillas de prueba
        bookRepo.addBook(new Book("ISBN-001", "Clean Code", "Robert C. Martin"));
        bookRepo.addBook(new Book("ISBN-002", "Domain-Driven Design", "Eric Evans"));
        bookRepo.addBook(new Book("ISBN-003", "Refactoring", "Martin Fowler"));

        memberRepo.addMember(new Member("M-01", "Ana"));
        memberRepo.addMember(new Member("M-02", "Luis"));

        service = new LoanService(bookRepo, memberRepo, loanRepo);
    }

    @AfterEach
    void tearDown() {
        ClockProvider.reset();
    }

    @Test
    void flujoExitoso_prestarYDevolverSinMulta() {
        var loan = service.loanBook("ISBN-001", "M-01");
        assertNotNull(loan.getId());
        assertFalse(bookRepo.findByIsbn("ISBN-001").orElseThrow().isAvailable());

        // Avanzar 5 días (antes del vencimiento de 14 días)
        Clock newClock = Clock.offset(ClockProvider.getClock(),
                java.time.Duration.ofDays(5));
        ClockProvider.setFixedClock(newClock);

        var fee = service.returnBook(loan.getId());
        assertEquals(BigDecimal.ZERO, fee);

        assertTrue(bookRepo.findByIsbn("ISBN-001").orElseThrow().isAvailable());
        assertEquals(0, memberRepo.findById("M-01").orElseThrow().getActiveLoans());
    }

    @Test
    void noPermitePrestarLibroYaPrestado_R1() {
        service.loanBook("ISBN-002", "M-01");
        assertThrows(DomainException.class, () -> service.loanBook("ISBN-002", "M-02"));
    }

    @Test
    void noPermiteMasDeTresPrestamosActivos_R2() {
        service.loanBook("ISBN-001", "M-01");
        service.loanBook("ISBN-002", "M-01");
        service.loanBook("ISBN-003", "M-01");
        // cuarto préstamo debe fallar
        bookRepo.addBook(new Book("ISBN-004", "Patterns", "Christopher Alexander"));
        assertThrows(DomainException.class, () -> service.loanBook("ISBN-004", "M-01"));
    }

    @Test
    void noPermitePrestarSiHayMora_R3() {
        service.loanBook("ISBN-001", "M-02");

        // Avanzar 20 días -> vence (14) y queda en mora
        Clock lateClock = Clock.offset(ClockProvider.getClock(),
                java.time.Duration.ofDays(20));
        ClockProvider.setFixedClock(lateClock);

        // Intentar otro préstamo debe fallar
        assertThrows(DomainException.class, () -> service.loanBook("ISBN-002", "M-02"));
    }

    @Test
    void calculaMultaEnDevolucionTardia_R5() {
        var loan = service.loanBook("ISBN-003", "M-01");
        // Vencer + 3 días
        Clock lateClock = Clock.offset(ClockProvider.getClock(),
                java.time.Duration.ofDays(17));
        ClockProvider.setFixedClock(lateClock);

        var fee = service.returnBook(loan.getId());
        assertEquals(new BigDecimal("4.5"), fee); // 3 días * 1.5
    }
}
