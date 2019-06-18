/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plustech.reportscore.service;

import org.springframework.stereotype.Component;

/**
 *
 * @author ottor
 */
@Component
public interface AMQService {
    
    public boolean tokenValidation(String token);
    
}
