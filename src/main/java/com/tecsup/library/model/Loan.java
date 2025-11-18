package com.tecsup.library.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import com.tecsup.library.service.DomainException;

public class Loan {
    private final String id;
    private final String isbn;
    private final String memberId;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String isbn, Member member, LocalDate loanDate, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.isbn = Objects.requireNonNull(isbn, "ISBN no puede ser null");
        this.memberId = Objects.requireNonNull(member.getId(), "Member ID no puede ser null");
        this.loanDate = Objects.requireNonNull(loanDate, "Loan date no puede ser null");
        this.dueDate = Objects.requireNonNull(dueDate, "Due date no puede ser null");
        member.incrementLoans();
    }

    public String getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getLoanDate() { return loanDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    public boolean isActive() { return returnDate == null; }

    public boolean isOverdue(LocalDate today) {
        return isActive() && today.isAfter(dueDate);
    }

    public void markReturned(LocalDate returnDate, Member member) {
        if (this.returnDate != null)
            throw new DomainException("Loan already returned");
        this.returnDate = Objects.requireNonNull(returnDate, "Return date no puede ser null");
        member.decrementLoans();
    }
}
