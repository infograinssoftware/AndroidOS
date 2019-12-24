package com.open_source.modal;

/**
 * Created by and-02 on 23/6/18.
 */

public class ChatMsgModel {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent_type() {
        return content_type;
    }

    public ChatMsgModel(String id, String massage, String fromid, String toid, String chat_id, String send, String msg_type, String time, String to_name, String content_type, String property_id) {
        this.id = id;
        this.massage = massage;
        this.fromid = fromid;
        this.toid = toid;
        this.chat_id = chat_id;
        this.send = send;
        this.msg_type = msg_type;
        this.time = time;
        this.to_name = to_name;

        this.content_type=content_type;
        this.property_id = property_id;
    }

    public ChatMsgModel(String massage, String fromid, String toid, String chat_id, String send, String msg_type, String time, String to_name, String content_type, String property_id) {
        this.massage = massage;
        this.fromid = fromid;
        this.toid = toid;
        this.chat_id = chat_id;
        this.send = send;
        this.msg_type = msg_type;
        this.time = time;
        this.to_name = to_name;
        this.content_type=content_type;
        this.property_id = property_id;
    }

    public ChatMsgModel() {

    }

    // Message content.

    private String id, massage, fromid, toid,chat_id, send,msg_type,time,to_name,property_id,content_type;



    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getFromid() {
        return fromid;
    }

    public void setFromid(String fromid) {
        this.fromid = fromid;
    }

    public String getToid() {
        return toid;
    }

    public void setToid(String toid) {
        this.toid = toid;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(String msg_type) {
        this.msg_type = msg_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    public String getProperty_id() {
        return property_id;
    }

    public void setProperty_id(String property_id) {
        this.property_id = property_id;
    }
}
