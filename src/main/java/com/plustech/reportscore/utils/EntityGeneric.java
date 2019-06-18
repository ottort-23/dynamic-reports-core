/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.plustech.reportscore.utils;


import java.util.List;

/**
 *
 * @author ottor
 */
public class EntityGeneric<T> {

    public List<T> Data;

    public List<T> getData() {
        return Data;
    }

    public void setData(List<T> Data) {
        this.Data = Data;
    }

}
