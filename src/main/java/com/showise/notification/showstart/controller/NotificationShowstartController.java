package com.showise.notification.showstart.controller;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.message.model.MailService;
import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;
import com.showise.session.model.SessionVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/notification_showstart")
public class NotificationShowstartController {

    @Autowired
    private NotificationShowstartService notificationShowstartSvc;

    @Autowired
    private MailService mailService;

    @Autowired
    private MemberService memberSvc;

    private String renderAdminLayout(Model model, String pageTitle, String contentFragment) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", contentFragment);
        return "back-end/layout/admin-layout";
    }

    @GetMapping("/select_page")
    public String selectPage(Model model) {

        model.addAttribute("memberList", memberSvc.getAll());

        // ✅ 預設顯示全部
        List<NotificationShowstartVO> list = notificationShowstartSvc.compositeQuery(null, null, null);
        model.addAttribute("notificationShowstartVOListData", list);

        return renderAdminLayout(model, "開演通知管理",
                "back-end/notification_showstart/select_page :: content");
    }

    // 進入「開演通知編輯」頁
    @GetMapping("/update_notificationShowstart_input")
    public String updateNotificationShowstartInput(Model model) {

        NotificationShowstartVO vo = new NotificationShowstartVO();
        vo.setMember(new MemberVO());
        vo.setSession(new SessionVO());
        vo.setNotiShowstScon("親愛的用戶您好：\n此封訊息為提醒您電影開演的時間...");

        model.addAttribute("notificationShowstartVO", vo);
        model.addAttribute("memberList", memberSvc.getAll());

        return renderAdminLayout(model, "開演通知編輯",
                "back-end/notification_showstart/update_notificationShowstart_input :: content");
    }

    // 立即發送 / 排程確認（同一支 POST）
    @PostMapping("/update")
    public String update(@Valid NotificationShowstartVO formVO,
                         BindingResult result,
                         @RequestParam(required = false) String mode,
                         @RequestParam(name="memberId", required=false) Integer memberId,
                         @RequestParam(name="sessionId", required=false) Integer sessionId,
                         @RequestParam(name="notiShowstScon", required=false) String content,
                         @RequestParam(name="advanceHours", required=false) Integer advanceHours,
                         RedirectAttributes ra) {

        // content 沒帶就用 VO 補
        if (content == null && formVO != null) content = formVO.getNotiShowstScon();

        // 基本檢查
        if (memberId == null) {
            ra.addFlashAttribute("error", "發送失敗：請先選擇會員");
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }
        if (sessionId == null) {
            ra.addFlashAttribute("error", "發送失敗：請先選擇場次");
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }
        if (content == null || content.isBlank()) {
            ra.addFlashAttribute("error", "發送失敗：訊息內容不可空白");
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }

        MemberVO member = notificationShowstartSvc.getMember(memberId);
        if (member == null) {
            ra.addFlashAttribute("error", "發送失敗：找不到會員 member_id=" + memberId);
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }

        // ✅ 不用 notificationShowstartSvc.getSession(sessionId)（會炸 session_status）
        // ✅ 改成直接查 start_time（不經過 SessionVO 映射）
        Timestamp showTime = notificationShowstartSvc.getSessionStartTime(sessionId);
        if (showTime == null) {
            ra.addFlashAttribute("error", "發送失敗：找不到場次或此場次沒有開演時間 session_id=" + sessionId);
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }

        // ✅ 存 FK 只要 reference，不查表，不觸發 session_status
        SessionVO sessionRef = notificationShowstartSvc.getSessionRef(sessionId);

        try {
            // ========= 1) 立即發送 =========
            if ("sendNow".equals(mode)) {

                String email = member.getEmail();
                if (email == null || email.isBlank()) {
                    ra.addFlashAttribute("error", "發送失敗：此會員沒有 email");
                    return "redirect:/notification_showstart/update_notificationShowstart_input";
                }

                // 先寄信
                mailService.sendTextMail(email.trim(), "開演通知", content.trim());

                // ✅ 寄出後 insert DB：0=已寄、stime=現在
                NotificationShowstartVO vo = new NotificationShowstartVO();
                vo.setMember(member);
                vo.setSession(sessionRef);
                vo.setNotiShowstScon(content.trim());
                vo.setNotiShowstStime(Timestamp.valueOf(LocalDateTime.now()));
                vo.setNotiShowstStat(NotificationShowstartService.STAT_SENT); // ✅ 0 已寄
                notificationShowstartSvc.save(vo);

                // ✅ 成功：顯示綠色訊息（上方）
                ra.addFlashAttribute("success",
                        "發送成功：已寄出並新增一筆通知紀錄：" + email.trim() + " (session_id=" + sessionId + ")");
                return "redirect:/notification_showstart/update_notificationShowstart_input";
            }

            // ========= 2) 排程寄送（提前小時） =========
            if ("schedule".equals(mode)) {
            	
                if (advanceHours == null || advanceHours <= 0) {
                    ra.addFlashAttribute("error", "發送失敗：請選擇正確的提前小時數");
                    return "redirect:/notification_showstart/update_notificationShowstart_input";
                }

                Timestamp sendAt = Timestamp.valueOf(showTime.toLocalDateTime().minusHours(advanceHours));

             // ✅ 新增：避免 sendAt 已經在過去（會被 Scheduler 下一分鐘立刻寄出）
             Timestamp now = Timestamp.valueOf(LocalDateTime.now());
             if (!sendAt.after(now)) {
                 ra.addFlashAttribute("error",
                     "排程失敗：你選的提前 " + advanceHours + " 小時，計算後的寄送時間是 " + sendAt +
                     "，已早於現在時間 " + now + "，系統會視為到期而立即寄出。請改選較小的小時數或改選較晚的場次。");
                 return "redirect:/notification_showstart/update_notificationShowstart_input";
             }

                NotificationShowstartVO vo = new NotificationShowstartVO();
                vo.setMember(member);
                vo.setSession(sessionRef);
                vo.setNotiShowstScon(content.trim());
                vo.setNotiShowstStime(sendAt);
                vo.setNotiShowstStat(NotificationShowstartService.STAT_PENDING); 
                notificationShowstartSvc.save(vo);

                // ✅ 成功：顯示綠色訊息（上方）
                ra.addFlashAttribute("success",
                        "排程成功：已建立通知紀錄，將於 " + sendAt + " 寄出（提前 " + advanceHours + " 小時，session_id=" + sessionId + "）");
                return "redirect:/notification_showstart/update_notificationShowstart_input";
            }

            ra.addFlashAttribute("error", "發送失敗：未知操作 mode=" + mode);
            return "redirect:/notification_showstart/update_notificationShowstart_input";

        } catch (Exception e) {
            // ❌ 任何寄信/存 DB 失敗都走這
            ra.addFlashAttribute("error", "發送失敗：" + e.getMessage());
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }
    }

    // 列出全部
    @GetMapping("/listAllNotificationShowstart")
    public String listAll(Model model) {
        List<NotificationShowstartVO> list = notificationShowstartSvc.compositeQuery(null, null, null);
        model.addAttribute("notificationShowstartVOListData", list);

        return renderAdminLayout(model, "開演通知管理",
                "back-end/notification_showstart/listAllNotificationShowstart :: content");
    }

    // 查詢頁：依條件查詢
    @PostMapping("/listNotificationShowstarts_ByCompositeQuery")
    public String listNotificationShowstarts_ByCompositeQuery(HttpServletRequest req, Model model) {

        Map<String, String[]> parameterMap = req.getParameterMap();

        String memberIdStr = getFirstTrimmed(parameterMap, "memberId");
        String sessionIdStr = getFirstTrimmed(parameterMap, "sessionId");
        String notiShowstStimeStr = getFirstTrimmed(parameterMap, "notiShowstStime");

        java.util.function.Function<String, String> backToSelectPage = (String errorMsg) -> {
            model.addAttribute("errorMessage", errorMsg);
            model.addAttribute("notificationShowstartVOListData", Collections.emptyList());
            return renderAdminLayout(model, "開演通知管理", "back-end/notification_showstart/select_page :: content");
        };

        Integer memberId = null;
        Integer sessionId = null;
        Date stime = null;

        if (!memberIdStr.isEmpty()) {
            if (!isDigits(memberIdStr)) return backToSelectPage.apply("會員編號必須為數字");
            memberId = Integer.valueOf(memberIdStr);
        }
        if (!sessionIdStr.isEmpty()) {
            if (!isDigits(sessionIdStr)) return backToSelectPage.apply("場次編號必須為數字");
            sessionId = Integer.valueOf(sessionIdStr);
        }
        if (!notiShowstStimeStr.isEmpty()) {
            try {
                stime = Date.valueOf(notiShowstStimeStr); // yyyy-MM-dd
            } catch (Exception e) {
                return backToSelectPage.apply("日期格式錯誤，請用 yyyy-MM-dd");
            }
        }

        if (memberId == null && sessionId == null && stime == null) {
            return backToSelectPage.apply("請至少輸入一個查詢條件");
        }

        List<NotificationShowstartVO> list = notificationShowstartSvc.compositeQuery(memberId, sessionId, stime);
        model.addAttribute("notificationShowstartVOListData", list);

        return renderAdminLayout(model, "開演通知管理", "back-end/notification_showstart/select_page :: content");
    }

    private String getFirstTrimmed(Map<String, String[]> parameterMap, String key) {
        String[] values = parameterMap.get(key);
        if (values == null || values.length == 0 || values[0] == null) return "";
        return values[0].trim();
    }

    private boolean isDigits(String value) {
        return value != null && value.matches("\\d+");
    }
}
