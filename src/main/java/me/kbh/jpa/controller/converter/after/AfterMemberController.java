package me.kbh.jpa.controller.converter.after;

import lombok.RequiredArgsConstructor;
import me.kbh.jpa.entity.Member;
import me.kbh.jpa.repository.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AfterMemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/after/members/{id}")
    public String findMember(@PathVariable("id") Member member) {
        return member.getUsername();
    }
}