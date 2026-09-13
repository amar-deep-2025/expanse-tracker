package com.amar.fullstack.expanse_tracker_backend.dtos;


public class ExpanseCategoryResponseDto {

    private Long id;
    private String name;


    public ExpanseCategoryResponseDto(){}
    public ExpanseCategoryResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setName(String name) {
        this.name = name;
    }
}
