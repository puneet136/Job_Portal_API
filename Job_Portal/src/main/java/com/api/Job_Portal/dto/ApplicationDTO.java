package com.api.Job_Portal.dto;


public class ApplicationDTO {
    private Long id;
    private Long jobPostId;
    private String jobTitle;
    private Long jobSeekerId;
    private String jobSeekerName;
    private String resumeUrl;
    private String status;

    // Constructors
    public ApplicationDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Long jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Long getJobSeekerId() {
        return jobSeekerId;
    }

    public void setJobSeekerId(Long jobSeekerId) {
        this.jobSeekerId = jobSeekerId;
    }

    public String getJobSeekerName() {
        return jobSeekerName;
    }

    public void setJobSeekerName(String jobSeekerName) {
        this.jobSeekerName = jobSeekerName;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public void setResumeUrl(String resumeUrl) {
        this.resumeUrl = resumeUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ApplicationDTO(Long id, Long jobPostId, String jobTitle, Long jobSeekerId, String jobSeekerName,
                          String resumeUrl, String status) {
        this.id = id;
        this.jobPostId = jobPostId;
        this.jobTitle = jobTitle;
        this.jobSeekerId = jobSeekerId;
        this.jobSeekerName = jobSeekerName;
        this.resumeUrl = resumeUrl;
        this.status = status;
    }
}

