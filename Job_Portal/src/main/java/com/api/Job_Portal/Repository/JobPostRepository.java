package com.api.Job_Portal.Repository;

import com.api.Job_Portal.Entity.JobPost;
import com.api.Job_Portal.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
    List<JobPost> findByEmployer(User employer);
}
