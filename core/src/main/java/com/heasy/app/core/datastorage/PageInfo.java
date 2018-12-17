package com.heasy.app.core.datastorage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/2/11.
 */
public class PageInfo<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    //当前页
    private int pageNum;
    //每页的数量
    private int pageSize;
    //当前页的数量
    private int size;

    //总记录数
    private long total;
    //总页数
    private int pages;
    //结果集
    private List<T> list;

    public PageInfo(){

    }

    public PageInfo(int pageNum, int pageSize, long total, List<T> list) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.total = total;
        this.list = list;
        update();
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        update();
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
        update();
    }

    public int getPages() {
        return this.pages;
    }

    private void update(){
        //total
        if(total > 0 && pageSize > 0){
            if(total % pageSize == 0){
                this.pages = (int)(total / pageSize);
            }else{
                this.pages = (int)(total / pageSize) + 1;
            }
        }else{
            this.pages = 0;
        }

        //size
        if(list != null){
            this.size = list.size();
        }
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
        update();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageInfo{");
        sb.append("pageNum=").append(pageNum);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", size=").append(size);
        sb.append(", total=").append(total);
        sb.append(", pages=").append(pages);
        sb.append(", list=").append(list);
        return sb.toString();
    }

}
