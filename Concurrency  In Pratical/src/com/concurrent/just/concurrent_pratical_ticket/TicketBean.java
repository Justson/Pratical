package com.concurrent.just.concurrent_pratical_ticket;

/**
 * <b>@项目名：</b> Helmet<br>
 * <b>@包名：</b>com.ucmap.helmet<br>
 * <b>@创建者：</b> cxz --  just<br>
 * <b>@创建时间：</b> &{DATE}<br>
 * <b>@公司：</b> 宝诺科技<br>
 * <b>@邮箱：</b> cenxiaozhong.qqcom@qq.com<br>
 * <b>@描述</b><br>
 */

public class TicketBean {


    private String ticketBelong = "-1";
    private double ticketMoney = 800;
    private String ticketKey = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TicketBean(int id) {
        this.id = id;
    }

    private int id;

    public String getTicketBelong() {
        return ticketBelong;
    }

    public void setTicketBelong(String ticketBelong) {
        this.ticketBelong = ticketBelong;
    }

    public double getTicketMoney() {
        return ticketMoney;
    }

    public void setTicketMoney(double ticketMoney) {
        this.ticketMoney = ticketMoney;
    }

    public String getTicketKey() {
        return ticketKey;
    }

    public void setTicketKey(String ticketKey) {
        this.ticketKey = ticketKey;
    }
}
