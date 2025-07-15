package com.example.dogmeeting.dto;

import com.example.dogmeeting.member.Member;
import com.example.dogmeeting.dto.MemberJoinRequest;
import com.example.dogmeeting.exception.DuplicateMemberIdException;
import com.example.dogmeeting.exception.MemberNotFoundException;
import com.example.dogmeeting.exception.PasswordMismatchException;
import com.example.dogmeeting.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 스프링 빈으로 등록
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 기본 설정
public class MemberServiceImpl implements com.example.dogmeeting.member.MemberService { // MemberService 인터페이스 구현

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 생성자 주입
    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override // 인터페이스 메서드 오버라이드
    @Transactional // 쓰기 작업에는 별도 트랜잭션 설정
    public Long joinMember(MemberJoinRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        memberRepository.findByMemberId(request.getMemberId())
                .ifPresent(m -> {
                    throw new DuplicateMemberIdException("이미 사용 중인 아이디입니다.");
                });

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Member newMember = new Member(request.getMemberId(), encodedPassword, request.getMemberName());

        memberRepository.save(newMember);
        return newMember.getMemberNo();
    }

    @Override // 인터페이스 메서드 오버라이드
    public Member loginMember(String memberId, String rawPassword) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new MemberNotFoundException("아이디를 찾을 수 없습니다."));

        if (!member.checkPassword(passwordEncoder, rawPassword)) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }
}