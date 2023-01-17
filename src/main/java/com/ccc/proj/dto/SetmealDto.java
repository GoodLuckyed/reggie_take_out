package com.ccc.proj.dto;

import com.ccc.proj.entity.Setmeal;
import com.ccc.proj.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
