package com.showise.notification.showstart.controller;

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

import com.showise.notification.showstart.model.NotificationShowstartService;
import com.showise.notification.showstart.model.NotificationShowstartVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/notification_showstart")
public class NotiShowstNoController {

    @Autowired
    private NotificationShowstartService notificationShowstartSvc;

    @PostMapping("getOne_For_Display")
    public String getOne_For_Display(
            @NotEmpty(message = "開演通知編號: 請勿空白")
            @Digits(integer = 4, fraction = 0, message = "開演通知編號: 請填數字-請勿超過{integer}位數")
            @Min(value = 1, message = "開演通知編號: 不能小於{value}")
            @Max(value = 9999, message = "開演通知編號: 不能超過{value}")
            @RequestParam("notiShowstNo") String notiShowstNo,
            ModelMap model) {

        // 2.開始查詢資料
    	NotificationShowstartVO notificationShowstartVO =
    			notificationShowstartSvc.getOneNotificationShowstart(Integer.valueOf(notiShowstNo));

        // select_page 需要的列表資料
        List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
        model.addAttribute("notificationShowstartListData", list);

        if (notificationShowstartVO == null) {
            model.addAttribute("errorMessage", "查無資料");
            return "back-end/notification_showstart/select_page";
        }

        // 3.查詢完成
        model.addAttribute("notificationShowstartVO", notificationShowstartVO);
        return "back-end/notification_showstart/select_page";
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

        // 回 select_page 仍需要列表資料
        List<NotificationShowstartVO> list = notificationShowstartSvc.getAll();
        model.addAttribute("notificationShowstartListData", list);

        String message = strBuilder.toString();
        return new ModelAndView(
                "back-end/notification_showstart/select_page",
                "errorMessage",
                "請修正以下錯誤:<br>" + message
        );
    }
}
