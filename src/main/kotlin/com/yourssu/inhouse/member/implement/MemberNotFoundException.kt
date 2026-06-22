package com.yourssu.inhouse.member.implement

class MemberNotFoundException(id: Long) : RuntimeException("Member not found: $id")
