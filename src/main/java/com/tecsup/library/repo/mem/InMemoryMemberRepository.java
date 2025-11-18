package com.tecsup.library.repo.mem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tecsup.library.model.Member;
import com.tecsup.library.repo.MemberRepository;

public class InMemoryMemberRepository extends MemberRepository {
    private final Map<String, Member> storage = new HashMap<>();

    @Override
    public void addMember(Member member) {
        storage.put(member.getId(), member);
    }

    @Override
    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(storage.get(memberId));
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean existsById(String memberId) {
        return storage.containsKey(memberId);
    }
}
