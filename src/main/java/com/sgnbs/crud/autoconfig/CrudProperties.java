package com.sgnbs.crud.autoconfig;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "crud")
public class CrudProperties {

    private String packageScan="com.sgnbs";

    private String insertName="insertSelective";

    private String updateName="updateByPrimaryKeySelective";

    private String delelteName="deleteByPrimaryKey";

    private String selectOneName="selectByPrimaryKey";

    private String delStatus="99";

    private String delfield="status";

    private String pageNoName  = "pageNo";

    private String pageSizeName = "pageSize";

    public String getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(String delStatus) {
        this.delStatus = delStatus;
    }

    public String getDelfield() {
        return delfield;
    }

    public void setDelfield(String delfield) {
        this.delfield = delfield;
    }

    public String getPackageScan() {
        return packageScan;
    }

    public void setPackageScan(String packageScan) {
        this.packageScan = packageScan;
    }

    public String getInsertName() {
        return insertName;
    }

    public void setInsertName(String insertName) {
        this.insertName = insertName;
    }

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public String getDelelteName() {
        return delelteName;
    }

    public void setDelelteName(String delelteName) {
        this.delelteName = delelteName;
    }

    public String getSelectOneName() {
        return selectOneName;
    }

    public void setSelectOneName(String selectOneName) {
        this.selectOneName = selectOneName;
    }

    public String getPageNoName() {
        return pageNoName;
    }

    public void setPageNoName(String pageNoName) {
        this.pageNoName = pageNoName;
    }

    public String getPageSizeName() {
        return pageSizeName;
    }

    public void setPageSizeName(String pageSizeName) {
        this.pageSizeName = pageSizeName;
    }
}
