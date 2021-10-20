package com.mycompany.webapp.service;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.mycompany.webapp.dao.ProductDao;
import com.mycompany.webapp.dto.CategoryDto;
import com.mycompany.webapp.dto.Pager;
import com.mycompany.webapp.dto.ProductListDto;


@Service
public class ProductService {
	
	@Resource
	ProductDao productdao;
	
	public List<CategoryDto> getcategoryList(String cateCode) {
		return productdao.getcategoryList(cateCode);
	}
	public CategoryDto currentCategory(String cateCode){
		return productdao.currentCategory(cateCode);
	}
	public List<CategoryDto> parentCategory(String cateCode) {
	//public CategoryDto parentCategory(String cateCode) {
		return productdao.parentCategory(cateCode);
	}
	public List<ProductListDto> getproductList(Pager pager, String cateCode){
		return productdao.getproductList(pager, cateCode);
	}
	public int getCountRows(String cateCode) {
		return productdao.getCountRows(cateCode);
	}
	public ProductListDto getproductDetail(String pid, String color) {
		return productdao.getproductDetail(pid, color);
	}
	public List<ProductListDto> getSizeList(String pid, String color) {
		return productdao.getSizeList(pid, color);
	}
	public List<ProductListDto> getColorList(String pid) {
		return productdao.getColorList(pid);
	}
	public ProductListDto getMatchingCloth(String pid, String color) {
		return productdao.getMatchingCloth(pid, color);
	}
	public List<ProductListDto> getStockListByPid(String pid){
		return productdao.getStockListByPid(pid);
	}

}
