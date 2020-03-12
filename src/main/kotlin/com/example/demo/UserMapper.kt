package com.example.demo

import org.apache.ibatis.annotations.*

@Mapper
interface UserRepository {
    @Insert("""
        insert into user (name, email) values(#{name}, #{email});
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun insert(request: AddUserRequest): Int

    @Select("""
        select * from user where id = #{id};
    """)
    fun select(id: Int): User?

    @Update("""
        update user set email = #{email} where id = #{id};
    """)
    fun update(id: Int, email: String): Int

    @Delete("""
        delete from user where id = #{id};
    """)
    fun delete(id: Int): Int
}
