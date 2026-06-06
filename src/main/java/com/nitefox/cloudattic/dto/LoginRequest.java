/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.dto;

import lombok.Data;

/**
 *
 * @author NiTeFox
 */
@Data
public class LoginRequest {
    private String email;
    private String password;
}