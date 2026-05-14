package com.example.cinema.po;

/**
 * @author fjj
 * @date 2019/4/28 5:09 PM
 */
public class Hall {
    private Integer id;
    private String name;
    private Integer row;
    private Integer column;

    // ✅ 新增：影厅的营业状态字段 (1: 正常营业, 0: 设备维护)
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    // ✅ 新增：Status 的 Getter 和 Setter 方法
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}