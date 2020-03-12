# MyBatisMapper_demo

#### Mapper Annotations
In MyBatis, We can define Mapper by annotation instead of Mapper XML.

[MyBatis](https://mybatis.org/mybatis-3/ja/java-api.html)


Each of these annotations represents the actual SQL that is to be executed.
* `@Insert`
* `@Update`
* `@Delete`
* `@Select`

```
@Select("""
  select * from user where id = #{id};
""")
fun select(id: Int): User?
```

We can retrieve keys generated internally by the database with `@Options` annotation.
The key value will be set into a property specified with a `keyProperty` option.
```
@Insert("""
    insert into user (name, email) values(#{name}, #{email});
""")
@Options(useGeneratedKeys = true, keyProperty = "id")
fun insert(request: AddUserRequest): Int
```
