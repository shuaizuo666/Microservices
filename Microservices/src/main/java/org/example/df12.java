package org.example;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class df12 {


        public static void main(String[] args) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String encodedPassword = encoder.encode("123456");
            System.out.println(encodedPassword);
        }
    }
