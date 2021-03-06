package com.github.zhangkaitao.shiro.chapter5.hash;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.codec.CodecException;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.UnknownAlgorithmException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-27
 * <p>Version: 1.0
 */
public class PasswordTest extends BaseTest {
	
	/**
	 * 1、passwordService 使用 DefaultPasswordService，如果有必要也可以自定义；
	 * 2、hashService 定义散列密码使用的 HashService，默认使用 DefaultHashService（默SHA-256 算法）；
	 * 3、hashFormat 用于对散列出的值进行格式化，默认使用 Shiro1CryptFormat，
	 * 	  另外提供了Base64Format 和 HexFormat，对于有 salt 的密码请自定义实现 ParsableHashFormat 
	 *    然后把salt 格式化到散列值中；
	 * 4、hashFormatFactory 用于根据散列值得到散列的密码和 salt； 因为如果使用如 SHA 算法，
	 *    那么会生成一个 salt，此 salt 需要保存到散列后的值中以便之后与传入的密码比较时使用；
	 *    默认使用 DefaultHashFormatFactory；
	 * 5、passwordMatcher 使用 PasswordMatcher，其是一个 CredentialsMatcher 实现；
	 * 6、将 credentialsMatcher 赋值给 myRealm，myRealm 间接继承了 AuthenticatingRealm，
	 * 	  其在调用 getAuthenticationInfo方法获取到AuthenticationInfo信息后，
	 * 	  会使用credentialsMatcher来验证凭据是否匹配，如果不匹配将抛出IncorrectCredentialsException异常。
	 */
    @Test
    public void testPasswordServiceWithMyRealm() {
        login("classpath:shiro-passwordservice.ini", "wu", "123");
    }

    @Test
    public void testPasswordServiceWithJdbcRealm() {
        login("classpath:shiro-jdbc-passwordservice.ini", "wu", "123");
    }

    @Test
    public void testGeneratePassword() {
        String algorithmName = "md5";
        String username = "liu";
        String password = "123";
        String salt1 = username;
        String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
        int hashIterations = 2;
       
        /**
        * @param algorithmName(加密算法)  the {@link java.security.MessageDigest MessageDigest} algorithm name to use when
        *                       performing the hash.
        * @param source（加密对象）         the source object to be hashed.
        * @param salt（盐值）           the salt to use for the hash
        * @param hashIterations	（迭代次数） 		the number of times the {@code source} argument hashed for attack resiliency.
        * @throws CodecException            if either Object constructor argument cannot be converted into a byte array.
        * @throws UnknownAlgorithmException if the {@code algorithmName} is not available.
        */
        SimpleHash hash = new SimpleHash(algorithmName, password, salt1 + salt2, hashIterations);
        String encodedPassword = hash.toHex();
        System.out.println(salt2);//随机数盐
        System.out.println(encodedPassword);//加密后的密码
    }

    @Test
    public void testHashedCredentialsMatcherWithMyRealm2() {
        //使用testGeneratePassword生成的散列密码
        login("classpath:shiro-hashedCredentialsMatcher.ini", "liu", "123");
    }

    @Test
    public void testHashedCredentialsMatcherWithJdbcRealm() {

        BeanUtilsBean.getInstance().getConvertUtils().register(new EnumConverter(), JdbcRealm.SaltStyle.class);

        //使用testGeneratePassword生成的散列密码
        login("classpath:shiro-jdbc-hashedCredentialsMatcher.ini", "liu", "123");
    }


    private class EnumConverter extends AbstractConverter {
        @Override
        protected String convertToString(final Object value) throws Throwable {
            return ((Enum) value).name();
        }
        @Override
        protected Object convertToType(final Class type, final Object value) throws Throwable {
            return Enum.valueOf(type, value.toString());
        }

        @Override
        protected Class getDefaultType() {
            return null;
        }

    }

    @Test(expected = ExcessiveAttemptsException.class)
    public void testRetryLimitHashedCredentialsMatcherWithMyRealm() {
        for(int i = 1; i <= 5; i++) {
            try {
                login("classpath:shiro-retryLimitHashedCredentialsMatcher.ini", "liu", "234");
            } catch (Exception e) {/*ignore*/}
        }
        login("classpath:shiro-retryLimitHashedCredentialsMatcher.ini", "liu", "234");
    }
}
