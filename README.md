# Mybatis-plus的使用，真香

自己学习了Oracle和mybatis-plus，不用用怎么行呢，用了的话，不写一篇博客怎么行呢，So，let's go！

为了方便MySQL读者，我把MySQL也配置好了的

#### 一、创建一个web工程没得说吧
#### 二、导入依赖
第一次使用maven引Oracle驱动的注意了，maven厂库是没用ojdbc，所以要自己安装，还有，如果你的Oracle版本是10g，请使用ojdbc14.jar，如果你的版本是11g，请使用ojdbc6.jar，我的版本是11g，所以我用的ojdbc6.jar，然后我提供一个下载地址吧:[ojdbc6](http://v7.jimxu.top/jar/ojdbc6.jar),毕竟官网的速度不敢恭维！！！有了jar包就要安装到maven厂库了，可以将你下载的jar包ongoing解压软件打开，MATE-INF里面有个MANIFEST.MF文件，用记事本打开就可以看到它的版本号，我这个是11.1.0.7.0，然后在jar包文件路径下打开cmd，执行:

```mvn
mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc6 -Dversion=11.1.0.7.0 -Dpackaging=jar -Dfile=ojdbc6.jar
```
看到BUILD SUCCESS就成功了
其中的Dversion=11.1.0.7.0就是查看的版本。
然后可以在maven厂库看见了![](http://v7.jimxu.top/images/201911031636.png)

最近学习了Lombok，它可以减少实体类的代码，也就是getter,setter,toString的一系列操作，具体的类容嘛，自行[百度](https://www.baidu.com/s?wd=Lombok)。
IDEA需要安装Lombok插件，Eclipse的话我没折腾过，不安装插件使用getter/setter,编辑器就会报错。
最后看看依赖

```xml
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>com.oracle</groupId>
			<artifactId>ojdbc6</artifactId>
			<version>11.1.0.7.0</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
		<dependency>
			<groupId>com.baomidou</groupId>
			<artifactId>mybatis-plus-boot-starter</artifactId>
			<version>3.1.0</version>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<version>5.1.26</version>
		</dependency>
```

我把MySQL和Oracle都引进来了的。

然后配置文件

```properties
# 监听端口
server.port=8080
server.address=127.0.0.1

#Oracle
spring.datasource.url=jdbc:oracle:thin:@127.0.0.1:1521:orcl
spring.datasource.username=scott
spring.datasource.password=tiger
spring.datasource.driver-class-name=oracle.jdbc.driver.OracleDriver


# MySql
#spring.datasource.url=jdbc:mysql:///test
#spring.datasource.username=root
#spring.datasource.password=1234
#spring.datasource.driver-class-name=com.mysql.jdbc.Driver

#打印执行的SQL语句
logging.level.top.jimxu.mapper:debug
```

这里说一下遇到的坑吧，Oracle要连接必须开启两个服务，我开始只开了一个，就报了一个

```
java.sql.SQLException: The Network Adapter could not establish the connection
```

具体的两个服务如图![](http://v7.jimxu.top/images/201911041444.png)

然后打印执行SQL语句里面的 top.jimxu.mapper是我的包名，要改哦

接下来就贴代码吧

Application

```java
@SpringBootApplication
@MapperScan("top.jimxu.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
```

Entity

```java
@Data
@TableName("users")
public class User {
    @TableId("user_id")
    private int userId;
    private String userName;
    private int userAge;
}
```

就这么一点点代码就好了，你说nice不nice，哈哈哈，不过这里还是说一下，userName会被Mybatis-plus的BaseMapper拆成user_name,同理，userAge就是user_age。

所以把表创建出来

Oracle

```sql
CREATE TABLE "USERS" (
user_id NUMBER NOT NULL ,
user_name VARCHAR2(50) NOT NULL ,
user_age NUMBER NOT NULL ,
PRIMARY KEY (user_id)
);
```

MySQL

```sql
CREATE TABLE `USERS` (
`user_id`  int NOT NULL ,
`user_name`  varchar(50) NOT NULL ,
`user_age`  int NOT NULL ,
PRIMARY KEY (`user_id`)
);
```

Controller

```java
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public User selById(@PathVariable int id){
        return userService.findUserById(id);
    }
    @GetMapping()
    public List<User> selUser(){
        return userService.selectList(null);
    }
    @PostMapping()
    public void addUser(@RequestBody User user){
        userService.insertUser(user);
    }
    @PutMapping()
    public void updateUser(@RequestBody User user){
        userService.insertUser(user);
    }
    @DeleteMapping()
    public void delUser(User user){
        userService.deleteUser(user);
    }
}
```

没错，RESTful风格的接口

Service接口

```java
public interface UserService extends IService<User>{
    int insertUser( User user );
    int updateUser( User user );
    int deleteUser( User user );
    User findUserById( int id );
    List<User> selectList(User user);
}
```

继承了Mybatis-plus的IService接口，泛型是User

Service实现类

```java
@Service
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public int insertUser(User user) {
        return baseMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return baseMapper.updateById(user);
    }

    @Override
    public int deleteUser(User user) {
        return baseMapper.deleteById(user.getUserId());
    }

    @Override
    public User findUserById(int id) {
        System.out.println(id);
        System.out.println(baseMapper.selectById(id));
        return baseMapper.selectById(id);
    }

    @Override
    public List<User> selectList(User user) {
        return baseMapper.selectList(null);
    }
}
```

Mapper

```java
public interface UserMapper extends BaseMapper<User>{
}
```

没错，写完了，不需要复杂的mapper.xml，BaseMapper提供的基础的CRUD，极大的简化了操作啊啊啊啊啊，只能说，牛逼。

来吧，运行起来，打开Postman测试咯

由于没有数据，那就先测试新增

```json
请求方式选择POST URL填 localhost:8080/user
依次插入几条数据
{"userId": 1,"userName": "张三","userAge": 16}
{"userId": 2,"userName": "李四","userAge": 17}
{"userId": 3,"userName": "王五","userAge": 18}
```

刷新数据库
![](http://v7.jimxu.top/images/201911041556.png)

OK!新增没得问题再测试一下查询吧

查询全部
![](http://v7.jimxu.top/images/201911041557.png)

通过id查询
![](http://v7.jimxu.top/images/201911041559.png)

大功告成！！！总的来说，确实好用啊

我把项目放到了GitHub，需要的小伙伴就去clone吧，别忘记start哦

链接:https://github.com/52JimXu/SpringBoot-Mybatis-plus
