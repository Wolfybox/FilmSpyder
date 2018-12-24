var app = getApp()

Page({
  data: {
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    userAvatarUrl: "",
    userNickname: "",
  },

  onLoad: function() {
    var that = this
    // 查看是否授权
    wx.getSetting({
      success(res) {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称
          wx.getUserInfo({
            success: function(res) {
              console.log("用户已授权，用户信息：")
              console.log(res.userInfo)
              var info = res.userInfo
              app.globalData.userInfo = info //记录用户信息为全局量
              that.setData({
                userAvatarUrl: info['avatarUrl'],
                userNickname: info['nickName'],
              })
              that.login()
            }
          })
        } else {
          console.log("用户未预先授权")
        }
      }
    })
  },

  // 点击登录键
  bindGetUserInfo(e) {
    console.log("用户点击登录")
    var res = e.detail.userInfo
    console.log(res)
    if (res != undefined) {
      this.setData({
        userAvatarUrl: res['avatarUrl'],
        userNickname: res['nickName']
      })
      app.globalData.userInfo = res //记录用户信息为全局量
      this.login()
    }
  },

  //完成登录，向服务器发送个人信息
  login: function() {
    var that = this
    wx.switchTab({
      url: '/pages/home/home',
    })
    wx.showToast({
      title: '登录成功',
    })
    // 向服务器发送用户信息 
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/register.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        username: that.data.userNickname,
        phone: "1"
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("返回数据:")
        console.log(jsonData)
        app.globalData.uid = jsonData.id
      }
    })
    console.log("登录完成，跳转到HOME")
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