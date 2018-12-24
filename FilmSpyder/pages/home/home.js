//home.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    hint: "搜名字",
    inputValue: "",
    instantInput: "",
    recommend: [],
    onAir: [],
    indicatorDots: true,
    autoplay: true,
    interval: 3000,
    duration: 500,
  },

  /**
   * 监听页面加载 ------------ 请求首页数据---------------------------------------
   */
  onLoad: function(options) {
    var that = this
    console.log("HOME UID:" + app.globalData.uid)
    // 请求推荐电影
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/recommend.do',
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
        console.log("推荐:")
        console.log(jsonData)
        that.setData({
          recommend: jsonData
        })
      },
      fail: function(res) {
        console.log("推荐请求失败：" + res.statusCode)
      }
    })
    // 请求正在热映数据
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/onair.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {},
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("HOME返回数据:")
        console.log(jsonData)
        that.setData({
          onAir: jsonData
        })
      }
    })

  },

  // 切换搜索模式
  switchMode: function() {
    // var that = this
    if (this.data.hint == "搜名字") {
      this.setData({
        hint: "搜类别"
      })
      wx.showToast({
        title: '按类别',
      })
    } else {
      this.setData({
        hint: "搜名字"
      })
      wx.showToast({
        title: '按名字',
      })
    }
  },

  // 查询结束
  query: function(e) {
    console.log(this.data.instantInput)
    wx.navigateTo({
      url: "/pages/search/search?queryContent=" + this.data.instantInput + "&hint=" + this.data.hint
    })
  },

  // 监听输入
  inputBind: function(input) {
    console.log(input.detail)
    // 记录用户输入
    this.setData({
      instantInput: input.detail.value
    })
  },

  // 点击推荐swiper
  onRecommendTap: function(e) {
    var id = e.currentTarget.id
    console.log("当前点击推荐ID:")
    console.log(id)
    wx.navigateTo({
      url: '/pages/detail/detail?id=' + id
    })
  },

  // 热门
  onHotClick: function() {
    wx.navigateTo({
      url: '/pages/hot/hot',
    })
  },
  // 排行
  onRankClick: function() {
    wx.navigateTo({
      url: '/pages/rank/rank',
    })
  },
  // 关注
  onSubscribeClick: function() {
    wx.navigateTo({
      url: '/pages/subscribe/subscribe',
    })
  },
  // 收藏
  onCollectClick: function() {
    wx.navigateTo({
      url: '/pages/collection/collection',
    })
  },
  // 正在热映电影
  onAirClick: function(e) {
    var id = e.currentTarget.id
    console.log("当前点击ID:")
    console.log(id)
    wx.navigateTo({
      url: '/pages/detail/detail?id=' + id
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