package com.showise.notification.preference.controller;

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

import com.showise.notification.preference.model.NotificationPreferenceService;
import com.showise.notification.preference.model.NotificationPreferenceVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/notification_preference")
public class NotificationPreferenceController {

    @Autowired
    private NotificationPreferenceService notificationPreferenceSvc;

    @GetMapping("/addNotificationPreference")
    public String addNotificationPreference(ModelMap model) {
        model.addAttribute("notificationPreferenceVO", new NotificationPreferenceVO());
        return "back-end/notification_preference/addNotificationPreference";
    }

    @PostMapping("/insert")
    public String insert(@Valid NotificationPreferenceVO notificationPreferenceVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/notification_preference/addNotificationPreference";
        }

        notificationPreferenceSvc.addNotificationPreference(notificationPreferenceVO);
        model.addAttribute("success", "- (新增成功)");
        return "redirect:/notification_preference/listAllNotificationPreference";
    }
    

    @PostMapping("/getOne_For_Update")
    public String getOne_For_Update(@RequestParam("notiPrefNo") Integer notiPrefNo,
                                    ModelMap model) {

        NotificationPreferenceVO notificationPreferenceVO =
                notificationPreferenceSvc.getOneNotificationPreference(notiPrefNo);

        model.addAttribute("notificationPreferenceVO", notificationPreferenceVO);
        return "back-end/notification_preference/update_notificationPreference_input";
    }

    @PostMapping("/update")
    public String update(@Valid NotificationPreferenceVO notificationPreferenceVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/notification_preference/update_notificationPreference_input";
        }

        notificationPreferenceSvc.updateNotificationPreference(notificationPreferenceVO);

        model.addAttribute("success", "- (修改成功)");
        return "redirect:/notification_preference/listAllNotificationPreference";
    }



    @GetMapping("/listAllNotificationPreference")
    public String listAll(ModelMap model) {
        List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);
        return "back-end/notification_preference/listAllNotificationPreference";
    }
    @GetMapping("/select_page")
    public String selectPage(ModelMap model) {
        List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);
        return "back-end/notification_preference/select_page";
    }
    @GetMapping("/")
    public String home() {
        return "index";
    }
	@PostMapping("listNotificationPreferences_ByCompositeQuery")
	public String listAllNotificationPreference(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll(map);
		model.addAttribute("notificationPreferenceListData", list); // for listAllEmp.html 第85行用
		return "back-end/notification_preference/listAllNotificationPreference";
	}


}

