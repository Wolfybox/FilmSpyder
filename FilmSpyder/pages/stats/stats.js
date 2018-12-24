Page({

  /**
   * 页面的初始数据
   */
  data: {
    // stock: "/images/stock.png",
    // trend: "http://img06.tooopen.com/images/20160818/tooopen_sy_175866434296.jpg",
    // top: "http://img06.tooopen.com/images/20160818/tooopen_sy_175866434296.jpg",
    // actor: "http://img06.tooopen.com/images/20160818/tooopen_sy_175866434296.jpg",
    // 屏幕宽高
    windowHeight: 0,
    windowWidth: 0 
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var that = this
    wx.getSystemInfo({
      success: function(res) {
        // 获取屏幕宽高
        console.log("windowHeight" + res.windowHeight)
        console.log("windowWidth" + res.windowWidth)
        that.setData({
          windowHeight: res.windowHeight,
          windowWidth: res.windowWidth
        })
      },
    })
  },

  toStock:function(){
    wx.navigateTo({
      url: '/pages/stock/stock',
    })
  },

  toTrend: function () {
    wx.navigateTo({
      url: '/pages/trend/trend',
    })
  },

  toTop: function () {
    wx.navigateTo({
      url: '/pages/top/top',
    })
  },

  toActor: function () {
    wx.navigateTo({
      url: '/pages/actor/actor',
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