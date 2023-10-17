package me.kbh.jpa.repository;

import me.kbh.jpa.dto.MemberDto;
import me.kbh.jpa.dto.projection.UsernameOnlyDto;
import me.kbh.jpa.entity.Member;
import me.kbh.jpa.repository.projection.MemberProjection;
import me.kbh.jpa.repository.projection.UsernameOnly;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, JpaSpecificationExecutor<Member> {
    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    //@Query(name = "Member.findByUsername") 생략가능하고 메서드 명으로 매칭 가능하다.
    //List<Member> findByUsername(@Param("username") String username);

    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new me.kbh.jpa.dto.MemberDto(m.id, m.username, t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    @Query("select m from Member m where m.username = :name")
    Member findMembers(@Param("name") String username);

    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    //컬렉션
    //List<Member> findByUsername(String name);
    //단건
    //Member findByUsername(String name);
    //단건 Optional
    //Optional<Member> findByUsername(String name);

    //count 쿼리 사용
    //Page<Member> findByUsername(String name, Pageable pageable);

    //count 쿼리 사용 안함
    //Slice<Member> findByUsername(String name, Pageable pageable);

    //count 쿼리 사용 안함
    //List<Member> findByUsername(String name, Pageable pageable);
    //List<Member> findByUsername(String name, Sort sort);

    Page<Member> findByAge(int age, Pageable pageable);

    //count query를 분리할 수 있다.
    @Query(value = "select m from Member m",
           countQuery = "select count(m.username) from Member m")
    Page<Member> findMemberAllCountBy(Pageable pageable);

    @Modifying
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    //공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    //JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    //메서드 이름으로 쿼리에서 특히 편리하다.
    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(String username);

    @EntityGraph("Member.all")
    @Query("select m from Member m")
    List<Member> findMemberEntityNamedEntityGraph();

    @QueryHints(value =
        @QueryHint(
                name = "org.hibernate.readOnly",
                value = "true"
        )
    )
    Member findReadOnlyByUsername(String username);

    @QueryHints(
            value = {
                    @QueryHint(
                            name = "org.hibernate.readOnly",
                            value = "true"
                    )
            }
            , forCounting = true
    )
    Page<Member> findByUsername(String name, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);

    //interface closed projection
    List<UsernameOnly> findProjectionsByUsername(String username);
    List<UsernameOnlyDto> findProjectionsDtoByUsername(String username);

    //동적 프로젝션
    <T> List<T> findProjectionsTypeByUsername(String username, Class<T> type);

    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName " +
            "FROM member m left join team t ON m.team_id = t.team_id",
            countQuery = "SELECT count(*) from member",
            nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}