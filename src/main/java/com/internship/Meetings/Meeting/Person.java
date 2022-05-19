package com.internship.Meetings.Meeting;

import org.apache.tomcat.jni.Local;

import java.time.LocalTime;

public class Person {

    private String nickname;
    private String timeAdded;
    public Person(){

    }
    public Person(String nickname) {
        this.nickname = nickname;
        this.timeAdded = LocalTime.now().toString();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(String timeAdded) {
        this.timeAdded = timeAdded;
    }

    @Override
    public String toString() {
        return "Person{" +
                "nickname='" + nickname + '\'' +
                ", timeAdded='" + timeAdded + '\'' +
                '}';
    }
}
