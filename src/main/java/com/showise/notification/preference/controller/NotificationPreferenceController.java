package com.showise.notification.preference.controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.message.model.MessageService;
import com.showise.message.model.MessageVO;
import com.showise.notification.preference.model.NotificationPreferenceService;
import com.showise.notification.preference.model.NotificationPreferenceVO;

@Controller
@RequestMapping("/notification_preference")
public class NotificationPreferenceController {

    @Autowired
    NotificationPreferenceService notiSvc;
    
    @Autowired
    MessageService messageService;
    

    /* =========================
     * 一進頁面：ListAll
     * ========================= */
    @GetMapping("/listAll")
    public String listAll(Model model) {

        List<NotificationPreferenceVO> list = notiSvc.getAll();
        model.addAttribute("notificationPreferenceListData", list);
        model.addAttribute("pageTitle","通知管理");
        model.addAttribute("content","back-end/notification_preference/select_page :: content");

        return "back-end/layout/admin-layout";
    }

    /* =========================
     * 複合查詢
     * ========================= */
    @PostMapping("/listNotificationPreferences_ByCompositeQuery")
    public String compositeQuery(
            @RequestParam(required = false) Integer memberId,
            @RequestParam(required = false) Integer movieId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate notiPrefStime,

            Model model) {

        // 👉 LocalDate → java.util.Date
        Date sendDate = null;
        if (notiPrefStime != null) {
            sendDate = Date.from(
                    notiPrefStime
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant()
            );
        }

        List<NotificationPreferenceVO> list =
                notiSvc.compositeQuery(memberId, movieId, sendDate);

        model.addAttribute("notificationPreferenceListData", list);
        model.addAttribute("pageTitle","通知管理");
        model.addAttribute("content","back-end/notification_preference/select_page :: content");

        return "back-end/layout/admin-layout";
    }
    
    /* =========================
     * 進入編輯頁（喜好通知 type = 1）
     * ========================= */
    @GetMapping("/update_notificationPreference_input")
    public String editPreferenceTemplate(Model model) {

        // type = 1 → 喜好通知
        MessageVO messageVO = messageService.findByType(1);

        model.addAttribute("messageVO", messageVO);
        model.addAttribute("pageTitle","通知管理");
        model.addAttribute("content","back-end/notification_preference/update_notificationPreference_input :: content");

        return "back-end/layout/admin-layout";
    }

    /* =========================
     * 儲存範本內容 + 提前小時
     * ========================= */
    @PostMapping("/update")
    public String updatePreferenceTemplate(
            @RequestParam("msgContent") String msgContent,
            @RequestParam("preHours") Integer preHours,
            RedirectAttributes redirectAttributes) {

        try {
            MessageVO messageVO = messageService.findByType(1);

            messageVO.setMsgContent(msgContent);
            messageVO.setPreHours(preHours);

            messageService.updateMessage(messageVO);

            redirectAttributes.addFlashAttribute(
                    "success", "喜好通知範本已成功儲存"
            );
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute(
                    "error", "儲存失敗，請稍後再試"
            );
        }

        return "redirect:/notification_preference/update_notificationPreference_input";
    }
}
