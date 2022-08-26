package cn.hutool.json.jwt;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.json.jwt.signers.JWTSignerUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class JWTValidatorTest {

	@Test(expected = ValidateException.class)
	public void expiredAtTest(){
		final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE0Nzc1OTJ9.isvT0Pqx0yjnZk53mUFSeYFJLDs-Ls9IsNAm86gIdZo";
		JWTValidator.of(token).validateDate(DateUtil.date());
	}

	@Test(expected = ValidateException.class)
	public void issueAtTest(){
		final String token = JWT.of()
				.setIssuedAt(DateUtil.date())
				.setKey("123456".getBytes())
				.sign();

		// 签发时间早于被检查的时间
		JWTValidator.of(token).validateDate(DateUtil.yesterday());
	}

	@Test
	public void issueAtPassTest(){
		final String token = JWT.of()
				.setIssuedAt(DateUtil.date())
				.setKey("123456".getBytes())
				.sign();

		// 签发时间早于被检查的时间
		JWTValidator.of(token).validateDate(DateUtil.date());
	}

	@Test(expected = ValidateException.class)
	public void notBeforeTest(){
		final JWT jwt = JWT.of()
				.setNotBefore(DateUtil.date());

		JWTValidator.of(jwt).validateDate(DateUtil.yesterday());
	}

	@Test
	public void notBeforePassTest(){
		final JWT jwt = JWT.of()
				.setNotBefore(DateUtil.date());
		JWTValidator.of(jwt).validateDate(DateUtil.date());
	}

	@Test
	public void validateAlgorithmTest(){
		final String token = JWT.of()
				.setNotBefore(DateUtil.date())
				.setKey("123456".getBytes())
				.sign();

		// 验证算法
		JWTValidator.of(token).validateAlgorithm(JWTSignerUtil.hs256("123456".getBytes()));
	}

	@Test
	public void validateTest(){
		final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJNb0xpIiwiZXhwIjoxNjI0OTU4MDk0NTI4LCJpYXQiOjE2MjQ5NTgwMzQ1MjAsInVzZXIiOiJ1c2VyIn0.L0uB38p9sZrivbmP0VlDe--j_11YUXTu3TfHhfQhRKc";
		final byte[] key = "1234567890".getBytes();
		final boolean validate = JWT.of(token).setKey(key).validate(0);
		Assert.assertFalse(validate);
	}

	@Test(expected = ValidateException.class)
	public void validateDateTest(){
		final JWT jwt = JWT.of()
				.setPayload("id", 123)
				.setPayload("username", "hutool")
				.setExpiresAt(DateUtil.parse("2021-10-13 09:59:00"));

		JWTValidator.of(jwt).validateDate(DateUtil.date());
	}

	@Test
	public void issue2329Test(){
		final long now = System.currentTimeMillis();
		final Date nowTime = new Date(now);
		final long expired = 3 * 1000L;
		final Date expiredTime = new Date(now + expired);

		// 使用这种方式生成token
		final String token = JWT.of().setPayload("sub", "blue-light").setIssuedAt(nowTime).setNotBefore(expiredTime)
				.setExpiresAt(expiredTime).setKey("123456".getBytes()).sign();

		// 使用这种方式验证token
		JWTValidator.of(JWT.of(token)).validateDate(DateUtil.date(now - 4000), 10);
		JWTValidator.of(JWT.of(token)).validateDate(DateUtil.date(now + 4000), 10);
	}
}