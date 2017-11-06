package com.example;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "py.com.electoralvoteapp.entities");

        Entity notifications = schema.addEntity("Notifications");
        notifications.addIdProperty();
        notifications.addStringProperty("type");
        notifications.addStringProperty("httpDetail");
        notifications.addIntProperty("status");
        notifications.addStringProperty("sendAppDate");
        notifications.addLongProperty("createdAt");
        notifications.addDateProperty("updatedAt");
        notifications.addStringProperty("observation");
        notifications.addStringProperty("tabletNumber");
        notifications.addIntProperty("queued");



        Entity tableVotes = schema.addEntity("TableVotes");
        tableVotes.addIdProperty();
        tableVotes.addIntProperty("tableVotesId");
        tableVotes.addBooleanProperty("complete");
        tableVotes.addStringProperty("description");
        tableVotes.addIntProperty("numberTable");


        Entity candidates = schema.addEntity("Candidates");
        candidates.addIdProperty();
        candidates.addIntProperty("candidatesId");
        candidates.addBooleanProperty("active");
        candidates.addStringProperty("description");
        candidates.addIntProperty("vote");
        candidates.addStringProperty("createdAt");
        candidates.addStringProperty("updatedAt");

        Entity votes = schema.addEntity("Votes");
        votes.addIdProperty();
        votes.addStringProperty("candidate");
        votes.addStringProperty("createdAt");
        votes.addIntProperty("tableVoteId");
        votes.addIntProperty("vote");

        new DaoGenerator().generateAll(schema, "../app/src/main/java");

    }
}
