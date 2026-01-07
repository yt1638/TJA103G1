package com.showise.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.showise.member.model.MemberVO;
import com.showise.message.model.MessageService;
import com.showise.message.model.MessageVO;
import com.showise.movie.model.MovieVO;
import com.showise.notification.preference.model.NotificationPreferenceVO;
import com.showise.notification.showstart.model.NotificationShowstartVO;
import com.showise.session.model.SessionVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageSvc;

    @PostMapping("/update")
    public String update(@Valid MessageVO messageVO,
                         BindingResult result,
                         ModelMap model) {

        if (result.hasErrors()) {
            return "back-end/message/update_message_input";
        }

        messageSvc.updateMessage(messageVO);

        model.addAttribute("success", "- (修改成功)");
        return "redirect:/message/listAllMessage";
    }
    
    @GetMapping("/select_page")
    public String selectPage() {
        return "back-end/message/select_page"; 
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }
    @GetMapping("/update_notificationShowstart_input")
    public String updateNotificationShowstartInput(Model model) {

        NotificationShowstartVO vo = new NotificationShowstartVO();

        vo.setMember(new MemberVO());
        vo.setSession(new SessionVO());

        model.addAttribute("notificationShowstartVO", vo);

        return "back-end/message/update_notificationShowstart_input";
    }
    
    @GetMapping("/update_notificationPreference_input")
    public String updateNotificationPreferenceInput(Model model) {

        NotificationPreferenceVO vo = new NotificationPreferenceVO();
        vo.setMovie(new MovieVO());       
        vo.setMember(new MemberVO());     

        model.addAttribute("notificationPreferenceVO", vo);
        return "back-end/message/update_notificationPreference_input";
    }
        

}


