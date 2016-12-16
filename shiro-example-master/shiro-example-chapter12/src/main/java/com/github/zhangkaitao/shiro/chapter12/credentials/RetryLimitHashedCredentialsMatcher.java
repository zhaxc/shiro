package com.github.zhangkaitao.shiro.chapter12.credentials;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * 密码重试次数限制哈希凭证匹配器
 * @author zhaxc
 * @date 2016年12月9日 下午4:25:01
 * @version 3.0
 */
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

	//密码重试缓存 key:username(String) value:重试次数（AtomicInteger）
    private Cache<String, AtomicInteger> passwordRetryCache;

    /**
     * constructor
     * @param cacheManager 
     */
    public RetryLimitHashedCredentialsMatcher(CacheManager cacheManager) {
        passwordRetryCache = cacheManager.getCache("passwordRetryCache");
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        String username = (String)token.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if(retryCount == null) {//首次密码校验 设置校验次数为0
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        if(retryCount.incrementAndGet() > 5) {//重试次数加1大于5（已校验次数）
            //if retry count > 5 throw
            throw new ExcessiveAttemptsException();
        }

        boolean matches = super.doCredentialsMatch(token, info);//Credentials认证校验
        if(matches) {//校验通过
            //clear retry count
            passwordRetryCache.remove(username);
        }
        return matches;
    }
    
    public static void main(String[] args) {
		AtomicInteger count = new AtomicInteger(0);
		System.out.println(count);
		count.incrementAndGet();
		System.out.println(count);
		count.incrementAndGet();
		System.out.println(count);
	}
}
