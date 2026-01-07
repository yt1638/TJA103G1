package com.showise.notification.showstart.controller;

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

import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/notification_showstart")
public class NotificationShowstartController {

    @Autowired
    private NotificationShowstartService notificationShowstartSvc;

    private String renderAdminLayout(Model model, String pageTitle, String contentFragment) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("content", contentFragment);
        return "back-end/layout/admin-layout";
    }

    @GetMapping("/update_notificationShowstart_input")
    public String updateNotificationShowstartInput(Model model) {
        model.addAttribute("notificationShowstartVO", new NotificationShowstartVO());

        return renderAdminLayout(
                model,
                "開演通知管理",
                "back-end/message/update_notificationShowstart_input :: content"
        );
    }

    @PostMapping("/insert")
    public String insert(@Valid NotificationShowstartVO notificationShowstartVO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("notificationShowstartVO", notificationShowstartVO);
            return renderAdminLayout(
                    model,
                    "開演通知管理",
                    "back-end/notification_showstart/addNotificationShowstart :: content"
            );
        }

        notificationShowstartSvc.addNotificationShowstart(notificationShowstartVO);
        redirectAttributes.addFlashAttribute("success", "- (新增成功)");
        return "redirect:/notification_showstart/listAllNotificationShowstart";
    }

    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("notiShowstNo") Integer notiShowstNo,
                                    Model model) {

        NotificationShowstartVO vo =
                notificationShowstartSvc.getOneNotificationShowstart(notiShowstNo);

        model.addAttribute("notificationShowstartVO", vo);

        return renderAdminLayout(
                model,
                "開演通知管理",
                "back-end/notification_showstart/update_notificationShowstart_input :: content"
        );
    }

    @PostMapping("/update")
    public String update(@Valid NotificationShowstartVO notificationShowstartVO,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("notificationShowstartVO", notificationShowstartVO);
            return renderAdminLayout(
                    model,
                    "開演通知管理",
                    "back-end/notification_showstart/update_notificationShowstart_input :: content"
            );
        }

        notificationShowstartSvc.updateNotificationShowstart(notificationShowstartVO);

        redirectAttributes.addFlashAttribute("success", "- (修改成功)");
        return "redirect:/notification_showstart/listAllNotificationShowstart";
    }


    @GetMapping("/listAllNotificationShowstart")
    public String listAll(Model model) {
        List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
        model.addAttribute("notificationShowstartVOListData", list);

        return renderAdminLayout(
                model,
                "開演通知管理",
                "back-end/notification_showstart/listAllNotificationShowstart :: content"
        );
    }

    @GetMapping("/select_page")
    public String selectPage(Model model) {
    	List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
        model.addAttribute("notificationShowstartVOListData", list);
//        model.addAttribute("notificationShowstartVOListData", Collections.emptyList());

        return renderAdminLayout(
                model,
                "開演通知管理",
                "back-end/notification_showstart/select_page :: content"
        );
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/";
    }

    @PostMapping("/listNotificationShowstarts_ByCompositeQuery")
    public String listNotificationShowstarts_ByCompositeQuery(HttpServletRequest req, Model model) {

        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, String[]> criteria = new HashMap<>();

        String memberId    = getFirstTrimmed(parameterMap, "memberId");
        String sessionId   = getFirstTrimmed(parameterMap, "sessionId");
        String notiShowstStime       = getFirstTrimmed(parameterMap, "notiShowstStime");

        java.util.function.Function<String, String> backToSelectPage = (String errorMsg) -> {
            model.addAttribute("errorMessage", errorMsg);
            model.addAttribute("notificationShowstartVOListData", Collections.emptyList());
            return renderAdminLayout(
                    model,
                    "開演通知管理",
                    "back-end/notification_showstart/select_page :: content"
            );
        };

        if (!memberId.isEmpty()) {
            if (!isDigits(memberId)) {
                return backToSelectPage.apply("會員編號必須為數字");
            }
            criteria.put("memberId", new String[] { memberId });
        }

        if (!sessionId.isEmpty()) {
            if (!isDigits(sessionId)) {
                return backToSelectPage.apply("場次編號必須為數字");
            }
            criteria.put("sessionId", new String[] { sessionId });
        }

        if (!notiShowstStime.isEmpty()) {
            criteria.put("notiShowstStime", new String[] { notiShowstStime });
        }

        List<NotificationShowstartVO> list = notificationShowstartSvc.getAll(criteria);

        model.addAttribute("notificationShowstartVOListData", list);

        return renderAdminLayout(
                model,
                "開演通知管理",
                "back-end/notification_showstart/select_page :: content"
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
