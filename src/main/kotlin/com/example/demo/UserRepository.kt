package com.example.demo

import org.apache.ibatis.annotations.*

@Mapper
interface UserRepository {
    @Insert("""
        insert into user (name, email, update_count) values(#{name}, #{email}, 1);
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun insert(request: AddUserRequest): Int

    @Select("""
        select * from user where id = #{id};
    """)
    fun select(id: Int): User?

    @Select("""
        select * from user where id = #{id} for update;
    """)
    fun selectForUpdate(id: Int): User?

    @Update("""
        update user set name = #{name}, email = #{email}, update_count = #{updateCount} where id = #{id};
    """)
    fun updateWithCount(id: Int, name: String, email: String, updateCount: Int): Int

    @Update("""
        update user set name = #{name}, email = #{email}, update_count = update_count + 1 where id = #{id};
    """)
    fun update(id: Int, name: String, email: String): Int

    @Delete("""
        delete from user where id = #{id};
    """)
    fun delete(id: Int): Int
}
