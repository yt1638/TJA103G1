package com.showise.member.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class AuthCodeService {
	
	@Autowired
	private JedisPool jedisPool;
	
	private static final int EXPIRE_SECONDS = 300;	// 有效時間為5分鐘
	
	
	// 產生隨機驗證碼
	private static String returnAuthCode() {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i <= 8; i++) {
			int condition = (int) (Math.random() * 3) + 1;
			switch (condition) {
			case 1:
				//隨機英文大寫
				char c1 = (char)((int)(Math.random() * 26) + 65);
				sb.append(c1);
				break;
			case 2:
				//隨機英文小寫
				char c2 = (char)((int)(Math.random() * 26) + 97);
				sb.append(c2);
				break;
			case 3:
				sb.append((int)(Math.random() * 10));
			}
		}
		return sb.toString();
	}
	

	public String generateAndSave(String memberId) {
		
		String redisKey = "verify:member" + memberId;
		String authCode = returnAuthCode();
		
		try(Jedis jedis = jedisPool.getResource()){
			jedis.set(redisKey, authCode);
			jedis.expire(redisKey, EXPIRE_SECONDS);
		}
		
		return authCode;
	}
	
	
	public boolean verify(String memberId, String inputCode) {
		
		String redisKey = "verify:member" + memberId;
		
		try(Jedis jedis = jedisPool.getResource()){
			String savedCode = jedis.get(redisKey);
			
			// 驗證碼已過期或不存在
			if(savedCode == null) {
				return false;
			}
			
			// 驗證成功就刪除
			if(savedCode.equals(inputCode)) {
				jedis.del(redisKey);
				return true;
			}
			
			return false;
		}
	} 
}
