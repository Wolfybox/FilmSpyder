Page({

  /**
   * 页面的初始数据
   */
  data: {
    filmList: []
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    // 设置标题
    wx.setNavigationBarTitle({
      title: options.title,
    })
    var filmList = JSON.parse(options.filmList)
    console.log("传参列表：")
    console.log(filmList)
    this.setData({
      filmList: filmList
    })
  }, 

  // 电影点击事件
  onFilmClick: function(e) {
    var curID = e.currentTarget.id
    console.log(curID)
    wx.navigateTo({
      url: '/pages/detail/detail?id=' + curID
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