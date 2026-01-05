package com.showise.notification.showstart.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/notification_showstart")
public class NotificationShowstartController {

    @Autowired
    private NotificationShowstartService notificationShowstartSvc;

    @GetMapping("/update_notificationShowstart_input")
    public String updateNotificationShowstartInput(Model model) {

        // ⚠️ 一定要放一個 notificationShowstartVO 給 th:object 用
        model.addAttribute("notificationShowstartVO", new NotificationShowstartVO());

        return "back-end/notification_showstart/update_notificationShowstart_input";
    }

    @PostMapping("/insert")
    public String insert(@Valid NotificationShowstartVO notificationShowstartVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/notification_showstart/addNotificationShowstart";
        }

        notificationShowstartSvc.addNotificationShowstart(notificationShowstartVO);
        model.addAttribute("success", "- (新增成功)");
        return "redirect:/notification_showstart/listAllNotificationShowstart";
    }

    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("notiShowstNo") Integer notiShowstNo,
                                    ModelMap model) {

    	NotificationShowstartVO notificationShowstartVO =
    			notificationShowstartSvc.getOneNotificationShowstart(notiShowstNo);

        model.addAttribute("notificationShowstartVO", notificationShowstartVO);
        return "back-end/notification_showstart/update_notificationShowstart_input";
    }

    @PostMapping("/update")
    public String update(@Valid NotificationShowstartVO notificationShowstartVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/notification_showstart/update_notificationShowstart_input";
        }

        notificationShowstartSvc.updateNotificationShowstart(notificationShowstartVO);

        model.addAttribute("success", "- (修改成功)");
        return "redirect:/notification_showstart/listAllNotificationShowstart";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("notiShowstNo") Integer notiShowstNo,
                         ModelMap model) {


        model.addAttribute("success", "- (刪除成功)");
        return "redirect:/notification_showstart/listAllNotificationShowstart";
    }

    @GetMapping("/listAllNotificationShowstart")
    public String listAll(ModelMap model) {
        List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
        model.addAttribute("notificationShowstartData", list);
        return "back-end/notification_showstart/listAllNotificationShowstart";
    }
    @GetMapping("/select_page")
    public String selectPage(ModelMap model) {
        List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
        model.addAttribute("notificationShowstartVOListData", list);
        return "back-end/notification_showstart/select_page";
    }
    @GetMapping("/")
    public String home() {
        return "index";
    }
    @PostMapping("listNotificationShowstarts_ByCompositeQuery")
    public String listAllNotificationShowstart(HttpServletRequest req, Model model) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, String[]> criteria = new HashMap<>();

        String notiShowstNo = getFirstTrimmed(parameterMap, "notiShowstNo");
        String memberId    = getFirstTrimmed(parameterMap, "memberId");
        String sessionId   = getFirstTrimmed(parameterMap, "sessionId");
        String stime       = getFirstTrimmed(parameterMap, "notiShowstStime");

        if (notiShowstNo.isEmpty() && memberId.isEmpty() && sessionId.isEmpty() && stime.isEmpty()) {
            model.addAttribute("errorMessage", "請至少輸入一個查詢條件");
            return "back-end/notification_showstart/select_page";
        }

        if (!notiShowstNo.isEmpty()) {
            if (!isDigits(notiShowstNo)) {
                model.addAttribute("errorMessage", "通知編號必須為數字");
                return "back-end/notification_showstart/select_page";
            }
            criteria.put("notiShowstNo", new String[] { notiShowstNo });
        }

        if (!memberId.isEmpty()) {
            if (!isDigits(memberId)) {
                model.addAttribute("errorMessage", "會員編號必須為數字");
                return "back-end/notification_showstart/select_page";
            }
            criteria.put("memberId", new String[] { memberId });
        }

        if (!sessionId.isEmpty()) {
            if (!isDigits(sessionId)) {
                model.addAttribute("errorMessage", "場次編號必須為數字");
                return "back-end/notification_showstart/select_page";
            }
            criteria.put("sessionId", new String[] { sessionId });
        }

        if (!stime.isEmpty()) {
            criteria.put("notiShowstStime", new String[] { stime });
        }

        List<NotificationShowstartVO> list = notificationShowstartSvc.getAll(criteria);
        model.addAttribute("notificationShowstartData", list);
        return "back-end/notification_showstart/listAllNotificationShowstart";
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

