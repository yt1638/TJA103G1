package com.showise.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.member.model.MemberService;
import com.showise.member.model.MemberVO;
import com.showise.message.model.MailService;
import com.showise.message.model.MessageService;
import com.showise.message.model.MessageVO;
import com.showise.movie.model.MovieVO;
import com.showise.notification.preference.model.NotificationPreferenceVO;
import com.showise.notification.showstart.model.NotificationShowstartVO;
import com.showise.session.model.SessionVO;

import jakarta.validation.Valid;

@Deprecated
//@Controller
//@RequestMapping("/notification_showstart")
public class MessageController {

    @Autowired
    private MessageService messageSvc;

    @Autowired
    private MemberService memberSvc;

    @Autowired
    private MailService mailService; 

   @PostMapping("/update")
    public String update(@Valid MessageVO messageVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/notification_showstart/update_notificationShowstart_input";
        }

        messageSvc.updateMessage(messageVO);

        model.addAttribute("success", "- (修改成功)");
        return "redirect:/notification_showstart/update_notificationShowstart_input";
    }

    @GetMapping("/select_page")
    public String selectPage() {
        return "notification_showstart/update_notificationShowstart_input";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    // === 你的原本頁面：開演通知編輯 ===
    @GetMapping("/update_notificationShowstart_input")
    public String updateNotificationShowstartInput(Model model) {

        NotificationShowstartVO vo = new NotificationShowstartVO();
        vo.setMember(new MemberVO());
        vo.setSession(new SessionVO());

        vo.setNotiShowstScon("親愛的用戶您好：\n此封訊息為提醒您電影開演的時間...");

        model.addAttribute("notificationShowstartVO", vo);

        // ✅ 下拉選單用（你頁面用 memberList 的話就保持 memberList）
        model.addAttribute("memberList", memberSvc.getAll());

        model.addAttribute("pageTitle", "即將開演通知編輯");
        model.addAttribute("content", "notification_showstart/update_notificationShowstart_input :: content");
        return "back-end/layout/admin-layout";
    }

    // ✅ 立即發送：把你右下角選到的信箱 + textarea 內容寄出去
    
    @PostMapping("/sendNow")
    public String sendNow(
            @RequestParam("toEmail") String toEmail,
            @RequestParam("content") String content,
            RedirectAttributes ra
    ) {
        if (toEmail == null || toEmail.isBlank()) {
            ra.addFlashAttribute("error", "請先選擇會員信箱");
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }
        if (content == null || content.isBlank()) {
            ra.addFlashAttribute("error", "訊息內容不可空白");
            return "redirect:/notification_showstart/update_notificationShowstart_input";
        }
        

        String subject = "開演通知";
        mailService.sendTextMail(toEmail, subject, content);

        ra.addFlashAttribute("success", "已寄出到：" + toEmail);
        return "redirect:/notification_showstart/update_notificationShowstart_input";
    }
    

    // === 你的原本頁面：喜好通知編輯 ===
    @GetMapping("/update_notificationPreference_input")
    public String updateNotificationPreferenceInput(Model model) {

        NotificationPreferenceVO vo = new NotificationPreferenceVO();
        vo.setMovie(new MovieVO());
        vo.setMember(new MemberVO());

        vo.setNotiPrefScon("親愛的用戶您好：\n此封訊息為依據您的喜好，所發送推薦電影...");

        model.addAttribute("notificationPreferenceVO", vo);
        model.addAttribute("memberList", memberSvc.getAll());

        model.addAttribute("pageTitle", "喜好通知編輯");
        model.addAttribute("content", "back-end/notification_preference/update_notificationPreference_input");
        return "back-end/layout/admin-layout";
    }
    
    @PostMapping("/sendNow")
    public String sendNow1(
            @RequestParam("toEmail") String toEmail,
            @RequestParam("content") String content,
            RedirectAttributes ra
    ) {
        if (toEmail == null || toEmail.isBlank()) {
            ra.addFlashAttribute("error", "請先選擇會員信箱");
            return "redirect:/notification_preference/update_notificationPreference_input";
        }
        if (content == null || content.isBlank()) {
            ra.addFlashAttribute("error", "訊息內容不可空白");
            return "redirect:/notification_preference/update_notificationPreference_input";
        }
        

        String subject = "開演通知";
        mailService.sendTextMail(toEmail, subject, content);

        ra.addFlashAttribute("success", "已寄出到：" + toEmail);
        return "redirect:/notification_preference/update_notificationPreference_input";
    }
}
