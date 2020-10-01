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

### Transactional Annotations
`@Transactional` annotation enable methods to execute within a transactional context.<br>
The only thing I need to do is to annotate a method with `@Transactional`.

```
@Transactional
fun selectAndUpdate(id: Int, name: String, email: String): Int {
    val user = userRepository.selectForUpdate(id) ?: return 0
    return userRepository.updateWithCount(id, name, email, user.updateCount+1)
}
```

This is the log with and without `@Transactional`.<br>
**with**
```
2020-10-01T11:04:38.142203Z	   53 Query	SET autocommit=0
2020-10-01T11:04:38.145767Z	   53 Query	select * from user where id = 1 for update
2020-10-01T11:04:38.149906Z	   53 Query	SELECT @@session.transaction_read_only
2020-10-01T11:04:38.152917Z	   53 Query	update user set name = 'nino', email = 'nino@hanayome.com', update_count = 9 where id = 1
2020-10-01T11:04:38.155993Z	   53 Query	commit
2020-10-01T11:04:38.162315Z	   53 Query	SET autocommit=1
```
**without**
```
2020-10-01T11:05:12.186425Z	   53 Query	SELECT @@session.transaction_read_only
2020-10-01T11:05:12.190027Z	   53 Query	update user set name = 'miku', email = 'miku@hanayome.com', update_count = update_count + 1 where id = 1
```

`SET autocommit=0` `commit` `SET autocommit=1` were executed automatically in the case with `@Transactional` annotation.

※ The way to see query log is here.
```
mysql> set global general_log = on;
Query OK, 0 rows affected (0.03 sec)

mysql> show variables like 'general_log%';
+------------------+---------------------------------+
| Variable_name    | Value                           |
+------------------+---------------------------------+
| general_log      | ON                              |
| general_log_file | /var/lib/mysql/ad558dd91ba6.log |
+------------------+---------------------------------+
2 rows in set (0.01 sec)
mysql> exit
Bye

$ docker-compose exec db bash
root@ad558dd91ba6:/# tail -f /var/lib/mysql/ad558dd91ba6.log
```

##### NOTE
`@Transactional` works by creating a proxy of the class and intercepting the annotated method(uses AOP at its foundation).<br>
[Understanding the Spring Framework’s Declarative Transaction Implementation](https://docs.spring.io/spring-framework/docs/current/spring-framework-reference/data-access.html#tx-decl-explained)

It is necessary to have a method annotated with `@Transactional` in a class different from the class that calls it due to above reason.<br>
It doesn't work within a transactional context if I call the method from the same class.
I want to lock the row with `for update` in above example but if implement `selectAndUpdate` method in the class that calls it, can't lock the row due to folling mysql's specification.
> Locking reads are only possible when autocommit is disabled (either by beginning transaction with START TRANSACTION or by setting autocommit to 0.

https://dev.mysql.com/doc/refman/5.6/en/innodb-locking-reads.html
