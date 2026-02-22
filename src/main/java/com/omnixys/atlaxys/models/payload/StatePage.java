package com.omnixys.atlaxys.models.payload;

import com.omnixys.atlaxys.models.entity.State;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class StatePage {

    private final List<State> content;
    private final long totalElements;
    private final int totalPages;
    private final int size;
    private final int number;

    public StatePage(Page<State> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.size = page.getSize();
        this.number = page.getNumber();
    }

}