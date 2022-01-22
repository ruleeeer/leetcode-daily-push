# leetcode-daily-push
一个每日早晨自动推送LeetCode每日一题的工具
+ 你可以使用`http://daily-code.ruleeeer.cn:1024/subscribe/${your_email}` 来订阅该服务，邮件大概会在每天早晨8点发出
+ 如果想自己搭建该服务可以`clone`项目然后补充重要的`mysql`和`email`配置文件

### 以后可能支持的选项
+ 自定义时间发送邮件（但是这需要一个前端界面）

### 已知的bad eggs
+ 部分同层调用
+ reactive redis `get()`参数为一个不存在的`key`时，永远不会回调`subscribe()`（原因未知，可能是我使用的问题），我不得不使用`mget()`来代替`get()`

### 可能的问题
+ 我暂时使用的发信邮箱是微信企业邮箱，但是由于发信量较高可能在人数较多的情况下出现发信限制，暂时的设想是使用邮件池轮询发信邮箱，个别邮箱出现发信限制后自动摘除该信箱


### 关于隐私方面
+ 该服务只会记载你订阅的邮箱和订阅的时间，不会记录任何其他数据（包括但不限于IP等信息），取消订阅后会直接物理删除数据而非逻辑删除

### 取消订阅
`http://daily-code.ruleeeer.cn:1024/unsubscribe/${your_email}`

### 效果
![WemYzpG](https://user-images.githubusercontent.com/70385062/136496495-ec8025e1-dad1-4a23-b4f6-503dfa3cb84f.png)
