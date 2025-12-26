package com.showise.notification.preference.model;

import java.io.Serializable;
import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.showise.member.model.MemberVO;
import com.showise.movie.model.MovieVO;

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
@Table(name = "notification_preference")
public class NotificationPreferenceVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noti_prefNO")
    private Integer notiPrefNo;

    /** 多對一：每筆通知偏好對應一部電影 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    @NotNull(message = "電影編號: 請勿空白")
    private MovieVO movie;

    /** 多對一：每筆通知偏好對應一個會員 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    @NotNull(message = "會員編號: 請勿空白")
    private MemberVO member;

    @NotBlank(message = "通知內容: 請勿空白")
    @Column(name = "noti_prefSCON", nullable = false)
    private String notiPrefScon;

    @NotNull(message = "通知日期: 請勿空白")
    @Future(message = "日期必須是在今日(不含)之後")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "noti_prefSTIME", nullable = false)
    private Date notiPrefStime;

    @NotNull(message = "通知狀態: 請勿空白")
    @Column(name = "noti_prefSTAT", nullable = false)
    private Short notiPrefStat;

    public NotificationPreferenceVO() {}

    public Integer getNotiPrefNo() {
        return notiPrefNo;
    }
    public void setNotiPrefNo(Integer notiPrefNo) {
        this.notiPrefNo = notiPrefNo;
    }

//    public Integer getMovieId() {
//        return movieId;
//    }
//
//    public void setMovieId(Integer movieId) {
//        this.movieId = movieId;
//    }
//    
//    public Integer getMemberId() {
//        return memberId;
//    }
//
//    public void setMemberId(Integer memberId) {
//        this.memberId = memberId;
//    }
    public MovieVO getMovie() {
        return movie;
    }
    public void setMovie(MovieVO movie) {
        this.movie = movie;
    }

    public MemberVO getMember() {
        return member;
    }
    public void setMember(MemberVO member) {
        this.member = member;
    }

    public String getNotiPrefScon() {
        return notiPrefScon;
    }
    public void setNotiPrefScon(String notiPrefScon) {
        this.notiPrefScon = notiPrefScon;
    }

    public Date getNotiPrefStime() {
        return notiPrefStime;
    }
    public void setNotiPrefStime(Date notiPrefStime) {
        this.notiPrefStime = notiPrefStime;
    }

    public Short getNotiPrefStat() {
        return notiPrefStat;
    }
    public void setNotiPrefStat(Short notiPrefStat) {
        this.notiPrefStat = notiPrefStat;
    }
}
