package py.com.electoralvoteapp.entities;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table "VOTES".
 */
public class Votes {

    private Long id;
    private String candidate;
    private String createdAt;
    private Integer tableVoteId;
    private Integer vote;

    public Votes() {
    }

    public Votes(Long id) {
        this.id = id;
    }

    public Votes(Long id, String candidate, String createdAt, Integer tableVoteId, Integer vote) {
        this.id = id;
        this.candidate = candidate;
        this.createdAt = createdAt;
        this.tableVoteId = tableVoteId;
        this.vote = vote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCandidate() {
        return candidate;
    }

    public void setCandidate(String candidate) {
        this.candidate = candidate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTableVoteId() {
        return tableVoteId;
    }

    public void setTableVoteId(Integer tableVoteId) {
        this.tableVoteId = tableVoteId;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

}