package com.mole.health.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class CommonPage<T> {
    private Integer pageNum;// 当前页码
    private Integer pageSize;// 每页大小
    private Integer totalPage;// 总页数
    private Long total;// 总条数
    private List<T> list;// 数据列表

    /**
     * 将SpringData分页后的list转为分页信息
     */
    public static <T> CommonPage<T> restPage(Page<T> pageInfo) {
        CommonPage<T> result = new CommonPage<>();
        result.setTotalPage(pageInfo.getTotalPages());
        result.setPageNum(pageInfo.getNumber());
        result.setPageSize(pageInfo.getSize());
        result.setTotal(pageInfo.getTotalElements());
        result.setList(pageInfo.getContent());
        return result;
    }

    /**
     * 将MyBatis-Plus分页后的list转为分页信息
     */
    public static <T> CommonPage<T> restPage(IPage<T> page) {
        CommonPage<T> result = new CommonPage<>();
        result.setTotalPage((int) page.getPages());
        result.setPageNum((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal(page.getTotal());
        result.setList(page.getRecords());
        return result;
    }

    /**
     * 将MyBatis-Plus分页后的list转为分页信息，并应用转换函数
     */
    public static <T, R> CommonPage<R> restPage(IPage<T> page, Function<T, R> converter) {
        CommonPage<R> result = new CommonPage<>();
        result.setTotalPage((int) page.getPages());
        result.setPageNum((int) page.getCurrent());
        result.setPageSize((int) page.getSize());
        result.setTotal(page.getTotal());
        result.setList(page.getRecords().stream().map(converter).collect(Collectors.toList()));
        return result;
    }
}
