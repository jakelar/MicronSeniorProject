/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

//import api.VoiceMetaData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.Map;

import app.User;
import app.DatabaseObject;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
//import src.main.java.app.AudioManager;

@Controller
@SpringBootApplication
@SessionAttributes("Login")
public class LoginController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        if(session.getAttribute("Login") != null)
            return "redirect:/";
        else {
            model.addAttribute("user", new User());
            return "login";
        }
    }

    // login endpoint and committing change
    @PostMapping("/login")
    String loginSubmit(@ModelAttribute User user, HttpSession session) {
        // Use 'user' variable (which should contain a username and password) to verify a user in the database.
        Connection connection = null;
        try {
            connection = DatabaseObject.getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users"
                    +" (user_id SERIAL NOT NULL PRIMARY KEY,"
                    +" username varchar(225) NOT NULL UNIQUE,"
                    +" password varchar(225),"
                    +" user_type varchar(255))");
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ? LIMIT 1;");
            ps.setString(1, user.getUsername());
            // Hash the password using MD5
            String password = user.getPassword();
            byte[] bytesOfMessage = password.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5Hash = md.digest(bytesOfMessage);
            BigInteger bigInt = new BigInteger(1, md5Hash);
            String hashtext = bigInt.toString(16);
            ps.setString(2, hashtext);
            ResultSet rs = ps.executeQuery();

            if(!rs.isBeforeFirst()) {
                connection.close();
                return "error";
            }
            else {
                session.setAttribute("Login", user.getUsername());
//                ps = connection.prepareStatement("SELECT * FROM users WHERE username = ? LIMIT 1;");
//                ps.setString(1, user.getUsername());
                rs.next();
                User userForSession = new User();
                userForSession.setId(rs.getLong("user_id"));
                userForSession.setUsername(rs.getString("username"));
                userForSession.setPassword(rs.getString("password"));
                userForSession.setUsertype(rs.getString("user_type"));
                userForSession.setEmail(rs.getString("email"));
                userForSession.setFirstname(rs.getString("firstname"));
                userForSession.setLastname(rs.getString("lastname"));
                userForSession.setGender(rs.getString("gender"));
                userForSession.setValid(rs.getString("valid"));
                userForSession.setAge(rs.getInt("age"));
                session.setAttribute("User", userForSession);
                connection.close();
                return "redirect:/record";
            }
        } catch (Exception e) {
            return "error";
        }finally{
            try{
                if(connection != null){
                    connection.close();
                }
            }catch(Exception e){
            }
        }
    }
}
