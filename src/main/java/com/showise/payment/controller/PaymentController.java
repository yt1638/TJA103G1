package com.showise.payment.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.showise.order.model.OrderRepository;
import com.showise.order.model.OrderVO;
import com.showise.payment.model.EcpayCheckMac;
import com.showise.payment.model.PaymentService;

@Controller
@RequestMapping("/payment")
public class PaymentController {
	
	@Autowired 
    private OrderRepository orderRepo;
	
	@Autowired 
	private PaymentService paymentSvc;
	
    private String merchantId="3002607";

    private String hashKey="pwFHCqoQZGmho4w6";

    private String hashIv="EkRm7iFT261dpevs";
 
    private String aioUrl="https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5";

    @GetMapping("/gotoecpay")
    public String goToEcpay(@RequestParam Integer orderId, Model model) {

        OrderVO order = orderRepo.findById(orderId).orElse(null);
        if(order==null) {
        	throw new RuntimeException("沒有這筆訂單");
        }

        // 僅允許「0待付款」的訂單去綠界
        if (order.getOrderStatus() != 0) {
            return "redirect:/order/complete?orderId=" + orderId;
        }

        // 綠界要的參數
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", merchantId);//特店編號
        params.put("MerchantTradeNo","ORDER"+orderId); //特店交易編號
        params.put("MerchantTradeDate",LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
        params.put("PaymentType", "aio");
        String totalPrice=String.valueOf(order.getTotalPrice().intValue());
        params.put("TotalAmount", totalPrice);//必須是整數字串
        params.put("TradeDesc", "電影院訂單");//交易描述
        params.put("ItemName", "電影票");//商品名稱
        params.put("ChoosePayment", "Credit"); //只用信用卡一次付清
        params.put("EncryptType", "1");//CheckMacValue加密類型(固定填入1，代表是用SHA256加密)
        params.put("CustomField1", String.valueOf(orderId));
        params.put("ReturnURL","http://showise.ddns.net/payment/notify");//付款完成回傳給後端的通知
        params.put("OrderResultURL","http://showise.ddns.net/payment/return");//前端的網址
//        params.put("ReturnURL","https://exogenously-unfactual-allison.ngrok-free.dev/payment/notify");//付款完成回傳給後端的通知
//        params.put("OrderResultURL", "https://exogenously-unfactual-allison.ngrok-free.dev/payment/return");//前端的網址

        //產生CheckMacValue，目的：綠界驗證是不是合法特店
        String checkMac = EcpayCheckMac.gen(params, hashKey, hashIv);
        params.put("CheckMacValue", checkMac);

        model.addAttribute("aioUrl", aioUrl);
        model.addAttribute("params", params);

        return "front-end/payment/ecpay-auto-submit";
    }
    
    @PostMapping("/notify")
    @ResponseBody
    public String paymentNotify(@RequestParam Map<String,String> params) {
    	System.out.println("ECPay notify");
    	
    	Integer orderId=Integer.valueOf(params.get("CustomField1"));
    	String rtnCode=params.get("RtnCode");
    	if("1".equals(rtnCode)) {
    		paymentSvc.orderPaid(orderId);
    		System.out.println("notify"+orderId);
    	}else {
    		paymentSvc.orderCancell(orderId);
    	}
    	return "1|OK";//告訴綠界我收到了，不然一直重送
    }
    
    @PostMapping("/return")
    public String paymentReturn(@RequestParam Map<String, String> params,RedirectAttributes ra) {
    	System.out.println("ECPay params = " + params);
    	Integer orderId=Integer.valueOf(params.get("CustomField1"));
    	OrderVO order = orderRepo.findById(orderId).orElse(null);
        if(order.getOrderStatus() == 0) {
        	ra.addFlashAttribute("resultMessage","未付款，請重新訂購");
        }
        if(order.getOrderStatus()==1) {
        	ra.addFlashAttribute("resultMessage","您已訂票成功！請留意email信件");
        }
        if(order.getOrderStatus()==2) {
        	ra.addFlashAttribute("resultMessage","付款失敗或逾時，訂單已取消，請重新訂購");
        }

        return "redirect:/order/orderresult?orderId="+orderId;
    } 
    
}

