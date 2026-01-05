package com.showise.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.showise.message.model.MessageService;
import com.showise.message.model.MessageVO;

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


}


