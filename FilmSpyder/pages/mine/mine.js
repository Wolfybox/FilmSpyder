var hour
var miniute
var seconds

function getCurTime() {
  var timeStamp = Date.parse(new Date)
  timeStamp = timeStamp / 1000
  console.log("当前时间戳为：" + timeStamp)
  var n = timeStamp * 1000
  var date = new Date(n)
  hour = date.getHours()
  miniute = date.getMinutes()
  seconds = date.getSeconds()
  console.log("当前时间" + hour + "/" + miniute + "/" + seconds)
}

var app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    greeting: "早上好",
    userNickname: "",
    userAvatarUrl: "",
    collect: [],
    subscribe: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {


  },

  // 设置问候语
  setGreeting: function() {
    getCurTime()
    var curTime = hour + "" + miniute + "" + seconds
    var time = parseInt(curTime)
    console.log("时间转换：" + time)
    var greet = ""
    if (time > 40000 && time <= 114000) {
      greet = "早上好"
    }
    if (time > 114000 && time <= 124000) {
      greet = "中午好"
    }
    if (time > 124000 && time <= 174000) {
      greet = "下午好"
    }
    if ((time > 174000 && time < 240000) || (time >= 0 && time <= 40000)) {
      greet = "晚上好"
    }
    this.setData({
      greeting: greet
    })
  },

  //点击收藏
  onCollectClick: function(e) {
    var id = e.currentTarget.id
    wx.navigateTo({
      url: '/pages/detail/detail?id=' + id,
    })
  },

  onSubscribeClick: function(e) {
    var name = e.currentTarget.dataset.name
    wx.navigateTo({
      url: '/pages/bio/bio?name=' + name,
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function() {
    var info = app.globalData.userInfo
    this.setData({
      userAvatarUrl: info['avatarUrl'],
      userNickname: info['nickName']
    })
    // 设置问候语
    this.setGreeting()
    var that = this
    console.log("个人页UID：" + app.globalData.uid)
    // 获取收藏
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/returncollect.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        userid: app.globalData.uid
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回收藏:")
        console.log(jsonData)
        that.setData({
          collect: jsonData
        })
      }
    })

    //获取关注
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/returnsub.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        userid: app.globalData.uid
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回关注:")
        console.log(jsonData)
        that.setData({
          subscribe: jsonData
        })
      }
    })

  },

  onTestTap: function() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function() {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  }
})