package com.ccc.proj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ccc.proj.entity.Category;

public interface CategoryService extends IService<Category> {
    /**
     * 根据id删除分类，删除之前进行判断
     * @param id
     */
    void remove(Long id);
}
