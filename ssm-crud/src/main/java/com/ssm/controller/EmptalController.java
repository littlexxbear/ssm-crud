package com.ssm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssm.bean.Emptal;
import com.ssm.bean.Msg;
import com.ssm.service.EmptalService;


/**
 * 对员工的CRUD请求
 * @author bear
 */

@Controller
public class EmptalController {

	@Autowired
	EmptalService emptalService;
	
	/**
	 * 保存
	 * @return
	 */
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmp(@Valid Emptal emptal,BindingResult result) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(result.hasErrors()) {
			//校验失败返回模态框的错误信息
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("错误的字段名："+ fieldError.getField());
				System.out.println("错误的信息："+ fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("error", map);
		}else {
			emptalService.saveEmp(emptal);
			return Msg.success();
		}
	}
	
	
	/**
	 * 导入jackson包
	 * @param pn
	 * @return
	 */
	@RequestMapping("/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue="1") Integer pn) {
		
		//引入pageHelper分页插件
		//在查询之前只需要调用，传入页码，以及每页显示数量
		PageHelper.startPage(pn,5);
		//startPage后面紧跟着的一个查询，就是一个分页查询
		List<Emptal> emps = emptalService.getAll();
		//用PageInfo对结果进行包装,连续传入5页
		PageInfo page = new PageInfo(emps,5);
		
		return Msg.success().add("pageInfo",page);
		
	}
	
	
	/**
	 * 查询员工数据（分页查询）
	 * @return
	 */
	//@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn",defaultValue="1") Integer pn,
			Model model) {
		
		//引入pageHelper分页插件
		//在查询之前只需要调用，传入页码，以及每页显示数量
		PageHelper.startPage(pn,5);
		//startPage后面紧跟着的一个查询，就是一个分页查询
		List<Emptal> emps = emptalService.getAll();
		//用PageInfo对结果进行包装,连续传入5页
		PageInfo page = new PageInfo(emps,5);
		model.addAttribute("pageInfo",page);
		
		return "list";
	}
	
	@ResponseBody
	@RequestMapping("/checkUser")
	public Msg checkUser(String empName) {
		//先判断用户名是否符合规格
		String regx = "(^[a-zA-Z0-9_-]{3,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		if(!empName.matches(regx)) {
			return Msg.fail().add("va_msg", "姓名格式为2-5位字符或3-16位数字和字母组合");
		}
		boolean result = emptalService.checkUser(empName);
		if(result) {
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg", "用户名已存在");
		}
	}
	 
	//查询信息
	@ResponseBody
	@RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
	public Msg getEmp(@PathVariable("id") Integer id) {
	
		Emptal emptal = emptalService.getEmp(id);
		return Msg.success().add("emp", emptal);
	}
	
	//修改-保存
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(@Valid Emptal emptal) {
		
		emptalService.updateEmp(emptal);
		return Msg.success();
	}
	
	//删除数据
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg delEmpById(@PathVariable("ids") String ids) {
		if(ids.contains("-")) {
			
			List<Integer> del_ids = new ArrayList<Integer>();
			
			String[] str_ids = ids.split("-");
			for (String string : str_ids) {
				del_ids.add(Integer.parseInt(string));
			}
			emptalService.delAll(del_ids);
		}else{
			Integer id = Integer.parseInt(ids);
			emptalService.delEmpById(id);
		}
		return Msg.success();
	}
	
	
}
