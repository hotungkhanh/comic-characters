package com.tuka.comiccharacters.dao;

import com.tuka.comiccharacters.model.Issue;

import java.util.List;

public interface IssueDao {
    void save(Issue issue);

    Issue findById(Long id);

    List<Issue> findAll();

    void deleteById(Long id);
}
