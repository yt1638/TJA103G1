package com.showise.notification.preference.controller;

import java.util.Collections;
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
import com.showise.movie.model.MovieVO;
import com.showise.notification.preference.model.NotificationPreferenceService;
import com.showise.notification.preference.model.NotificationPreferenceVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/notification_preference")
public class NotificationPreferenceController {

    @Autowired
    private NotificationPreferenceService notificationPreferenceSvc;

    // ✅ 比照附件：寄信用同一套 MailService（showstart controller 有用）:contentReference[oaicite:3]{index=3}
    @Autowired
    private MailService mailService;

    // ✅ 下拉選單 memberList
    @Autowired
    private MemberService memberSvc;

    private String renderAdminLayout(Model model, String pageTitle, String contentFragment) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", contentFragment);
        return "back-end/layout/admin-layout";
    }

    @GetMapping("/update_notificationPreference_input")
    public String updateNotificationPreferenceInput(Model model) {

        // ✅ 重要：避免 th:field="*{member.memberId}" / "*{movie.movieId}" 時 NullPointer
        NotificationPreferenceVO vo = new NotificationPreferenceVO();
        vo.setMember(new MemberVO());
        vo.setMovie(new MovieVO());

         vo.setNotiPrefScon("親愛的用戶您好：\n此封訊息為依據您的喜好，所發送推薦電影...");

        model.addAttribute("notificationPreferenceVO", vo);

        // ✅ 下拉選單（跟附件一樣在 input 頁就丟 memberList）:contentReference[oaicite:4]{index=4}
        model.addAttribute("memberList", memberSvc.getAll());

        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/update_notificationPreference_input"
        );
    }

    @PostMapping("/insert")
    public String insert(@Valid NotificationPreferenceVO notificationPreferenceVO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("notificationPreferenceVO", notificationPreferenceVO);
            model.addAttribute("memberList", memberSvc.getAll());
            return renderAdminLayout(
                    model,
                    "通知管理",
                    "back-end/notification_preference/addNotificationPreference :: content"
            );
        }

        notificationPreferenceSvc.addNotificationPreference(notificationPreferenceVO);
        redirectAttributes.addFlashAttribute("success", "- (新增成功)");

        // ✅ redirect 不要加 :: content
        return "redirect:/notification_preference/listAllNotificationPreference";
    }

    @PostMapping("/update")
    public String update(@Valid NotificationPreferenceVO notificationPreferenceVO,
                         BindingResult result,
                         @RequestParam(required = false) String mode,
                         @RequestParam(name = "toEmail", required = false) String toEmail,
                         @RequestParam(name = "notiPrefScon", required = false) String content,
                         @RequestParam(name = "advanceDays", required = false) String advanceDays,
                         RedirectAttributes ra,
                         Model model) {

        if ("sendNow".equals(mode)) {

            if (toEmail == null || toEmail.isBlank()) {
                ra.addFlashAttribute("error", "請先選擇會員信箱");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }
            if (content == null || content.isBlank()) {
                ra.addFlashAttribute("error", "訊息內容不可空白");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }

            try {
                mailService.sendTextMail(toEmail, "喜好通知", content);
                ra.addFlashAttribute("success", "已寄出到：" + toEmail);
            } catch (Exception e) {
                e.printStackTrace();
                ra.addFlashAttribute("error", "寄信失敗：" + e.getMessage());
            }

            return "redirect:/notification_preference/update_notificationPreference_input";
        }

        if ("schedule".equals(mode)) {

            if (advanceDays == null || advanceDays.isBlank()) {
                ra.addFlashAttribute("error", "請先選擇天數");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }
            if (!advanceDays.matches("\\d+")) {
                ra.addFlashAttribute("error", "天數必須為數字");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }
            if (content == null || content.isBlank()) {
                ra.addFlashAttribute("error", "訊息內容不可空白");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }

            ra.addFlashAttribute("success", "已設定預先發送時間：" + advanceDays + " 天");
            return "redirect:/notification_preference/update_notificationPreference_input";
        }

        // ✅ 一般修改 DB
        if (result.hasErrors()) {
            model.addAttribute("notificationPreferenceVO", notificationPreferenceVO);
            model.addAttribute("memberList", memberSvc.getAll());
            return renderAdminLayout(
                    model,
                    "通知管理",
                    "back-end/notification_preference/update_notificationPreference_input :: content"
            );
        }

        notificationPreferenceSvc.updateNotificationPreference(notificationPreferenceVO);
        ra.addFlashAttribute("success", "- (修改成功)");

        return "redirect:/notification_preference/listAllNotificationPreference";
    }

    @GetMapping("/listAllNotificationPreference")
    public String listAll(Model model) {
        List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);

        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/listAllNotificationPreference"
        );
    }

    @GetMapping("/select_page")
    public String selectPage(Model model) {
        List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);

        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/select_page"
        );
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/";
    }

    @PostMapping("/listNotificationPreferences_ByCompositeQuery")
    public String listNotificationPreferences_ByCompositeQuery(HttpServletRequest req, Model model) {

        Map<String, String[]> parameterMap = req.getParameterMap();

        String memberIdStr = getFirstTrimmed(parameterMap, "memberId");
        String movieIdStr  = getFirstTrimmed(parameterMap, "movieId");
        String stimeStr    = getFirstTrimmed(parameterMap, "notiPrefStime");

        java.util.function.Function<String, String> backToSelectPage = (String errorMsg) -> {
            model.addAttribute("errorMessage", errorMsg);
            model.addAttribute("notificationPreferenceListData", Collections.emptyList());
            return renderAdminLayout(
                    model,
                    "通知管理",
                    "back-end/notification_preference/select_page"
            );
        };

        if (memberIdStr.isEmpty() && movieIdStr.isEmpty() && stimeStr.isEmpty()) {
            return backToSelectPage.apply("請至少輸入一個查詢條件");
        }

        if (!memberIdStr.isEmpty() && !isDigits(memberIdStr)) {
            return backToSelectPage.apply("會員編號必須為數字");
        }

        if (!movieIdStr.isEmpty() && !isDigits(movieIdStr)) {
            return backToSelectPage.apply("電影編號必須為數字");
        }

        Integer memberId = memberIdStr.isEmpty() ? null : Integer.valueOf(memberIdStr);
        Integer movieId  = movieIdStr.isEmpty()  ? null : Integer.valueOf(movieIdStr);

        java.sql.Date sendDate = null;
        if (!stimeStr.isEmpty()) {
            try {
                sendDate = java.sql.Date.valueOf(stimeStr);
            } catch (IllegalArgumentException e) {
                return backToSelectPage.apply("日期格式錯誤（請用 yyyy-MM-dd）");
            }
        }

        List<NotificationPreferenceVO> list =
                notificationPreferenceSvc.compositeQuery(memberId, movieId, sendDate);

        model.addAttribute("notificationPreferenceListData", list);

        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/select_page"
        );
    }

    private String getFirstTrimmed(Map<String, String[]> parameterMap, String key) {
        String[] values = parameterMap.get(key);
        if (values == null || values.length == 0 || values[0] == null) {
            return "";
        }
        return values[0].trim();
    }

    private boolean isDigits(String value) {
        return value != null && value.matches("\\d+");
    }
    
}
