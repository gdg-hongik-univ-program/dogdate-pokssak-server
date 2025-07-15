package com.example.dogmeeting.controller;

import com.example.dogmeeting.member.Member;
import com.example.dogmeeting.dto.MemberJoinRequest;
import com.example.dogmeeting.dto.MemberLoginRequest;
import com.example.dogmeeting.member.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class MemberController {

    private final MemberService memberService;


    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> join(@Valid @RequestBody MemberJoinRequest request) {
        memberService.joinMember(request); // 인터페이스 메서드 호출
        return new ResponseEntity<>("회원가입이 성공적으로 완료되었습니다.", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody MemberLoginRequest request) {
        Member loggedInMember = memberService.loginMember(request.getMemberId(), request.getPassword()); // 인터페이스 메서드 호출
        return new ResponseEntity<>("로그인 성공! 환영합니다, " + loggedInMember.getMemberId() + "님!", HttpStatus.OK);
    }
}