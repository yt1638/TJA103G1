package com.showise.order.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.showise.seat.model.SeatVO;

@Service
public class SeatLockService {

    private final StringRedisTemplate redis;
    private static final Duration TTL = Duration.ofMinutes(15);

    public SeatLockService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    //Key：
    public String key(int sessionId, int seatId) {
        return "seatlock:" + sessionId + ":" + seatId; //seatlock:{sessionId}:{seatId}
    }

    //看這顆座位現在有沒有被鎖，有就回傳value，沒有就回傳null
    public String getLockValue(int sessionId, int seatId) {
        return redis.opsForValue().get(key(sessionId, seatId));
    }


    //查「被別人鎖住」的seatIds有哪些(排除自己鎖住的)
    public List<Integer> getLockedSeatIds(int sessionId, Integer memberId, List<SeatVO> seats,String myLockToken) {
        List<Integer> lockedSeatIds = new ArrayList<>();

        for (SeatVO seat : seats) {
        	int seatId =seat.getSeatId();
            String v = getLockValue(sessionId, seatId);
            
            if (v == null) {
            	continue; //沒鎖
            } 
            
            String[] parts = v.split(":");
            Integer ownerMemberId=Integer.valueOf(parts[0]);
            String ownerLockToken =parts[1];
            
            //自己鎖的要排除
            if(memberId.equals(ownerMemberId) && myLockToken.equals(ownerLockToken)) {
            	continue;
            }
            lockedSeatIds.add(seatId);
        }
        return lockedSeatIds;
    }
    
    //鎖住一批座位，要嘛全成功要嘛全失敗
    public Map<String, Object> lockSeats(int sessionId, List<Integer> seatIds, String lockToken ,Integer memberId) {
        
    	//回給controller的結果 ex:success / message
    	Map<String, Object> m = new HashMap<>();

        // 記錄成功鎖到哪些seatId，失敗時rollback用
        List<Integer> lockedSeatIds = new ArrayList<>();
        
        String value=memberId+":"+lockToken;

        for (Integer seatId : seatIds) {
            if (seatId == null) {
                rollback(sessionId, lockedSeatIds, value);
                m.put("success", false);
                m.put("message", "seatId不可為null");
                return m;
            }

            String k = key(sessionId, seatId); //seatlock:{sessionId}:{seatId}

            //第一次鎖
            //.opsForValue() :用Redis的字串型別
            Boolean ok = redis.opsForValue().setIfAbsent(k, value, TTL);
            if (Boolean.TRUE.equals(ok)) {
                lockedSeatIds.add(seatId);//鎖成功就記錄下來（rollback用）→ 繼續鎖下一顆
                continue;
            }

            //鎖失敗，已存在（讀existing看是不是自己鎖住的）
            String existing = redis.opsForValue().get(k);
            if(existing != null) {
                String[] parts = existing.split(":");
                Integer ownerMemberId= Integer.valueOf(parts[0]);
                String ownerLockToken = parts[1];
                
                //如果是自己鎖的 → 為OK，並restTTL
                if (memberId.equals(ownerMemberId)  && lockToken.equals(ownerLockToken)) {
                    redis.expire(k, TTL); 
                    continue;
                }

                // 不是自己的→失敗，rollback，回覆衝突的seatId
                rollback(sessionId, lockedSeatIds, value);
                m.put("success", false);
                m.put("message", "座位已被其他人鎖定，請重新選位");
                return m;
            }
        }
        m.put("success", true);
        return m;
    }

    //只允許釋放自己的鎖：使用者下單成功、按返回取消/逾時
    public void releaseSeats(int sessionId, List<Integer> seatIds, String lockToken ,Integer memberId) {
        if (seatIds == null || seatIds.isEmpty()) return;
        if(memberId ==null)return;
        if (lockToken == null ||  lockToken.isBlank()) return;
        
        String value= memberId+":"+lockToken;

        for (Integer seatId : seatIds) {
            if (seatId == null) continue;

            String key = key(sessionId, seatId);
            String redisValue = redis.opsForValue().get(key);
            
            //同member + 同token才可以刪除
            if (value.equals(redisValue)) {
                redis.delete(key);
            }
        }
    }


    //lockSeats中途失敗時的rollback：只刪「自己這次鎖的」避免誤刪別人的
    private void rollback(int sessionId, List<Integer> lockedSeatIds, String value) {
        for (Integer seatId : lockedSeatIds) {
            String k = key(sessionId, seatId);
            String v = redis.opsForValue().get(k);
            if (value.equals(v)) {
                redis.delete(k);
            }
        }
    }
}
