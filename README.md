# Zedio

> @author	Zenos
>
> 行百里者半九十

[TOC]

## 搭建项目目录骨架

1. 在项目目录下分别新建对应的板块：

   - api

     向前端暴露接口，供前端调用。

   - common

     通用工具类，所有的通用工具、异常处理、枚举。

   - mapper

     数据层，与数据库直接相关的数据

   - model

     模型工具，各种应用场景下的实体集合

   - service

     服务层，业务处理逻辑

2. 配置MySQL数据库，使用数据库逆向生成工具，配置数据层数据

3. 配置Lombook工具和knife4j接口文档

## 用户端开发

### 获取验证码与用户注册

1. 集成腾讯云短信

   - **common**
     - 在目录下的pom中引入tencentcloud-sdk-java依赖
     - 对应腾讯云网站注册信息，编写密钥信息
     - 编写TencentCloudProperties资源映射文件
     - 编写SMSUtils短信工具类，实现短信发送等功能
   - **api**
     - 编写对应的controller，测试相关功能

2. 整合redis数据库

   - 在Linux环境下安装redis，并使用数据库连接软件配置redis连接
   - **common**
     - 引入redis依赖
     - 编写redis数据库工具类
   - **api**
     - 在yml中配置redis
     - 编写controller，测试对应功能
   - 编写拦截器，限制60s短信发送
     - **api**
       - 创建对应的拦截器处理器
       - 创建拦截器注册类，并配置对应的拦截器

3. 在common中封装自定义异常和异常处理器

4. 用户注册

   - **api**

     - 编写对应的controller类

       判断手机号是否符合规范，获取用户ip并对用户进行访问限制，60s之内使用获取一次验证码，发送验证码，并将验证码存储在redis数据库中。

     - 创建对应的登录BO实体（使用Hibernate验证框架）

   - **service**

     - 创建对应的userService和userServiceImp，并实现对应的登录手机号的验证功能
     - 添加新的id生成工具，n3r.idworker，相关工具类和对用的启动类额外扫描
     - 在userServiceImp中创建对应的用户创建功能

### 用户登录/登出

1. 用户登录

   - **api**

     - 开发对应的用户信息登录controller类

       查询数据库，用户是否存在，不存在则创建新的数据记录，登陆成功后，使用uuid生成用户id存储在redis数据库中，用户注册成功后记得删除对应生成的验证码；编写对用的VO实体类，将用户的信息和token都传给前端。

2. 用户登出

   - **api**

     - 开发对应的用户信息登出controller类

       也就是删除redis中存储的token

### 用户页面信息修改

1. 用户信息查询
   - **model**
     - 在userVO中添加，我的关注、我的粉丝、以及我的获赞总数的属性值
   - **service**
     - 创建对应的getUser方法，用户的userId查询相关用户信息
   - **api**
     - 创建对应的controller方法，查询对应的用户数据信息，转换成对应的VO传递给前端
2. 用户信息修改
   - **model**
     - 创建对应的用户信息修改BO实体类
   - **service**
     - 创建对应的用户修改方法，根据前端传入的type种类，对相应的字段进行修改
   - **api**
     - 新增对应的controller方法，接收前端传来的用户信息修改对象和修改字段类型，调用对应方法完成相关功能

### 整合Minio分布式数据库

1. 在linux环境下安装minio数据库，并完成数据库配置
2. **common**
   - 添加minio依赖
   - 添加对应的minioUtils工具类
3. **api**
   - 在yml中添加对应的数据库连接配置
   - 创建minio配置类
   - 编写对应的controller，测试文件上传功能

### 实现用户修改头像和背景

- **api**

  - 实现用户头像修改的controller

    接收用户id、修改属性的type、文件，修改数据库中的图片url

  - 限制用户文件上传大小

    修改yml中的文件大小配置即可，在common中添加异常处理函数中新增对应的功能。

### 限制一台手机登录

- **api**
  - 创建对应的拦截器
  - 注册对应拦截器路由

## 视频业务开发

### 使用uniclod，与前端关联

### 实现视频上传功能

- **model**
  - 构建对应的vlogBO

- **service**
  - 创建对应的vlogservice，编写创建vlog的方法

- **api**
  - 创建对应的controller方法


### 实现视频列表显示

在数据库中进行连表查询

- **mapper**
  - 创建对应的新的vlogMapperCustom接口，添加repsitory注解
  - 创建对应的xml文件，修改映射配置，编写对应的SQL语句
- **model**
  - 创建新的IndexVlogVO，来接受数据库的查询结果
- **service**
  - 新增查询视频列表的功能
- **api**
  - 新增查询视频列表的controller

### 实现视频详情页展示

- **mapper**
  - 在新的vlogMapperCustom接口中新增对应的方法
  - 创建对应的xml文件，修改映射配置，编写对应的SQL语句
- **service**
  - 在视频service中新增对应的功能
- **api**
  - 在视频的controller中新增对应的方法

### 实现视频私密和公开转换

### 展示我的私密视频/公共视频列表

## 粉丝业务开发

### 用户互关功能的实现

- **service**
  - 在粉丝service中新增对应的功能
- **api**
  - 在粉丝controller中新增对应的方法
  - 使用redis存储相关信息计数

### 用户取关功能的实现

- **service**
  - 在粉丝service中新增对应的功能
- **api**
  - 在粉丝controller中新增对应的方法
  - 删除redis存储计数信息

### 判断用户是否已经关注

- **service**

  - 在粉丝service中新增对应的功能

- **api**

  - 在粉丝controller中新增对应的方法
  - 在controller中添加对应的路由

  







































## 附录

> 对应源码：
>
> ​                                