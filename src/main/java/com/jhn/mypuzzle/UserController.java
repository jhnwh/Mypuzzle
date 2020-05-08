package com.jhn.mypuzzle;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ClassName: UserController
 * Description:
 * date: 2020/4/23 11:25
 *
 * @author jianghaonan
 * @version 1.0.0
 */
public class UserController {
    @GetMapping()
    public ResponseEntity<String> hello(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<>("Hello World!", headers, HttpStatus.OK);
    }
}
