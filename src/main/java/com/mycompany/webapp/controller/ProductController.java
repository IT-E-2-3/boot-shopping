package com.mycompany.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mycompany.webapp.dto.CategoryDto;
import com.mycompany.webapp.dto.Pager;
import com.mycompany.webapp.dto.ProductListDto;
import com.mycompany.webapp.service.ProductService;

import lombok.extern.java.Log;

@Controller
@RequestMapping("/product")
@Log
public class ProductController {
	
	@Resource
	ProductService productService;
	
	@RequestMapping("/")
	public String home(){
		log.info("실행");
		return "product/productList";
	}
	
	@GetMapping("/list")
	public String getList(@RequestParam(defaultValue="1") int pageNo, Model model, @RequestParam("c")String cateCode){
		log.info("실행");
		List<CategoryDto> subcategory = productService.getcategoryList(cateCode);
		CategoryDto current = productService.currentCategory(cateCode);
		List<CategoryDto> parent = productService.parentCategory(cateCode);
		log.info(cateCode);
		
		model.addAttribute("subcategory", subcategory);
		model.addAttribute("current", current);
		model.addAttribute("parent", parent);
		
		int totalRows = productService.getCountRows(cateCode);
		Pager pager = new Pager(9, 5, totalRows, pageNo);
		model.addAttribute("pager", pager);
		
		List<ProductListDto> plist = productService.getproductList(pager, cateCode);
		List<List<ProductListDto>> colorlist = new ArrayList<>();
		List<List<ProductListDto>> stocklist = new ArrayList<>();
		
		for(ProductListDto p: plist) {
			String pid = p.getPid();
			List<ProductListDto> colorlist_temp = productService.getColorList(pid);
			List<ProductListDto> stocklist_temp = productService.getStockListByPid(pid);
			colorlist.add(colorlist_temp);
			stocklist.add(stocklist_temp);
		}

		//하나의 pid당 하나의 최종 재고(모든 컬러와 모든 사이즈의 재고를 합한 )만 가질 수 있도록 stocklist를 재가공
		List<ProductListDto> new_stocklist = new ArrayList<>();
		
		for(List<ProductListDto> stocks : stocklist) {
			int cnt = 0;
			int stockBypid = 0;
			for(ProductListDto stock: stocks) {
				if(stocks.get(cnt).getPid().equals(stock.getPid())) {
					stockBypid += stock.getRemaining_stock();
				}
				cnt++;
			}
//			System.out.println("###########"+ stocks.get(cnt-1).getPid()+"-" +stockBypid);
			ProductListDto new_stocks = new ProductListDto();
			new_stocks.setPid(stocks.get(cnt-1).getPid());
			new_stocks.setRemaining_stock(stockBypid);
			new_stocklist.add(new_stocks);
		}
//		
//		for(ProductListDto p : new_stocklist) {
//			System.out.println(p);
//		}
		
		
		model.addAttribute("plist", plist);
		model.addAttribute("colorlist", colorlist);
		model.addAttribute("stocklist", new_stocklist);
		
		return "product/productList";
	}
	
	@GetMapping("/detail")
	public String getDetailView(@RequestParam("pid") String pid, @RequestParam("co") String color, Model model) {
		log.info("실행");
		ProductListDto product = productService.getproductDetail(pid, color);
		List<ProductListDto> sizelist = productService.getSizeList(pid, color);
		List<ProductListDto> colorlist = productService.getColorList(pid);
		model.addAttribute("product", product);
		model.addAttribute("sizelist", sizelist);
		model.addAttribute("colorlist", colorlist);
		
		ProductListDto matching_cloth = productService.getMatchingCloth(pid, color);
		model.addAttribute("matching_cloth", matching_cloth);
//		System.out.println("#########매칭 컬러코드 : " + matching_cloth.getColor_code());
		System.out.println("######### 재고 " + sizelist);
		return "product/productDetail";
	}
}
