package com.showise.notification.preference.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

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
import com.showise.movie.model.MovieService;
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

    @Autowired
    private MailService mailService;

    @Autowired
    private MemberService memberSvc;
    
    @Autowired
    private com.showise.movie.model.MovieRepository movieRepo;

    // ✅ 新增：用 movieId 查 DB 取得 MovieVO
    @Autowired
    private MovieService movieSvc;

    private String renderAdminLayout(Model model, String pageTitle, String contentFragment) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", contentFragment);
        return "back-end/layout/admin-layout";
    }

    @GetMapping("/update_notificationPreference_input")
    public String updateNotificationPreferenceInput(Model model) {

        // ✅ 避免 th:field="*{member.memberId}" 時 NullPointer
        NotificationPreferenceVO vo = new NotificationPreferenceVO();
        vo.setMember(new MemberVO());

        // ✅ 不再靠 th:field="*{movie.movieId}"，所以 movie 可不設（但設了也無妨）
        vo.setMovie(new MovieVO());

        vo.setNotiPrefScon("親愛的用戶您好：\n此封訊息為依據您的喜好，所發送推薦電影...");

        model.addAttribute("notificationPreferenceVO", vo);

        // ✅ 會員下拉
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

        return "redirect:/notification_preference/listAllNotificationPreference";
    }

    @PostMapping("/update")
    public String update(@Valid NotificationPreferenceVO notificationPreferenceVO,
                         BindingResult result,
                         @RequestParam(required = false) String mode,
                         @RequestParam(name = "toEmail", required = false) String toEmail,
                         @RequestParam(name = "notiPrefScon", required = false) String content,
                         @RequestParam(name = "advanceDays", required = false) String advanceDays,
                         // ✅ 新增：前端用 name="movieId" 送上來
                         @RequestParam(name = "movieId", required = false) String movieIdStr,
                         RedirectAttributes ra,
                         Model model) {

        // content 若沒帶，用 VO 補
        if (content == null && notificationPreferenceVO != null) {
            content = notificationPreferenceVO.getNotiPrefScon();
        }

        // ========= 共用檢查：movieId =========
        Integer movieId = null;
        if (movieIdStr == null || movieIdStr.isBlank() || !movieIdStr.matches("\\d+")) {
            ra.addFlashAttribute("error", "請輸入正確的 movie_id（數字）");
            return "redirect:/notification_preference/update_notificationPreference_input";
        } else {
            movieId = Integer.valueOf(movieIdStr.trim());
        }

        // ✅ 用 movieId 查 DB，確定電影存在
        MovieVO movieFromDb = movieRepo.findById(movieId).orElse(null);
        if (movieFromDb == null) {
            ra.addFlashAttribute("error", "找不到 movie_id = " + movieId + " 的電影");
            return "redirect:/notification_preference/update_notificationPreference_input";
        }

        // ========= 1) 立即發送 =========
        if ("sendNow".equals(mode)) {

            if (toEmail == null || toEmail.isBlank()) {
                ra.addFlashAttribute("error", "請先選擇會員信箱");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }
            if (content == null || content.isBlank()) {
                ra.addFlashAttribute("error", "訊息內容不可空白");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }

            MemberVO member = findMemberByEmail(toEmail);
            if (member == null) {
                ra.addFlashAttribute("error", "找不到此信箱對應的會員：" + toEmail);
                return "redirect:/notification_preference/update_notificationPreference_input";
            }

            // 組 VO 存 DB
            NotificationPreferenceVO vo = new NotificationPreferenceVO();
            vo.setMember(member);
            vo.setMovie(movieFromDb);
            vo.setNotiPrefScon(content);

            // 立即寄：你 VO 是 @FutureOrPresent，所以用今天 OK
            vo.setNotiPrefStime(java.time.LocalDate.now());
            vo.setNotiPrefStat((short) 0); // 已寄送
            notificationPreferenceSvc.addNotificationPreference(vo);

            try {
                mailService.sendTextMail(toEmail, "喜好通知", content);
                ra.addFlashAttribute("success", "已寄出並新增一筆通知紀錄：" + toEmail + "（movie_id=" + movieId + "）");
            } catch (Exception e) {
                ra.addFlashAttribute("error", "寄信失敗：" + e.getMessage());
            }

            return "redirect:/notification_preference/update_notificationPreference_input";
        }

        // ========= 2) 排程寄送 =========
        if ("schedule".equals(mode)) {

            if (toEmail == null || toEmail.isBlank()) {
                ra.addFlashAttribute("error", "請先選擇會員信箱");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }
            if (content == null || content.isBlank()) {
                ra.addFlashAttribute("error", "訊息內容不可空白");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }
            if (advanceDays == null || advanceDays.isBlank() || !advanceDays.matches("\\d+")) {
                ra.addFlashAttribute("error", "請選擇正確的提前天數");
                return "redirect:/notification_preference/update_notificationPreference_input";
            }

            int days = Integer.parseInt(advanceDays);

            MemberVO member = findMemberByEmail(toEmail);
            if (member == null) {
                ra.addFlashAttribute("error", "找不到此信箱對應的會員：" + toEmail);
                return "redirect:/notification_preference/update_notificationPreference_input";
            }

            NotificationPreferenceVO vo = new NotificationPreferenceVO();
            vo.setMember(member);
            vo.setMovie(movieFromDb);
            vo.setNotiPrefScon(content);

            // 排程寄送日 = 今天 + days
            vo.setNotiPrefStime(java.time.LocalDate.now().plusDays(days));
            vo.setNotiPrefStat((short) 0); // 0=待寄

            notificationPreferenceSvc.addNotificationPreference(vo);

            ra.addFlashAttribute("success", "已建立排程：將於 " + days + " 天後寄出（movie_id=" + movieId + "）");
            return "redirect:/notification_preference/update_notificationPreference_input";
        }

        ra.addFlashAttribute("error", "mode 不正確或未帶入");
        return "redirect:/notification_preference/update_notificationPreference_input";
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

        LocalDate today = LocalDate.now();

        List<NotificationPreferenceVO> list =
                notificationPreferenceSvc.findPendingBySendDate(today);

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

    private MemberVO findMemberByEmail(String email) {
        return memberSvc.getAll().stream()
            .filter(m -> email != null && email.equalsIgnoreCase(m.getEmail()))
            .findFirst().orElse(null);
    }
}
