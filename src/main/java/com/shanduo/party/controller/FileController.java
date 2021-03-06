package com.shanduo.party.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.shanduo.party.common.ErrorCodeConsts;
import com.shanduo.party.common.ConfigConsts;
import com.shanduo.party.entity.common.ErrorBean;
import com.shanduo.party.entity.common.ResultBean;
import com.shanduo.party.entity.common.SuccessBean;
import com.shanduo.party.service.BaseService;
import com.shanduo.party.service.PictureService;
import com.shanduo.party.util.UUIDGenerator;

/**
 * 上传文件控制层
 * @ClassName: FileController
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月23日 上午9:33:28
 * 
 */
@Controller
@RequestMapping("file")
public class FileController {
	
	private static final Logger log = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private BaseService baseService;
	@Autowired
	private PictureService pictureService;
	
	/**
	 * 文件上传
	 * @Title: upload
	 * @Description: TODO
	 * @param @param request
	 * @param @param file 文件集合
	 * @param @param token
	 * @param @return
	 * @param @throws IOException
	 * @return ResultBean
	 * @throws
	 */
    @RequestMapping(value="upload",method=RequestMethod.POST)
    @ResponseBody
    public ResultBean upload(HttpServletRequest request,@RequestParam("file") MultipartFile[] file,String token) throws IOException{
    	Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
		String imangs = getImage(file);
    	try {
    		imangs = pictureService.savePicture(isUserId, imangs);
		} catch (Exception e) {
			log.error("图片记录插入失败");
			return new ErrorBean(10003,"上传失败");
		}
        return new SuccessBean(imangs);
    }   

    /**
     * 上传图片返回图片名字
     * @Title: uploads
     * @Description: TODO
     * @param @param request
     * @param @param file
     * @param @param token
     * @param @return
     * @param @throws IOException
     * @return ResultBean
     * @throws
     */
    @RequestMapping(value="uploads",method=RequestMethod.POST)
    @ResponseBody
    public ResultBean uploads(HttpServletRequest request,@RequestParam("file") MultipartFile[] file,String token) throws IOException{
    	Integer isUserId = baseService.checkUserToken(token);
		if(isUserId == null) {
			log.error(ErrorCodeConsts.USER_TOKEN_PASTDUR);
			return new ErrorBean(10001,ErrorCodeConsts.USER_TOKEN_PASTDUR);
		}
    	String imangs = getImage(file);
    	try {
    		pictureService.savePicture(isUserId, imangs);
		} catch (Exception e) {
			log.error("图片记录插入失败");
			return new ErrorBean(10003,"上传失败");
		}
        return new SuccessBean(imangs);
    }
    
    private String getImage(MultipartFile[] file) throws IOException {
    	StringBuilder imangs = new StringBuilder();
    	for(int i=0;i<file.length;i++) {
    		MultipartFile files = file[i];
            String fileName = files.getOriginalFilename();
            fileName = UUIDGenerator.getUUID()+fileName.substring(fileName.lastIndexOf("."));
            File dir = new File(ConfigConsts.FILE_PATH,fileName);
            if(!dir.exists()){
                dir.mkdirs();
            }
            //MultipartFile自带的解析方法
            files.transferTo(dir);
            if(i == file.length - 1) {
            	imangs.append(fileName);
			}else {
				imangs.append(fileName+",");
			}
    	}
    	return imangs.toString();
    }
}
