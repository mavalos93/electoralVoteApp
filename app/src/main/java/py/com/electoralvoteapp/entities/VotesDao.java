package py.com.electoralvoteapp.entities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import py.com.electoralvoteapp.entities.Votes;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "VOTES".
*/
public class VotesDao extends AbstractDao<Votes, Long> {

    public static final String TABLENAME = "VOTES";

    /**
     * Properties of entity Votes.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Candidate = new Property(1, String.class, "candidate", false, "CANDIDATE");
        public final static Property CreatedAt = new Property(2, String.class, "createdAt", false, "CREATED_AT");
        public final static Property TableVoteId = new Property(3, Integer.class, "tableVoteId", false, "TABLE_VOTE_ID");
        public final static Property Vote = new Property(4, Integer.class, "vote", false, "VOTE");
    };


    public VotesDao(DaoConfig config) {
        super(config);
    }
    
    public VotesDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"VOTES\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"CANDIDATE\" TEXT," + // 1: candidate
                "\"CREATED_AT\" TEXT," + // 2: createdAt
                "\"TABLE_VOTE_ID\" INTEGER," + // 3: tableVoteId
                "\"VOTE\" INTEGER);"); // 4: vote
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"VOTES\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Votes entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String candidate = entity.getCandidate();
        if (candidate != null) {
            stmt.bindString(2, candidate);
        }
 
        String createdAt = entity.getCreatedAt();
        if (createdAt != null) {
            stmt.bindString(3, createdAt);
        }
 
        Integer tableVoteId = entity.getTableVoteId();
        if (tableVoteId != null) {
            stmt.bindLong(4, tableVoteId);
        }
 
        Integer vote = entity.getVote();
        if (vote != null) {
            stmt.bindLong(5, vote);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Votes readEntity(Cursor cursor, int offset) {
        Votes entity = new Votes( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // candidate
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // createdAt
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // tableVoteId
            cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4) // vote
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Votes entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setCandidate(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCreatedAt(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTableVoteId(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setVote(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(Votes entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(Votes entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
