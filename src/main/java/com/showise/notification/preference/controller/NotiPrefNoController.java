package com.showise.notification.preference.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.showise.notification.preference.model.NotificationPreferenceService;
import com.showise.notification.preference.model.NotificationPreferenceVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/notification_preference")
public class NotiPrefNoController {

    @Autowired
    private NotificationPreferenceService notificationPreferenceSvc;

    @PostMapping("getOne_For_Display")
    public String getOne_For_Display(
            @NotEmpty(message = "喜好通知編號: 請勿空白")
            @Digits(integer = 4, fraction = 0, message = "喜好通知編號: 請填數字-請勿超過{integer}位數")
            @Min(value = 1, message = "喜好通知編號: 不能小於{value}")
            @Max(value = 9999, message = "喜好通知編號: 不能超過{value}")
            @RequestParam("notiPrefNo") String notiPrefNo,
            ModelMap model) {

        NotificationPreferenceVO notificationPreferenceVO =
                notificationPreferenceSvc.getOneNotificationPreference(Integer.valueOf(notiPrefNo));

        List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);

        if (notificationPreferenceVO == null) {
            model.addAttribute("errorMessage", "查無資料");
            return "back-end/notification_preference/select_page";
        }

        model.addAttribute("notificationPreferenceVO", notificationPreferenceVO);
        return "back-end/notification_preference/select_page";
    }

    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ModelAndView handleError(HttpServletRequest req,
                                    ConstraintViolationException e,
                                    Model model) {

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder strBuilder = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            strBuilder.append(violation.getMessage()).append("<br>");
        }

        List<NotificationPreferenceVO> list = notificationPreferenceSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);

        String message = strBuilder.toString();
        return new ModelAndView(
                "back-end/notification_preference/select_page",
                "errorMessage",
                "請修正以下錯誤:<br>" + message
        );
    }
}
