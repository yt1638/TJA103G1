package com.showise.notification.showstart.model;

import java.io.Serializable;
import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.showise.member.model.MemberVO;
import com.showise.session.model.SessionVO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "notification_showstart")
public class NotificationShowstartVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_ShowstNO")
    private Integer notiShowstNo;

    /** 多對一：每筆通知對應一個會員 */
    @Column(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @NotNull(message = "會員編號: 請勿空白")
//    private Integer memberId;
    private MemberVO member;

    /** 多對一：每筆通知對應一個場次 */
    @Column(name = "session_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @NotNull(message = "場次: 請勿空白")
//    private Integer sessionId;
    private SessionVO session;

    @NotBlank(message = "通知內容: 請勿空白")
    @Column(name = "noti_ShowstSCON", nullable = false)
    private String notiShowstScon;

    @NotNull(message = "通知日期: 請勿空白")
    @Future(message = "日期必須是在今日(不含)之後")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "noti_showstSTIME", nullable = false)
    private Date notiShowstStime;

    @NotNull(message = "通知狀態: 請勿空白")
    @Column(name = "noti_showstSTAT", nullable = false)
    private Short notiShowstStat;

    public NotificationShowstartVO() {}

    public Integer getNotiShowstNo() {
        return notiShowstNo;
    }
    public void setNotiShowstNo(Integer notiShowstNo) {
        this.notiShowstNo = notiShowstNo;
    }

//    public Integer getmemberId() {
//        return memberId;
//    }
//
//    public void setsessionId(Integer sessionId) {
//        this.memberId = sessionId;
//    }
//    
//    public Integer getMemberId() {
//        return sessionId;
//    }
//
//    public void setMemberId(Integer memberId) {
//        this.sessionId = memberId;
//    }
    public MemberVO getMember() {
        return member;
    }
    public void setMember(MemberVO member) {
        this.member = member;
    }

    public SessionVO getSession() {
        return session;
    }
    public void setSession(SessionVO session) {
        this.session = session;
    }

    public String getNotiShowstScon() {
        return notiShowstScon;
    }
    public void setNotiShowstScon(String notiShowstScon) {
        this.notiShowstScon = notiShowstScon;
    }

    public Date getNotiShowstStime() {
        return notiShowstStime;
    }
    public void setNotiShowstStime(Date notiShowstStime) {
        this.notiShowstStime = notiShowstStime;
    }

    public Short getNotiShowstStat() {
        return notiShowstStat;
    }
    public void setNotiShowstStat(Short notiShowstStat) {
        this.notiShowstStat = notiShowstStat;
    }
}
