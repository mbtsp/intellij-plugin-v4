package github;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Issue {

    @SerializedName("url")
    private String url;
    @SerializedName("repository_url")
    private String repositoryUrl;
    @SerializedName("labels_url")
    private String labelsUrl;
    @SerializedName("comments_url")
    private String commentsUrl;
    @SerializedName("events_url")
    private String eventsUrl;
    @SerializedName("html_url")
    private String htmlUrl;
    @SerializedName("id")
    private String id;
    @SerializedName("node_id")
    private String nodeId;
    @SerializedName("number")
    private String number;
    @SerializedName("title")
    private String title;
    @SerializedName("labels")
    private List<LabelsDTO> labels;
    @SerializedName("state")
    private String state;
    @SerializedName("locked")
    private Boolean locked;
    @SerializedName("assignee")
    private Object assignee;
    @SerializedName("assignees")
    private List<?> assignees;
    @SerializedName("milestone")
    private Object milestone;
    @SerializedName("comments")
    private String comments;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;
    @SerializedName("closed_at")
    private Object closedAt;
    @SerializedName("author_association")
    private String authorAssociation;
    @SerializedName("active_lock_reason")
    private Object activeLockReason;
    @SerializedName("body")
    private String body;
    @SerializedName("closed_by")
    private Object closedBy;
    @SerializedName("timeline_url")
    private String timelineUrl;
    @SerializedName("performed_via_github_app")
    private Object performedViaGithubApp;
    @SerializedName("state_reason")
    private Object stateReason;


    public static class LabelsDTO {
        @SerializedName("id")
        private String id;
        @SerializedName("node_id")
        private String nodeId;
        @SerializedName("url")
        private String url;
        @SerializedName("name")
        private String name;
        @SerializedName("color")
        private String color;
        @SerializedName("default")
        private Boolean defaultX;
        @SerializedName("description")
        private String description;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Boolean getDefaultX() {
            return defaultX;
        }

        public void setDefaultX(Boolean defaultX) {
            this.defaultX = defaultX;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getLabelsUrl() {
        return labelsUrl;
    }

    public void setLabelsUrl(String labelsUrl) {
        this.labelsUrl = labelsUrl;
    }

    public String getCommentsUrl() {
        return commentsUrl;
    }

    public void setCommentsUrl(String commentsUrl) {
        this.commentsUrl = commentsUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<LabelsDTO> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelsDTO> labels) {
        this.labels = labels;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public Object getAssignee() {
        return assignee;
    }

    public void setAssignee(Object assignee) {
        this.assignee = assignee;
    }

    public List<?> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<?> assignees) {
        this.assignees = assignees;
    }

    public Object getMilestone() {
        return milestone;
    }

    public void setMilestone(Object milestone) {
        this.milestone = milestone;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Object closedAt) {
        this.closedAt = closedAt;
    }

    public String getAuthorAssociation() {
        return authorAssociation;
    }

    public void setAuthorAssociation(String authorAssociation) {
        this.authorAssociation = authorAssociation;
    }

    public Object getActiveLockReason() {
        return activeLockReason;
    }

    public void setActiveLockReason(Object activeLockReason) {
        this.activeLockReason = activeLockReason;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Object getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(Object closedBy) {
        this.closedBy = closedBy;
    }

    public String getTimelineUrl() {
        return timelineUrl;
    }

    public void setTimelineUrl(String timelineUrl) {
        this.timelineUrl = timelineUrl;
    }

    public Object getPerformedViaGithubApp() {
        return performedViaGithubApp;
    }

    public void setPerformedViaGithubApp(Object performedViaGithubApp) {
        this.performedViaGithubApp = performedViaGithubApp;
    }

    public Object getStateReason() {
        return stateReason;
    }

    public void setStateReason(Object stateReason) {
        this.stateReason = stateReason;
    }
}
