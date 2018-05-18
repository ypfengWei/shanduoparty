
package com.shanduo.party.util;

/**
 * 分页
 * @ClassName: Page
 * @Description: TODO
 * @author fanshixin
 * @date 2018年4月26日 下午4:04:56
 *
 */
public class Page {
	
	private int totalPage;//总页数
	
	private int totalRecord;//总记录数
	 
	private int pageSize ;//每页记录数
	
	private int pageNum;//当前页码
	
	//用来计算总页数
	public Page(int totalRecord,int pageSize,int pageNum){
	    this.pageSize = pageSize < 1?10:pageSize;
	    this.totalRecord = totalRecord;
	    this.totalPage = this.totalRecord % this.pageSize==0?this.totalRecord/this.pageSize:this.totalRecord/this.pageSize+1;
	    this.pageNum = pageNum > this.totalPage?totalPage:pageNum;
	    this.pageNum = this.pageNum < 1?1:this.pageNum;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	@Override
	public String toString() {
		return "Page [totalPage=" + totalPage + ", totalRecord=" + totalRecord + ", pageSize=" + pageSize + ", pageNum="
				+ pageNum + "]";
	}

}
