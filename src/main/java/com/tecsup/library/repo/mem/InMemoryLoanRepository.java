package com.tecsup.library.repo.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tecsup.library.model.Loan;

public class InMemoryLoanRepository {
    private final Map<String, Loan> storage = new HashMap<>();

    public void addLoan(Loan loan) {
        storage.put(loan.getId(), loan);
    }

    public Optional<Loan> findById(String loanId) {
        return Optional.ofNullable(storage.get(loanId));
    }

    public List<Loan> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Loan> findByMemberId(String memberId) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : storage.values()) {
            if (loan.getMemberId().equals(memberId)) {
                result.add(loan);
            }
        }
        return result;
    }

    public List<Loan> findActiveLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : storage.values()) {
            if (loan.isActive()) {
                result.add(loan);
            }
        }
        return result;
    }
}
