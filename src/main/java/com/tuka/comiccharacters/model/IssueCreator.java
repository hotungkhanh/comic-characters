package com.tuka.comiccharacters.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "issue_creators")
public class IssueCreator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Issue issue;

    @ManyToOne(optional = false)
    private Creator creator;

    @ElementCollection(targetClass = Role.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "issue_creator_roles",
            joinColumns = @JoinColumn(name = "issue_creator_id")
    )
    @Column(name = "role")
    private Set<Role> roles;

    public IssueCreator() {
    }

    public IssueCreator(Creator creator, Set<Role> roles) {
        this.creator = creator;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
