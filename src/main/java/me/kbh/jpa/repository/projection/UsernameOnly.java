package me.kbh.jpa.repository.projection;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    //interface close projection
    //String getUsername();

    //interface open projection
    @Value("#{'전체 select에서 변형한다 -> '+target.username + ' ' + target.age + ' ' + target.team.name}")
    String getUsername();
}