package com.example.java.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.java.admin.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
