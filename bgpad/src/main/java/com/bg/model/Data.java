package com.bg.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017-02-14.
 */

public class Data extends DataSupport{
    private long id;
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


}
