package me.kbh.jpa.controller.converter.before;

import lombok.RequiredArgsConstructor;
import me.kbh.jpa.entity.Member;
import me.kbh.jpa.repository.MemberRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BeforeMemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/before/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getUsername();
    }
}