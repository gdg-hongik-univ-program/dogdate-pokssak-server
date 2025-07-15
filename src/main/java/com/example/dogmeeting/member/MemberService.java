package com.example.dogmeeting.member;

import com.example.dogmeeting.member.Member;
import com.example.dogmeeting.dto.MemberJoinRequest;

public interface MemberService {
    Long joinMember(MemberJoinRequest request);
    Member loginMember(String memberId, String rawPassword);
}