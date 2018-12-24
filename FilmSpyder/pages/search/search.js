Page({

  /**
   * 页面的初始数据
   */
  data: {
    initValue: "",
    instantInput: "",
    resultList: [],
    hint: "",
    hasResult: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(query) {
    // 获取从首页传来的用户输入
    console.log(query.queryContent)
    var that = this
    // 设置搜索框初始信息
    that.setData({
      initValue: query.queryContent,
      instantInput: query.queryContent,
      hint: query.hint
    })
    // 执行一次搜索
    // that.searchByName()
    that.doSearch()
  },

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

  doSearch: function() {
    if (this.data.hint == "搜名字") {
      this.searchByName()
      console.log("按名搜索")
    } else {
      this.searchByClass()
      console.log("按类搜索")
    }
  },

  // 输入结束，按下放大镜或回车
  query: function(e) {
    console.log(this.data.instantInput)
    var that = this
    that.doSearch()
  },

  // 监听输入
  inputBind: function(input) {
    console.log(input.detail)
    // 记录用户输入
    this.setData({
      instantInput: input.detail.value
    })
  },

  // 按名字搜索
  searchByName: function() {
    var that = this
    wx.showLoading({
      title: '搜索中',
    })
    if (that.data.instantInput != "") {
      wx.request({
        url: 'https://filmspyder.cn/MoviesServer/searchbyname.do',
        header: {
          "content-type": "application/json; charset=utf-8"
        },
        data: {
          search: this.data.instantInput
        },
        method: "POST",
        success: function(res) {
          console.log("status Code:" + res.statusCode)
          var jsonData = res.data
          console.log("返回数据:")
          console.log(jsonData)
          that.checkEmpty(jsonData)
          that.setData({
            resultList: jsonData
          })
          // wx.hideLoading()
        },
        fail: function() {
          wx.showToast({
            title: '请检查网络连接',
          })
          wx.hideLoading()
        }
      })
    }
  },

  checkEmpty: function(jsonData) {
    if (jsonData.length != 0) {
      this.setData({
        hasResult: true
      })
    } else {
      this.setData({
        hasResult: false
      })
    }
    wx.hideLoading()
  },

  //按类别搜索
  searchByClass: function() {
    var that = this
    wx.showLoading({
      title: '搜索中',
    })
    if (that.data.instantInput != "") {
      wx.request({
        url: 'https://filmspyder.cn/MoviesServer/searchbyclass.do',
        header: {
          "content-type": "application/json; charset=utf-8"
        },
        data: {
          search: this.data.instantInput
        },
        method: "POST",
        success: function(res) {
          console.log("status Code:" + res.statusCode)
          var jsonData = res.data
          console.log("返回数据:")
          console.log(jsonData)
          that.checkEmpty(jsonData)
          that.setData({
            resultList: jsonData
          })
        },
        fail: function() {
          wx.showToast({
            title: '请检查网络连接',
          })
          wx.hideLoading()
        }
      })
    }
  },

  onResultClick: function(e) {
    var curID = e.currentTarget.id
    console.log("结果点击ID：")
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