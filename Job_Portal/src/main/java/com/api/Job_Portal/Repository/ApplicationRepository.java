package com.api.Job_Portal.Repository;

import com.api.Job_Portal.Entity.Application;
import com.api.Job_Portal.Entity.JobPost;
import com.api.Job_Portal.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application , Long> {
    List<Application> findByJobSeekerId(Long jobSeekerId);
    Page<Application> findByJobSeekerId(Long jobSeekerId, Pageable pageable);
    Page<Application> findByJobPostId(Long jobPostId, Pageable pageable);
    @Query("SELECT COUNT(a) > 0 FROM Application a WHERE a.jobSeeker = :jobSeeker AND a.jobPost = :jobPost")
    boolean existsByJobSeekerAndJobPost(User jobSeeker, JobPost jobPost);

}
