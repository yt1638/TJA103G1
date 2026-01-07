package com.showise.notification.preference.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.notification.preference.model.NotificationPreferenceService;
import com.showise.notification.preference.model.NotificationPreferenceVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/notification_preference")
public class NotificationPreferenceController {

    @Autowired
    private NotificationPreferenceService notificationPreferenceSvc;

    // ===== 共用：統一回 admin-layout（避免漏設 content 造成 index2 類問題） =====
    private String renderAdminLayout(Model model, String pageTitle, String contentFragment) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", contentFragment);
        return "back-end/layout/admin-layout";
    }

    // 新增頁面（你原方法名稱是 update_input，但其實內容是 new VO，這裡先不改路由避免你前端連結壞掉）
    @GetMapping("/update_notificationPreference_input")
    public String updateNotificationPreferenceInput(Model model) {
        model.addAttribute("notificationPreferenceVO", new NotificationPreferenceVO());

        // ✅ 走 layout
        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/update_notificationPreference_input :: content"
        );
    }

    @PostMapping("/insert")
    public String insert(@Valid NotificationPreferenceVO notificationPreferenceVO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            // ✅ 錯誤時回表單同頁（layout）
            model.addAttribute("notificationPreferenceVO", notificationPreferenceVO);
            return renderAdminLayout(
                    model,
                    "通知管理",
                    "back-end/notification_preference/addNotificationPreference :: content"
            );
        }

        notificationPreferenceSvc.addNotificationPreference(notificationPreferenceVO);
        redirectAttributes.addFlashAttribute("success", "- (新增成功)");

        // ✅ 用 redirect 避免重送
        return "redirect:/notification_preference/listAllNotificationPreference";
    }

    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("notiPrefNo") Integer notiPrefNo,
                                    Model model) {

        NotificationPreferenceVO notificationPreferenceVO =
                notificationPreferenceSvc.getOneNotificationPreference(notiPrefNo);

        model.addAttribute("notificationPreferenceVO", notificationPreferenceVO);

        // ✅ 走 layout
        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/update_notificationPreference_input :: content"
        );
    }

    @PostMapping("/update")
    public String update(@Valid NotificationPreferenceVO notificationPreferenceVO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("notificationPreferenceVO", notificationPreferenceVO);
            return renderAdminLayout(
                    model,
                    "通知管理",
                    "back-end/notification_preference/update_notificationPreference_input :: content"
            );
        }

        notificationPreferenceSvc.updateNotificationPreference(notificationPreferenceVO);
        redirectAttributes.addFlashAttribute("success", "- (修改成功)");

        // ✅ 建議 redirect 回列表（最乾淨）
        return "redirect:/notification_preference/listAllNotificationPreference";
    }

    @GetMapping("/listAllNotificationPreference")
    public String listAll(Model model) {
        List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);

        // ✅ 走 layout
        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/listAllNotificationPreference :: content"
        );
    }

    @GetMapping("/select_page")
    public String selectPage(Model model) {
        model.addAttribute("notificationPreferenceListData", Collections.emptyList());

        // ✅ 走 layout
        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/select_page :: content"
        );
    }

    // ⚠️ 這個 mapping "/notification_preference/" 很容易跟首頁混淆
    // 你 log 有顯示 WelcomePage: index。建議不要在這裡 return "index"
    // 如果你真的要導回首頁，建議 redirect:/index 或 redirect:/ (由 welcome page 接)
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

        // ✅ 共用：遇到錯誤時，要回 select_page 且顯示錯誤訊息 + 維持畫面
        java.util.function.Function<String, String> backToSelectPage = (String errorMsg) -> {
            model.addAttribute("errorMessage", errorMsg);
            // 你想預設空白(查無資料)就給空集合；想顯示全部就改成 getAll()
            model.addAttribute("notificationPreferenceListData", Collections.emptyList());
            return renderAdminLayout(
                    model,
                    "通知管理",
                    "back-end/notification_preference/select_page :: content"
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

        // ✅ 查詢結果也回同一個 select_page（layout）
        return renderAdminLayout(
                model,
                "通知管理",
                "back-end/notification_preference/select_page :: content"
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
