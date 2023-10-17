package me.kbh.jpa.repository.projection;


//nested projection
public interface NestedClosedProjection {
    String getUsername();

    TeamInfo getTeam();

    interface TeamInfo {
        String getName();
    }
}