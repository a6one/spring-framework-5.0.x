package spring.example.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyMapper {

	User getAll();
}
