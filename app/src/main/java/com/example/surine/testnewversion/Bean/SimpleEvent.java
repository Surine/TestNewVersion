package com.example.surine.testnewversion.Bean;

/**
 * Created by Surine on 2017/12/17.
 * 1. 管理员界面添加isbn的fragment发出，父类接受
 */

public class SimpleEvent {
    public SimpleEvent(int id, String message) {
        this.id = id;
        this.message = message;
    }

    private int id;
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
