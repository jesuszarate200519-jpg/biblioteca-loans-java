package com.tecsup.library.repo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.tecsup.library.model.Member;

public class MemberRepository {
    private final Map<String, Member> members = new HashMap<>();

    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    public Optional<Member> findById(String memberId) {
        return Optional.ofNullable(members.get(memberId));
    }

    public List<Member> findAll() {
        return new ArrayList<>(members.values());
    }

    public boolean existsById(String memberId) {
        return members.containsKey(memberId);
    }
}
