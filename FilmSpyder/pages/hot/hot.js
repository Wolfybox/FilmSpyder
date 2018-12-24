const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    tabMenu: [{
        name: "院线电影",
        checked: true,
        value: "0"
      },
      {
        name: "即将上映",
        checked: false,
        value: "1"
      },
      {
        name: "12月观影指南",
        checked: false,
        value: "2"
      }
    ],

    pageIndex: 1,
    pageSize: 50,
    cursorInPage: 0,
    // hasMoreData: true,
    curPage: [],
    hotFilm: [],
  },

  onHotFilmClick:function(e){
    var curID = e.currentTarget.id
    console.log(curID)
    wx.navigateTo({
      url: '/pages/detail/detail?id='+curID
    })
  },
  // 菜单栏切换监听
  onTabChange: function(e) {
    console.log("当前菜单选项：" + e.detail.value);
    var items = this.data.tabMenu;
    for (var i = 0, len = items.length; i < len; i++) {
      items[i].checked = (items[i].value == e.detail.value)
    }
    this.setData({
      tabMenu: items
    });
  },

  /**
   * 页面加载--------------加载一次数据
   */
  onLoad: function(options) {
    this.getNextPage() // 获取一页  --- 50    
    // this.loadMoreData() //将数据加载到页面中
  },

  // 列表充入更多数据
  loadMoreData: function() {
    var that = this
    // 如果当前页还有数据
    if (that.data.pageIndex <= 40) {
      var start = that.data.cursorInPage
      var tempList = []
      var i
      for (i = start; i < start + 10; i++) {
        if (i >= that.data.pageSize) {
          that.getNextPage()
          break;
        }
        tempList.push(that.data.curPage[i])
      }
      // 向列表中拼接数据
      var newList = that.data.hotFilm.concat(tempList)
      that.setData({
        hotFilm: newList,
        cursorInPage: i
      })
    } else {
      wx.showToast({
        title: '没有更多数据了',
      })
    }

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
    this.loadMoreData()
  },

  // 获取下一页
  getNextPage: function() {
    var that = this
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/moviebyrate.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        page: that.data.pageIndex,
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        // var jsonData = JSON.stringify(res.data)
        var jsonData = res.data
        console.log("页码:")
        console.log(that.data.pageIndex)
        console.log("返回数据:")
        console.log(jsonData)
        var newPageIndex = that.data.pageIndex + 1
        //页码递增
        that.setData({
          pageIndex: newPageIndex,
          cursorInPage: 0, //页内游标归零
          curPage: jsonData
        })
        //第一次添加数据
        if (that.data.hotFilm.length == 0) {
          that.loadMoreData()
        }
      }
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
   * 用户点击右上角分享
   */
  onShareAppMessage: function() {

  }
})