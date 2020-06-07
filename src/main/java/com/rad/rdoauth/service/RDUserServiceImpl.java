package com.rad.rdoauth.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Handling user activities
 * 
 * @author rajinda
 * @since 04th May 2020
 * @version 1.0
 */
@Service(value = "rdUserService")
public class RDUserServiceImpl implements UserDetailsService {

	@Autowired @Qualifier("resourceJdbcTemplate")
	private JdbcTemplate resourceJdbcTemplate;
	
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		String sql = "SELECT * FROM RD_LOGIN WHERE USERNAME = (?) AND STATUS = 1";
		List<Map<String, Object>> rows = resourceJdbcTemplate.queryForList(sql, new Object[]{userId});
		
		long userDid = (Long) rows.get(0).get("USER_DID");
		String username = ((String) rows.get(0).get("USERNAME"));
		String passwd = ((String) rows.get(0).get("PASSWORD"));
		
		List<SimpleGrantedAuthority> authority = new ArrayList<SimpleGrantedAuthority>();
		if (userDid > 0) {
			String sqlRole = "SELECT RL.ROLE_NAME "
					+ "FROM RD_ROLE RL "
					+ "JOIN RD_USER_ROLE UR ON UR.ROLE_DID = RL.ROLE_DID "
					+ "AND RL.STATUS = (?) "
					+ "AND UR.USER_DID = (?) ";
			List<String> roles = resourceJdbcTemplate.queryForList(sqlRole, String.class, new Object[]{1, userDid});
			for (String role : roles) {
				authority.add(new SimpleGrantedAuthority(role));
			}
		}
		
		UserDetails loginDTO = new User(username, passwd, authority);
		return loginDTO;
	}
}
