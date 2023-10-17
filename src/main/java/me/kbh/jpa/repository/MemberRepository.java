package me.kbh.jpa.repository;

import me.kbh.jpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //@Query(name = "Member.findByUsername") 생략가능하고 메서드 명으로 매칭 가능하다.
    List<Member> findByUsername(@Param("username") String username);
}