const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    actor: {},
    subscribed: false
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function(options) {
    var that = this
    wx.request({
      url: 'https://filmspyder.cn/MoviesServer/searchactor.do',
      header: {
        "content-type": "application/json; charset=utf-8"
      },
      data: {
        search: options.name,
      },
      method: "POST",
      success: function(res) {
        console.log("status Code:" + res.statusCode)
        var jsonData = res.data
        console.log("演员详情返回数据:")
        console.log(jsonData)
        that.setData({
          actor: jsonData
        })

        //再次发送请求获取关注
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
            var curName = that.data.actor.actorname
            // 查重
            for (var i = 0; i < jsonData.length; i++) {
              var curItem = jsonData[i]
              if (curItem.actorname == curName) {
                that.setData({
                  subscribed: true
                })
              }
            }
          }
        })
      }
    })




  },

  onStatsTap: function() {
    wx.showModal({
      title: '跳转提示',
      content: '即将跳转到数据页',
      success: function(res) {
        if (res.confirm) {
          wx.switchTab({
            url: '/pages/stats/stats',
          })
        }
      }
    })

  },

  onSubTap: function() {
    wx.showLoading({
      title: '关注中',
    })
    var that = this
    // 翻转按钮状态
    that.setData({
      subscribed: !that.data.subscribed
    })
    if (that.data.subscribed) {
      wx.request({
        url: 'https://filmspyder.cn/MoviesServer/sub.do',
        header: {
          "content-type": "application/json; charset=utf-8"
        },
        data: {
          userid: app.globalData.uid,
          actorname: that.data.actor.actorname
        },
        method: "POST",
        success: function(res) {
          console.log("status Code:" + res.statusCode)
          var jsonData = res.data
          console.log("返回数据:")
          console.log(jsonData)
          wx.hideLoading()
          wx.showToast({
            title: '关注成功',
          })
        }
      })
    } else {
      wx.request({
        url: 'https://filmspyder.cn/MoviesServer/deletesub.do',
        header: {
          "content-type": "application/json; charset=utf-8"
        },
        data: {
          userid: app.globalData.uid,
          actorname: that.data.actor.actorname
        },
        method: "POST",
        success: function(res) {
          console.log("status Code:" + res.statusCode)
          var jsonData = res.data
          console.log("取关返回数据:")
          console.log(jsonData)
          wx.hideLoading()
          wx.showToast({
            title: '已取关',
          })
        }
      })

    }
  },

  onShareTap: function() {
    wx.showShareMenu({
      withShareTicket: true,
      success: function() {
        wx.showToast({
          title: '请点击胶囊分享',
        })
      },
      fail: function() {
        wx.showToast({
          title: '分享失败',
        })
      }
    })
  },

  onPlayTap: function(e) {
    var movieId = e.currentTarget.id
    console.log("点击参演电影ID：" + movieId)
    wx.navigateTo({
      url: '/pages/detail/detail?id=' + movieId,
    })
  },


  onShareAppMessage: function() {

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