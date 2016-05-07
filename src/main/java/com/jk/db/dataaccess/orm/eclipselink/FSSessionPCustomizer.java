/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jk.db.dataaccess.orm.eclipselink;


import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
public class FSSessionPCustomizer  {

	public void customize(Properties prop) throws Exception {
		Object userNameProperty = prop.getProperty("fs.username");
		Object passwordProperty = prop.getProperty("fs.password");
		System.out.println("Username : " + userNameProperty + " , Password : " + passwordProperty);

		String userName = decode(userNameProperty.toString());

		String password = decode(passwordProperty.toString());

//		prop.getLogin().setUserName(userName);
//		prop.getLogin().setPassword(password);
//
//		prop.getProperties().put("javax.persistence.jdbc.user", userName);
//		prop.getProperties().put("javax.persistence.jdbc.password", password);

	}

	public static String decode(String name) {
		return new String(Base64.decodeBase64(name));
	}

	public static String encode(String value) {
		return new String(Base64.encodeBase64(value.getBytes()));
	}

	public static void main(String[] args) {
		System.out.println(encode("root"));
		System.out.println(encode("123456"));
		System.out.println(decode("ZmluOTc1MzE="));
	}

}
