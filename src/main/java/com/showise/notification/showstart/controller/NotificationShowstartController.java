package com.showise.notification.showstart.controller;

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

import com.showise.notification.preference.model.NotificationPreferenceVO;
import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/notification_showstart")
public class NotificationShowstartController {

    @Autowired
    private NotificationShowstartService notificationShowstartSvc;

    @GetMapping("/addNotificationShowstart")
    public String addNotificationShowstart(ModelMap model) {
        model.addAttribute("notificationShowstartVO", new NotificationShowstartVO());
        return "back-end/notification_showstart/addNotificationShowstart";
    }

    @PostMapping("/insert")
    public String insert(@Valid NotificationShowstartVO notificationShowstartVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/notification_showstart/addNotificationPreference";
        }

        notificationShowstartSvc.addNotificationShowstart(notificationShowstartVO);
        model.addAttribute("success", "- (新增成功)");
        return "redirect:/notification_showstart/listAllNotificationPreference";
    }

    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("notiShowstNo") Integer notiShowstNo,
                                    ModelMap model) {

    	NotificationShowstartVO notificationShowstartVO =
    			notificationShowstartSvc.getOneNotificationShowstart(notiShowstNo);

        model.addAttribute("notificationShowstartVO", notificationShowstartVO);
        return "back-end/notification_showstart/update_notificationPreference_input";
    }

    @PostMapping("/update")
    public String update(@Valid NotificationShowstartVO notificationShowstartVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/notification_showstart/update_notificationPreference_input";
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
        model.addAttribute("notificationShowstartData", list);
        return "back-end/notification_showstart/select_page";
    }
    @GetMapping("/")
    public String home() {
        return "index";
    }
    @PostMapping("listNotificationShowstart_ByCompositeQuery")
	public String listAllNotificationShowstartData(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<NotificationShowstartVO> list = notificationShowstartSvc.getAll(map);
		model.addAttribute("notificationShowstartListData", list); 
		return "back-end/notification_showstart/listAllNotificationShowstart";
	}


}

