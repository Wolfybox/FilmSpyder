# FilmSpyder
提供实时电影资讯，帮助您的分析电影数据，您的电影小助手  

【使用须知】  
本项目为HITSZ 2018 软件工程项目 基于微信小程序开发 仅供学习和交流  

【DEMO视频地址】  
百度云盘：https://pan.baidu.com/s/1XHPqGhqY6cRswSQONKI39g  

【项目结构说明】  
（根目录）  
—— FilmSpyder → 前端代码  
—— MoviesServer → 后台服务器代码  
—— maoyan-update → 爬虫代码  
—— maoyan → 爬虫代码  


【项目依赖环境】  
前端：微信开发者工具或其他小程序开发工具  
后台：tomcat+java+Spring+Hibernate+Gradle   
爬虫：python3+scrapy   

【实现功能】  
1.基础功能：  
（1）登录/注册 —— 微信授权登录 + 数据加密  
（2）数据可视化 —— 小程序可视化开源库 → WxChart  
（3）图表导出和保存—— 长按保存（保存技术是Canvas绘制）  
（4）数据动态爬取 —— Scrapy爬虫框架定时爬取 + 反爬技术  
2.扩展功能：  
（1）电影按名字或按类别搜索 —— 模糊搜索  
（2）电影个性化推荐和热映推荐 —— 数据冷启动 + Bandit算法 + SVD++推荐  
（3）电影收藏  
（4）演员关注  
（5）电影和演员详情查看  
（6）电影个性化排行  
（7）页面分享   

【新增技术】  
1. 数据可视化功能的优化：服务器增加缓存机制，提前统计和计算，使生成图表的速度大大提高  

【使用方法】  
打开手机微信扫描二维码使用FilmSpyder：  
![image](https://github.com/Wolfybox/FilmSpyder/blob/master/ReadMEImage/XS.jpg)  
