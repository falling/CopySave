package com.falling.copysave.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.Date;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by falling on 2017/7/18.
 */
@Entity
public class NoteBean {
    @Id
    private Long id;

    private String copyContent;
    private String comment;
    private Date date;

    @Generated(hash = 74968373)
    public NoteBean(Long id, String copyContent, String comment, Date date) {
        this.id = id;
        this.copyContent = copyContent;
        this.comment = comment;
        this.date = date;
    }

    @Generated(hash = 451626881)
    public NoteBean() {
    }

    public NoteBean(String copyContent) {
        this(null,copyContent,"",new Date());
    }

    public NoteBean(String copyContent, String comment) {
        this(null,copyContent,comment,new Date());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCopyContent() {
        return copyContent;
    }

    public void setCopyContent(String copyContent) {
        this.copyContent = copyContent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
