package github;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class IssueInfo {

    @SerializedName("total_count")
    private Integer totalCount;
    @SerializedName("incomplete_results")
    private Boolean incompleteResults;
    @SerializedName("items")
    private List<ItemsDTO> items;

    public static class ItemsDTO {
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
        private Long id;
        @SerializedName("node_id")
        private String nodeId;
        @SerializedName("number")
        private Long number;
        @SerializedName("title")
        private String title;
        @SerializedName("user")
        private UserDTO user;
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
        private Integer comments;
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
        @SerializedName("reactions")
        private ReactionsDTO reactions;
        @SerializedName("timeline_url")
        private String timelineUrl;
        @SerializedName("performed_via_github_app")
        private Object performedViaGithubApp;
        @SerializedName("state_reason")
        private Object stateReason;
        @SerializedName("score")
        private Double score;

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


        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getNumber() {
            return number;
        }

        public void setNumber(Long number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public UserDTO getUser() {
            return user;
        }

        public void setUser(UserDTO user) {
            this.user = user;
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

        public Integer getComments() {
            return comments;
        }

        public void setComments(Integer comments) {
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

        public ReactionsDTO getReactions() {
            return reactions;
        }

        public void setReactions(ReactionsDTO reactions) {
            this.reactions = reactions;
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

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public static class UserDTO {
            @SerializedName("login")
            private String login;
            @SerializedName("id")
            private Long id;
            @SerializedName("node_id")
            private String nodeId;
            @SerializedName("avatar_url")
            private String avatarUrl;
            @SerializedName("gravatar_id")
            private String gravatarId;
            @SerializedName("url")
            private String url;
            @SerializedName("html_url")
            private String htmlUrl;
            @SerializedName("followers_url")
            private String followersUrl;
            @SerializedName("following_url")
            private String followingUrl;
            @SerializedName("gists_url")
            private String gistsUrl;
            @SerializedName("starred_url")
            private String starredUrl;
            @SerializedName("subscriptions_url")
            private String subscriptionsUrl;
            @SerializedName("organizations_url")
            private String organizationsUrl;
            @SerializedName("repos_url")
            private String reposUrl;
            @SerializedName("events_url")
            private String eventsUrl;
            @SerializedName("received_events_url")
            private String receivedEventsUrl;
            @SerializedName("type")
            private String type;
            @SerializedName("site_admin")
            private Boolean siteAdmin;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public String getNodeId() {
                return nodeId;
            }

            public void setNodeId(String nodeId) {
                this.nodeId = nodeId;
            }

            public String getAvatarUrl() {
                return avatarUrl;
            }

            public void setAvatarUrl(String avatarUrl) {
                this.avatarUrl = avatarUrl;
            }

            public String getGravatarId() {
                return gravatarId;
            }

            public void setGravatarId(String gravatarId) {
                this.gravatarId = gravatarId;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getHtmlUrl() {
                return htmlUrl;
            }

            public void setHtmlUrl(String htmlUrl) {
                this.htmlUrl = htmlUrl;
            }

            public String getFollowersUrl() {
                return followersUrl;
            }

            public void setFollowersUrl(String followersUrl) {
                this.followersUrl = followersUrl;
            }

            public String getFollowingUrl() {
                return followingUrl;
            }

            public void setFollowingUrl(String followingUrl) {
                this.followingUrl = followingUrl;
            }

            public String getGistsUrl() {
                return gistsUrl;
            }

            public void setGistsUrl(String gistsUrl) {
                this.gistsUrl = gistsUrl;
            }

            public String getStarredUrl() {
                return starredUrl;
            }

            public void setStarredUrl(String starredUrl) {
                this.starredUrl = starredUrl;
            }

            public String getSubscriptionsUrl() {
                return subscriptionsUrl;
            }

            public void setSubscriptionsUrl(String subscriptionsUrl) {
                this.subscriptionsUrl = subscriptionsUrl;
            }

            public String getOrganizationsUrl() {
                return organizationsUrl;
            }

            public void setOrganizationsUrl(String organizationsUrl) {
                this.organizationsUrl = organizationsUrl;
            }

            public String getReposUrl() {
                return reposUrl;
            }

            public void setReposUrl(String reposUrl) {
                this.reposUrl = reposUrl;
            }

            public String getEventsUrl() {
                return eventsUrl;
            }

            public void setEventsUrl(String eventsUrl) {
                this.eventsUrl = eventsUrl;
            }

            public String getReceivedEventsUrl() {
                return receivedEventsUrl;
            }

            public void setReceivedEventsUrl(String receivedEventsUrl) {
                this.receivedEventsUrl = receivedEventsUrl;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public Boolean getSiteAdmin() {
                return siteAdmin;
            }

            public void setSiteAdmin(Boolean siteAdmin) {
                this.siteAdmin = siteAdmin;
            }
        }


        public static class ReactionsDTO {
            @SerializedName("url")
            private String url;
            @SerializedName("total_count")
            private Integer totalCount;
            @SerializedName("+1")
            private Integer _$1327;// FIXME check this code
            @SerializedName("-1")
            private Integer $1;
            @SerializedName("laugh")
            private Integer laugh;
            @SerializedName("hooray")
            private Integer hooray;
            @SerializedName("confused")
            private Integer confused;
            @SerializedName("heart")
            private Integer heart;
            @SerializedName("rocket")
            private Integer rocket;
            @SerializedName("eyes")
            private Integer eyes;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public Integer getTotalCount() {
                return totalCount;
            }

            public void setTotalCount(Integer totalCount) {
                this.totalCount = totalCount;
            }

            public Integer get_$1327() {
                return _$1327;
            }

            public void set_$1327(Integer _$1327) {
                this._$1327 = _$1327;
            }

            public Integer get$1() {
                return $1;
            }

            public void set$1(Integer $1) {
                this.$1 = $1;
            }

            public Integer getLaugh() {
                return laugh;
            }

            public void setLaugh(Integer laugh) {
                this.laugh = laugh;
            }

            public Integer getHooray() {
                return hooray;
            }

            public void setHooray(Integer hooray) {
                this.hooray = hooray;
            }

            public Integer getConfused() {
                return confused;
            }

            public void setConfused(Integer confused) {
                this.confused = confused;
            }

            public Integer getHeart() {
                return heart;
            }

            public void setHeart(Integer heart) {
                this.heart = heart;
            }

            public Integer getRocket() {
                return rocket;
            }

            public void setRocket(Integer rocket) {
                this.rocket = rocket;
            }

            public Integer getEyes() {
                return eyes;
            }

            public void setEyes(Integer eyes) {
                this.eyes = eyes;
            }
        }


        public static class LabelsDTO {
            @SerializedName("id")
            private Long id;
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

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
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
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Boolean getIncompleteResults() {
        return incompleteResults;
    }

    public void setIncompleteResults(Boolean incompleteResults) {
        this.incompleteResults = incompleteResults;
    }

    public List<ItemsDTO> getItems() {
        return items;
    }

    public void setItems(List<ItemsDTO> items) {
        this.items = items;
    }
}
