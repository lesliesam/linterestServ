package com.linterest.services;

import com.linterest.entity.MenuEntity;

import java.util.List;

/**
 * @author <a href="mailto:lesliesam@hotmail.com"> Sam Yu </a>
 */
public interface MenuServices {
    List<MenuEntity> getById(String id);
}
