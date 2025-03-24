package com.nexus.nexus.MyPackage.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "follows", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "follower_id", "followee_id" })
})
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use referencedColumnName to join on UserModal.userId (a String)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", referencedColumnName = "userId", nullable = false)
    private UserModal follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", referencedColumnName = "userId", nullable = false)
    private UserModal followee;

    public Follow() {
    }

    public Follow(UserModal follower, UserModal followee) {
        this.follower = follower;
        this.followee = followee;
    }

    public Long getId() {
        return id;
    }

    public UserModal getFollower() {
        return follower;
    }

    public void setFollower(UserModal follower) {
        this.follower = follower;
    }

    public UserModal getFollowee() {
        return followee;
    }

    public void setFollowee(UserModal followee) {
        this.followee = followee;
    }
}
