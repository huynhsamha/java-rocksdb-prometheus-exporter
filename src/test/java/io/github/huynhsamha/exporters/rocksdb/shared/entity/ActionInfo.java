package io.github.huynhsamha.exporters.rocksdb.shared.entity;

import java.io.Serializable;

public class ActionInfo implements Serializable {

    private long id;
    private int uid;
    private String message;

    public ActionInfo(long id, int uid, String message) {
        this.id = id;
        this.uid = uid;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ActionInfo{" +
                "id=" + id +
                ", uid=" + uid +
                ", message='" + message + '\'' +
                '}';
    }
}
