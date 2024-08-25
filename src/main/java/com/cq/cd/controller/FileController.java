package com.cq.cd.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

//status = 500 后端异常
@RestController
public class FileController {
	private static final String UPLOAD_FOLDER = "D:\\mavenProject\\mavenproject820\\src\\main\\resources\\static\\upload";

	@PostMapping("/upload")
	//Multipartfile要和前端对应
	public String upload(String username, MultipartFile photo, HttpServletRequest request) throws IOException{
		System.out.println(username);
		System.out.println("文件大小: " + photo.getSize());
		System.out.println(photo.getOriginalFilename());
		System.out.println(photo.getContentType());
		String path = request.getServletContext().getRealPath("/upload/");
		System.out.println(path);
		saveFile(photo, path);
		//todo 返回访问路径
		return "上传成功";
	}

	public void saveFile(MultipartFile photo, String path) throws IOException {
		File up_Dir = new File(path);
		if(!up_Dir.exists()){
			up_Dir.mkdir();
		}
		File file = new File(path + photo.getOriginalFilename());
		photo.transferTo(file);
	}
}
