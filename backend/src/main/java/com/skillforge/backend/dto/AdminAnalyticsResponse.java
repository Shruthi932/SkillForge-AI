package com.skillforge.backend.dto;

public class AdminAnalyticsResponse {

    private final long totalUsers;
    private final long totalStudents;
    private final long totalTrainers;
    private final long totalAdmins;
    private final long recentSignups;

    public AdminAnalyticsResponse(long totalUsers,
                                  long totalStudents,
                                  long totalTrainers,
                                  long totalAdmins,
                                  long recentSignups) {
        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.totalTrainers = totalTrainers;
        this.totalAdmins = totalAdmins;
        this.recentSignups = recentSignups;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public long getTotalTrainers() {
        return totalTrainers;
    }

    public long getTotalAdmins() {
        return totalAdmins;
    }

    public long getRecentSignups() {
        return recentSignups;
    }
}
