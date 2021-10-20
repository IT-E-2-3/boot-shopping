package com.mycompany.webapp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.mycompany.webapp.dto.CategoryDto;
import com.mycompany.webapp.dto.Pager;
import com.mycompany.webapp.dto.ProductListDto;

@Mapper
public interface ProductDao {
	public List<CategoryDto> getcategoryList(String cateCode);
	public CategoryDto currentCategory(String cateCode);
	public List<CategoryDto> parentCategory(String cateCode);
	public List<ProductListDto> getproductList(@Param("pager") Pager pager, @Param("cateCode") String catecode);
	public int getCountRows(String cateCode);
	public ProductListDto getproductDetail(@Param("pid")String pid, @Param("color") String color);
	public List<ProductListDto> getSizeList(@Param("pid")String pid, @Param("color") String color);
	public List<ProductListDto> getColorList(String pid);
	public ProductListDto getMatchingCloth(@Param("pid") String pid, @Param("color") String color);
	public List<ProductListDto> getStockListByPid(String pid);
}
