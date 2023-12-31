package me.kbh.jpa.repository;

import me.kbh.jpa.dto.MemberDto;
import me.kbh.jpa.dto.MemberNativeDto;
import me.kbh.jpa.dto.projection.UsernameOnlyDto;
import me.kbh.jpa.entity.Member;
import me.kbh.jpa.entity.Team;
import me.kbh.jpa.repository.projection.MemberProjection;
import me.kbh.jpa.repository.projection.UsernameOnly;
import me.kbh.jpa.spec.MemberSpec;
import org.assertj.core.api.Assertions;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember =
                memberRepository.findById(savedMember.getId()).get();
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());

        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());

        //JPA 엔티티 동일성 보장
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();

        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();

        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();

        assertThat(deletedCount).isEqualTo(0);
    }

    //페이징 조건과 정렬 조건 설정
    @Test
    public void page() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));
        //when
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
        Page<Member> page = memberRepository.findByAge(10, pageRequest);
        //then
        List<Member> content = page.getContent(); //조회된 데이터
        assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
        assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
        assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
    }

    @Test
    public void bulkUpdate() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception {
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        memberRepository.save(new Member("member1", 10, teamA));
        memberRepository.save(new Member("member2", 20, teamB));

        em.flush();
        em.clear();

        //when
        List<Member> members = memberRepository.findAll();

        //then
        for (Member member : members) {
            member.getTeam().getName();
        }
    }

    @Test
    public void queryHint() throws Exception {
        //given
        memberRepository.save(new Member("member1", 10));

        em.flush();
        em.clear();

        //when
        Member member = memberRepository.findReadOnlyByUsername("member1");
        member.setUsername("member2");

        em.flush(); //Update Query 실행X
    }

    @Test
    void queryLock() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        em.flush();
        em.clear();

        //when
        List<Member> memberList = memberRepository.findLockByUsername("member1");
    }

    //pure.JpaBaseEntity로 테스트
    @Test
    public void jpaEventBaseEntity() throws Exception {
        //given
        Member member = new Member("member1");
        memberRepository.save(member); //@PrePersist
        Thread.sleep(100);
        member.setUsername("member2");

        em.flush(); //@PreUpdate
        em.clear();

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        System.out.println("findMember.createdDate = " + findMember.getCreatedDate());
        //System.out.println("findMember.updatedDate = " + findMember.getUpdatedDate());
    }

    @Test
    void baseEntityTest() throws Exception{
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        Thread.sleep(100)        ;
        member1.setUsername("member2");

        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(1L).orElseThrow(RuntimeException::new);
        System.out.println("findMember.getCreatedBy = " + findMember.getCreatedBy());
        System.out.println("findMember.getLastModifiedBy = " + findMember.getLastModifiedBy());
        System.out.println("findMember.getCreatedDate = " + findMember.getCreatedDate());
        System.out.println("findMember.getLastModifiedDate = " + findMember.getLastModifiedDate());
    }

    @Test
    public void specBasic() throws Exception {
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);
        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();
        //when
        Specification<Member> spec =
                MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);
        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void projections() throws Exception {
        //given
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);

        em.persist(m1);
        em.persist(m2);
        em.flush();
        em.clear();

        //when
        List<UsernameOnly> result =
                memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnly usernameOnly : result) {
            System.out.println("interface open projection usernameOnly.getUsername() = " + usernameOnly.getUsername());
        }

        List<UsernameOnlyDto> result2 =
                memberRepository.findProjectionsDtoByUsername("m1");

        for (UsernameOnlyDto UsernameOnlyDto : result2) {
            System.out.println("class projection UsernameOnlyDto.getUsername() = " + UsernameOnlyDto.getUsername());
        }

        List<UsernameOnlyDto> result3 =
                memberRepository.findProjectionsTypeByUsername("m1", UsernameOnlyDto.class);

        for (UsernameOnlyDto UsernameOnlyDto : result3) {
            System.out.println("dynamic projection UsernameOnlyDto.getUsername = " + UsernameOnlyDto.getUsername());
        }

        //then
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result2.size()).isEqualTo(1);
        Assertions.assertThat(result3.size()).isEqualTo(1);
    }

    @Test
    public void navtiveTest() throws Exception {
        //given
        Team teamA = new Team("teamA");

        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);

        em.persist(m1);
        em.flush();
        em.clear();

        //when
        Member result = memberRepository.findByNativeQuery("m1");
        System.out.println("native result = " + result.getUsername());

        Page<MemberProjection> result2 = memberRepository.findByNativeProjection(PageRequest.of(0,10));
        List<MemberProjection> getContent = result2.getContent();

        getContent.forEach( mp -> {
            System.out.println("member projection and native query = " + mp.getUsername());
            System.out.println("member projection and native query = " + mp.getTeamName());
        });

        //동적 네이티브 쿼리
        String sql = "select m.username as username from member m";
        List<MemberNativeDto> result3 = em.createNativeQuery(sql)
                .setFirstResult(0)
                .setMaxResults(10)
                .unwrap(NativeQuery.class)
                .addScalar("username")
                .setResultTransformer(Transformers.aliasToBean(MemberDto.class))
                .getResultList();

        System.out.println("dynamic native query : " +result3.get(0).getUsername());

    }
}