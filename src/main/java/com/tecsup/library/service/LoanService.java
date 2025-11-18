package com.tecsup.library.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tecsup.library.model.Book;
import com.tecsup.library.model.Loan;
import com.tecsup.library.model.Member;
import com.tecsup.library.repo.mem.InMemoryBookRepository;
import com.tecsup.library.repo.mem.InMemoryLoanRepository;
import com.tecsup.library.repo.mem.InMemoryMemberRepository;
import com.tecsup.library.util.ClockProvider;

public class LoanService {

    private final InMemoryBookRepository bookRepo;
    private final InMemoryMemberRepository memberRepo;
    private final InMemoryLoanRepository loanRepo;

    public LoanService(InMemoryBookRepository bookRepo,
                       InMemoryMemberRepository memberRepo,
                       InMemoryLoanRepository loanRepo) {
        this.bookRepo = bookRepo;
        this.memberRepo = memberRepo;
        this.loanRepo = loanRepo;
    }

    /** Prestar un libro a un miembro */
    public Loan loanBook(String isbn, String memberId) {
        Book book = bookRepo.findByIsbn(isbn)
                .orElseThrow(() -> new DomainException("Book not found"));
        if (!book.isAvailable())
            throw new DomainException("Book already loaned");

        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new DomainException("Member not found"));

        if (member.getActiveLoans() >= 3)
            throw new DomainException("Max 3 loans per member");

        if (hasOverdueLoans(member))
            throw new DomainException("Member has overdue loans");

        LocalDate now = LocalDate.now(ClockProvider.getClock());
        Loan loan = new Loan(isbn, member, now, now.plusDays(14));
        book.markAsLoaned();
        loanRepo.addLoan(loan);
        return loan;
    }

    /** Devolver un libro y calcular multa si corresponde */
    public BigDecimal returnBook(String loanId) {
        Loan loan = loanRepo.findById(loanId)
                .orElseThrow(() -> new DomainException("Loan not found"));

        Member member = memberRepo.findById(loan.getMemberId())
                .orElseThrow(() -> new DomainException("Member not found"));

        Book book = bookRepo.findByIsbn(loan.getIsbn())
                .orElseThrow(() -> new DomainException("Book not found"));

        LocalDate today = LocalDate.now(ClockProvider.getClock());
        loan.markReturned(today, member);
        book.markAsReturned();

        long overdueDays = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), today);
        if (overdueDays <= 0) return BigDecimal.ZERO;

        return BigDecimal.valueOf(overdueDays * 1.5);
    }

    /** Verifica si un miembro tiene préstamos vencidos */
    private boolean hasOverdueLoans(Member member) {
        return loanRepo.findByMemberId(member.getId()).stream()
                .anyMatch(l -> l.isActive() && l.getDueDate().isBefore(LocalDate.now(ClockProvider.getClock())));
    }

    /** Opcionales: Listados de préstamos */
    public List<Loan> listAllLoans() {
        return loanRepo.findAll();
    }

    public List<Loan> listActiveLoans() {
        return loanRepo.findActiveLoans();
    }

    public List<Loan> listLoansByMember(String memberId) {
        return loanRepo.findByMemberId(memberId);
    }
}
