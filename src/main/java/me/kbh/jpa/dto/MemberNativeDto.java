package me.kbh.jpa.dto;


public class MemberNativeDto {
    private String username;

    public MemberNativeDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

