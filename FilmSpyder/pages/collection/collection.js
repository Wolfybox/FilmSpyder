const app = getApp()

Page({

  /**
   * 页面的初始数据
   */
  data: {
    collection: [],
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var that = this
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
          collection: jsonData
        })
      }
    })
  },


  //点击收藏
  onCollectClick: function(e) {
    var id = e.currentTarget.id
    wx.navigateTo({
      url: '/pages/detail/detail?id=' + id,
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